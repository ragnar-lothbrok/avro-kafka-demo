package avro.demo.multipleConsumer;

import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import avro.demo.config.KafkaProperties;
import avro.demo.model.ClickRetry;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

@Service
public class KafkaPublisherService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisherService.class);

	@Autowired
	@Qualifier(
		value = "feedKafkaProducer")
	private Producer<String, ClickRetry> feedKafkaProducer;

	@Autowired
	private KafkaProperties kafkaroperties;

	public boolean dispatch(ClickRetry message) {
		ClickRetry clickRetry = new ClickRetry();
		for (int i = 0; i < 10; i++) {
			clickRetry.setSupc(UUID.randomUUID().toString());
			clickRetry.setPogId(new Random().nextLong());
			try {
				KeyedMessage<String, ClickRetry> keyMsg = new KeyedMessage<String, ClickRetry>(kafkaroperties.getTopic(), clickRetry.getPogId() + "",
						clickRetry);
				this.feedKafkaProducer.send(keyMsg);
				LOGGER.info("topic = {}, data = {}", kafkaroperties.getTopic(), keyMsg);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}
}
