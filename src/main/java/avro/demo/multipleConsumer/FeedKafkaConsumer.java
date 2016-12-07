package avro.demo.multipleConsumer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import avro.demo.config.KafkaProperties;
import avro.demo.model.FeedRetry;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.Decoder;

@Service
public class FeedKafkaConsumer {

	private static final Logger logger = LoggerFactory.getLogger(FeedKafkaConsumer.class);

	private ExecutorService executor;

	private ConsumerConnector consumer;

	@Autowired
	private KafkaProperties kafkaProperties;

	@Autowired
	@Qualifier("stringDecoder")
	private Decoder<String> stringDecoder;

	@Autowired
	@Qualifier(
		value = "feedDecoder")
	private Decoder<FeedRetry> feedDecoder;

	private Map<String, Map<String, KafkaStream<String, FeedRetry>>> topicStreamMap = new ConcurrentHashMap<String, Map<String, KafkaStream<String, FeedRetry>>>();

	@Autowired
	public FeedKafkaConsumer(KafkaProperties kafkaProperties) {
		this.kafkaProperties = kafkaProperties;
		executor = Executors.newFixedThreadPool(kafkaProperties.getPartitionCount());
	}

	@PostConstruct
	public void createConsumerConnector() {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(kafkaProperties.getBootstrap(), kafkaProperties.getGroup()));
		createMultipleConsumerStreams();
	}

	private static ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "5000");
		props.put("zookeeper.sync.time.ms", "250");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	public void createMultipleConsumerStreams() {
		int partitionCount = kafkaProperties.getPartitionCount();
		Map<String, Integer> topicThreadCountMap = new HashMap<String, Integer>();

		// Define thread count for each topic
		topicThreadCountMap.put(kafkaProperties.getTopic(), new Integer(partitionCount));

		// Here we have used a single topic but we can also add
		// multiple topics to topicCount MAP
		Map<String, List<KafkaStream<String, FeedRetry>>> consumerStreamsMap = consumer.createMessageStreams(topicThreadCountMap, stringDecoder,
				feedDecoder);

		List<KafkaStream<String, FeedRetry>> streamList = consumerStreamsMap.get(kafkaProperties.getTopic());

		// Creating an object messages consumption streams
		int count = 0;
		for (final KafkaStream<String, FeedRetry> stream : streamList) {
			final String streamName = kafkaProperties.getTopic() + "-" + kafkaProperties.getGroup() + "-" + count + "-"
					+ Calendar.getInstance().getTimeInMillis();
			executor.submit(new ConsumerThread(stream, streamName, this, kafkaProperties.getTopic()));
			addStream(kafkaProperties.getTopic(), streamName, kafkaProperties.getPartitionCount(), stream);
			count++;
		}
	}

	boolean temp = true;

	protected void shutdownConsumer() {
		try {
			if (consumer != null)
				consumer.shutdown();
		} catch (Exception e) {
			logger.error("Interrupted during shutdown consumer, exiting uncleanly {} ", e);
		}
	}

	protected void shutdownExecutor() {
		try {
			if (executor != null)
				executor.shutdown();
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				logger.info("Timed out waiting for consumer threads to shut down, exiting uncleanly");
			}
		} catch (InterruptedException e) {
			logger.error("Interrupted during shutdown executor, exiting uncleanly {} ", e);
		}
	}

	public Boolean addStream(String topic, String streamName, int numOfThreads, KafkaStream<String, FeedRetry> stream) {
		Boolean isAdded = false;
		synchronized (topic) {
			if (topicStreamMap.size() < numOfThreads) {
				if (topicStreamMap.get(topic) == null) {
					Map<String, KafkaStream<String, FeedRetry>> streamMap = new ConcurrentHashMap<String, KafkaStream<String, FeedRetry>>();
					streamMap.put(streamName, stream);
					topicStreamMap.put(topic, streamMap);
					isAdded = true;
				}
				if (topicStreamMap.get(topic).get(streamName) == null) {
					topicStreamMap.get(topic).put(streamName, stream);
					isAdded = true;
				}
			} else {
				logger.error("Can not create Kafka stream. Max Size reached {} ", topicStreamMap.size());
			}
		}
		logger.info("=========>>>>>>> Consumer Cache configuration " + topicStreamMap);
		return isAdded;
	}

	public Boolean removeStream(String topic, String streamName) {
		Boolean isRemoved = false;
		synchronized (topic) {
			if (topicStreamMap.size() != 0 && topicStreamMap.get(topic) != null && topicStreamMap.get(topic).get(streamName) != null) {
				topicStreamMap.get(topic).remove(streamName);
				isRemoved = true;
			} else {
				logger.error("Can not create Kafka stream. Limit is zero {} ", topicStreamMap.size());
				isRemoved = false;
			}
		}
		if (topicStreamMap.get(topic).size() <= 2) {
			shutdownConsumer();
			createConsumerConnector();
		}
		logger.info("=========<<<<<< Consumer Cache configuration " + topicStreamMap);
		return isRemoved;
	}

}
