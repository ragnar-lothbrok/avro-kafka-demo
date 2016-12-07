package avro.demo.multipleConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import avro.demo.model.FeedRetry;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class ConsumerThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

	private KafkaStream<String, FeedRetry> m_stream;
	private String streamName;
	private String topicName;
	private FeedKafkaConsumer feedKafkaConsumer;

	public ConsumerThread(KafkaStream<String, FeedRetry> m_stream, String streamName, FeedKafkaConsumer feedKafkaConsumer, String topicName) {
		super();
		this.m_stream = m_stream;
		this.streamName = streamName;
		this.feedKafkaConsumer = feedKafkaConsumer;
		this.topicName = topicName;
	}

	@Override
	public void run() {
		try {
			FeedRetry message = null;
			logger.info("started listening stream:  " + streamName);
			ConsumerIterator<String, FeedRetry> it = m_stream.iterator();
			while (it.hasNext()) {
				MessageAndMetadata<String, FeedRetry> msg = it.next();
				try {
					if (msg != null) {
						message = msg.message();
					}
				} catch (Exception ex) {
					logger.error("error processing queue for message {} ", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("Exception on message {}", ex);
		} finally {
			try {
				if (this.m_stream != null) {
					this.m_stream.clear();
					feedKafkaConsumer.removeStream(topicName, streamName);
				}
			} catch (Exception e) {
				logger.error("Exception while unregistering {} ", e);
			}
		}
		logger.info("Shutting down listener stream: " + streamName);
	}
}
