package avro.demo.multipleConsumer;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import avro.demo.model.FeedRetry;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

@Component(
	value = "feedDecoder")
public class FeedDecoder<T> implements Decoder<FeedRetry> {

	private static final Logger logger = LoggerFactory.getLogger(FeedDecoder.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public FeedRetry fromBytes(byte[] bytes) {
		try {
			return mapper.readValue(bytes, FeedRetry.class);
		} catch (Exception e) {
			logger.error("Json processing failed for object {}", e);
		}
		return null;
	}

	public FeedDecoder() {

	}

	public FeedDecoder(VerifiableProperties props) {

	}

}
