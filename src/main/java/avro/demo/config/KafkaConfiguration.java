package avro.demo.config;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import avro.demo.model.Feed;
import avro.demo.serializers.JsonDeserializer;
import avro.demo.serializers.JsonSerializer;

@Configuration
public class KafkaConfiguration {

	@Bean
	public Serializer<String> stringKeySerializer() {
		return new StringSerializer();
	}

	@Bean
	public Serializer<Feed> clickSerializer() {
		return new JsonSerializer<Feed>();
	}

	@Bean
	public Deserializer<String> stringKeyDeserializer() {
		return new StringDeserializer();
	}

	@Bean
	public Deserializer<Feed> clickDeserializer() {
		return new JsonDeserializer<Feed>(Feed.class);
	}

	@Bean
	public KafkaProducer<String, Feed> clickProducer(KafkaProperties kafkaProperties) {
		KafkaProducer<String, Feed> producer = null;
		Properties kafkaProps = new Properties();
		kafkaProps.put("bootstrap.servers", kafkaProperties.getBootstrap());
//		producer = new KafkaProducer<String, Click>(kafkaProps, stringKeySerializer(), clickSerializer());
		return producer;
	}

	@Bean
	public KafkaConsumer<String, Feed> workUnitConsumer(KafkaProperties kafkaProperties) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaProperties.getBootstrap());
		props.put("group.id", kafkaProperties.getGroup());
		KafkaConsumer<String, Feed> consumer = new KafkaConsumer<String, Feed>(props, stringKeyDeserializer(), clickDeserializer());
		consumer.subscribe(Collections.singletonList(kafkaProperties.getTopic()));
		return consumer;
	}

}
