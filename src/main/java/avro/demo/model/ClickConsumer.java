package avro.demo.model;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickConsumer implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ClickConsumer.class);

	private final KafkaConsumer<String, Click> clickConsumer;

	public ClickConsumer(KafkaConsumer<String, Click> clickConsumer) {
		this.clickConsumer = clickConsumer;
	}

	public void run() {
		try {
			while (true) {
				ConsumerRecords<String, Click> records = this.clickConsumer.poll(100);
				for (ConsumerRecord<String, Click> record : records) {
					log.info("consuming from topic = {}, partition = {}, offset = {}, key = {}, value = {}", record.topic(), record.partition(),
							record.offset(), record.key(), record.value());

				}
			}
		} finally {
			this.clickConsumer.close();
		}
	}
}
