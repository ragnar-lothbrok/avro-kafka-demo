package avro.demo.multipleConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import avro.demo.model.ClickRetry;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class ConsumerThread implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

	private KafkaStream<String, ClickRetry> m_stream;
	private int m_threadNumber;
	private KafkaPublisherService kafkaPublisherService;

	public ConsumerThread(KafkaStream<String, ClickRetry> m_stream, int m_threadNumber, KafkaPublisherService kafkaPublisherService) {
		super();
		this.m_stream = m_stream;
		this.m_threadNumber = m_threadNumber;
		this.kafkaPublisherService = kafkaPublisherService;
	}

	@Override
	public void run() {
		try {
			ClickRetry message = null;
			logger.info("started listener thread: {}", m_threadNumber);
			ConsumerIterator<String, ClickRetry> it = m_stream.iterator();
			while (it.hasNext()) {
				MessageAndMetadata<String, ClickRetry> msg = it.next();
				try {
					if (msg != null) {
						message = msg.message();
						Long offSet = msg.offset();
						kafkaPublisherService.dispatch(message);
						logger.info("receive message by " + m_threadNumber + " : " + message);
					} else {
						logger.info("receive message by " + m_threadNumber + " : " + msg);
					}
				} catch (Exception ex) {
					logger.error("error processing queue for message: " + message, ex);
				}
			}
			logger.info("Shutting down listener thread: " + m_threadNumber);
		} catch (Exception ex) {
			logger.error("error:", ex);
		}
	}

}
