package avro.demo.multipleConsumer;

import org.springframework.stereotype.Component;

import kafka.producer.DefaultPartitioner;
import kafka.utils.VerifiableProperties;

@Component
public class CustomPartitioner extends DefaultPartitioner {

	public CustomPartitioner(VerifiableProperties props) {
		super(props);
	}

	@Override
	public int partition(Object key, int numPartitions) {
		if (key != null) {
			return (key+"").hashCode() % numPartitions;
		} else {
			return 1;
		}
	}
}
