package avro.demo.services;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import avro.demo.config.KafkaProperties;
import avro.demo.model.Feed;

@Service
public class ProducerService {

	@Autowired
	private KafkaProducer<String, Feed> clickProducer;

	@Autowired
	private KafkaProperties kafkaroperties;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class);

	public boolean dispatch(Feed message) {
		ProducerRecord<String, Feed> record = new ProducerRecord<String, Feed>(kafkaroperties.getTopic(), message.getTrackerId(), message);
		try {
			RecordMetadata recordMetadata = this.clickProducer.send(record).get();
			LOGGER.info("topic = {}, partition = {}, offset = {}, workUnit = {}", recordMetadata.topic(), recordMetadata.partition(),
					recordMetadata.offset(), message);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
