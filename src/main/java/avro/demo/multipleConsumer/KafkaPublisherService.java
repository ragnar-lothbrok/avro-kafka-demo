package avro.demo.multipleConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import avro.demo.config.KafkaProperties;
import avro.demo.model.FeedRetry;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

@Service
public class KafkaPublisherService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisherService.class);

	@Autowired
	@Qualifier(
		value = "feedKafkaProducer")
	private Producer<String, FeedRetry> feedKafkaProducer;

	@Autowired
	private KafkaProperties kafkaroperties;

	public boolean dispatch(FeedRetry message) {
		if(message != null){
			try {
				KeyedMessage<String, FeedRetry> keyMsg = new KeyedMessage<String, FeedRetry>(kafkaroperties.getTopic(), message.getPogId() + "",
						message);
				this.feedKafkaProducer.send(keyMsg);
				LOGGER.info("topic = {}, data = {}", kafkaroperties.getTopic(), keyMsg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}
}
