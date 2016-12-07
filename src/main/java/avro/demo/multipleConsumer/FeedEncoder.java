package avro.demo.multipleConsumer;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

@Component(
	value = "feedEncoder")
public class FeedEncoder<ClickRetry> implements Encoder<ClickRetry> {

	private static final Logger logger = LoggerFactory.getLogger(FeedDecoder.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	public FeedEncoder() {

	}

	public FeedEncoder(VerifiableProperties props) {

	}

	@Override
	public byte[] toBytes(ClickRetry data) {
		try {
			return mapper.writeValueAsString(data).getBytes();
		} catch (Exception e) {
			logger.error("Exception occured {}", e);
		}
		return null;
	}

}
