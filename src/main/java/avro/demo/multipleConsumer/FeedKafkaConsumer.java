package avro.demo.multipleConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import avro.demo.model.ClickRetry;
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
	private Decoder<ClickRetry> feedDecoder;

	@Autowired
	private KafkaPublisherService kafkaPublisherService;

	@Autowired
	public FeedKafkaConsumer(KafkaProperties kafkaProperties) {
		this.kafkaProperties = kafkaProperties;
	}

	@PostConstruct
	public void createConsumers() {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(kafkaProperties.getBootstrap(), kafkaProperties.getGroup()));
		createMultipleConsumers(kafkaProperties.getPartitionCount());
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

	public void createMultipleConsumers(int partitionCount) {

		Map<String, Integer> topicMap = new HashMap<String, Integer>();

		// Define thread count for each topic
		topicMap.put(kafkaProperties.getTopic(), new Integer(partitionCount));

		// Here we have used a single topic but we can also add
		// multiple topics to topicCount MAP
		Map<String, List<KafkaStream<String, ClickRetry>>> consumerStreamsMap = consumer.createMessageStreams(topicMap, stringDecoder,
				feedDecoder);

		List<KafkaStream<String, ClickRetry>> streamList = consumerStreamsMap.get(kafkaProperties.getTopic());

		// Launching the thread pool
		executor = Executors.newFixedThreadPool(partitionCount);

		// Creating an object messages consumption
		int count = 0;
		for (final KafkaStream<String, ClickRetry> stream : streamList) {
			final int threadNumber = count;
			executor.submit(new ConsumerThread(stream, threadNumber, kafkaPublisherService));
			count++;
		}
	}

	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
		try {
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				logger.info("Timed out waiting for consumer threads to shut down, exiting uncleanly");
			}
		} catch (InterruptedException e) {
			logger.error("Interrupted during shutdown, exiting uncleanly");
		}
	}

}
