package avro.demo.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import avro.demo.model.Feed;
import avro.demo.model.ClickConsumer;

@Service
public class KafkaConsumerListener implements SmartLifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerListener.class);

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private final KafkaConsumer<String, Feed> kafkaConsumer;

	private volatile boolean running = false;

	public KafkaConsumerListener(KafkaConsumer<String, Feed> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

	public void start() {
		ClickConsumer clickConsumer = new ClickConsumer(this.kafkaConsumer);
		executorService.submit(clickConsumer);
		this.running = true;
	}

	public void stop() {
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isAutoStartup() {
		return true;
	}

	public void stop(Runnable callback) {

	}

	public int getPhase() {
		return 0;
	}

}
