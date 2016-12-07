package avro.demo.multipleConsumer;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import avro.demo.config.KafkaProperties;
import avro.demo.model.ClickRetry;
import kafka.javaapi.producer.Producer;
import kafka.serializer.Decoder;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

/**
 * This will fetch data from events and will post to a Topic
 * 
 * @author raghunandangupta
 *
 */
@Configuration
public class FeedKafkaProducerConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(FeedKafkaProducerConfiguration.class);

	@Bean
	public VerifiableProperties verifiableProperties(){
		return new kafka.utils.VerifiableProperties();
	}
	@Bean(
		name = "stringDecoder")
	public Decoder<String> stringDecoder() {
		return new StringDecoder(null);
	}

	@Autowired
	private KafkaProperties kafkaProperties;

	@Bean(
		value = "feedKafkaProducer")
	public Producer<String, ClickRetry> feedKafkaProducer() {
		Properties kafkaProps = new Properties();
		kafkaProps.put("metadata.broker.list", kafkaProperties.getBrokers());
		kafkaProps.put("key.serializer.class", "kafka.serializer.StringEncoder");
		kafkaProps.put("serializer.class", "avro.demo.multipleConsumer.FeedEncoder");
		kafkaProps.put("partitioner.class", "avro.demo.multipleConsumer.CustomPartitioner");
		kafkaProps.put("request.required.acks", "1");
		kafkaProps.put("producer.type", "sync");
		kafka.producer.ProducerConfig producerConfig = new kafka.producer.ProducerConfig(kafkaProps);
		return new Producer<String, ClickRetry>(producerConfig);
	}

}
