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

import avro.demo.model.Click;
import avro.demo.serializers.JsonDeserializer;
import avro.demo.serializers.JsonSerializer;

@Configuration
public class KafkaConfiguration {

	@Bean
	public Serializer<String> stringKeySerializer() {
		return new StringSerializer();
	}

	@Bean
	public Serializer<Click> clickSerializer() {
		return new JsonSerializer<Click>();
	}

	@Bean
	public Deserializer<String> stringKeyDeserializer() {
		return new StringDeserializer();
	}

	@Bean
	public Deserializer<Click> clickDeserializer() {
		return new JsonDeserializer<Click>(Click.class);
	}

	@Bean
	public KafkaProducer<String, Click> clickProducer(KafkaProperties kafkaProperties) {
		KafkaProducer<String, Click> producer = null;
		Properties kafkaProps = new Properties();
		kafkaProps.put("bootstrap.servers", kafkaProperties.getBootstrap());
//		producer = new KafkaProducer<String, Click>(kafkaProps, stringKeySerializer(), clickSerializer());
		return producer;
	}

	@Bean
	public KafkaConsumer<String, Click> workUnitConsumer(KafkaProperties kafkaProperties) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaProperties.getBootstrap());
		props.put("group.id", kafkaProperties.getGroup());
		KafkaConsumer<String, Click> consumer = new KafkaConsumer<String, Click>(props, stringKeyDeserializer(), clickDeserializer());
		consumer.subscribe(Collections.singletonList(kafkaProperties.getTopic()));
		return consumer;
	}

}
