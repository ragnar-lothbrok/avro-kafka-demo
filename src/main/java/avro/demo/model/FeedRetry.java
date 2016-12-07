package avro.demo.model;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class FeedRetry extends Feed {

	private static final long serialVersionUID = 1L;

	private Integer googleFailedCount;
	private Integer facebookFailedCount;
	private Integer mongoFailedCount;
	private Boolean facebookEnable;
	private Boolean googleEnable;
	private Boolean mongoEnable;

	public Integer getGoogleFailedCount() {
		return googleFailedCount;
	}

	public void setGoogleFailedCount(Integer googleFailedCount) {
		this.googleFailedCount = googleFailedCount;
	}

	public Integer getFacebookFailedCount() {
		return facebookFailedCount;
	}

	public void setFacebookFailedCount(Integer facebookFailedCount) {
		this.facebookFailedCount = facebookFailedCount;
	}

	public Integer getMongoFailedCount() {
		return mongoFailedCount;
	}

	public void setMongoFailedCount(Integer mongoFailedCount) {
		this.mongoFailedCount = mongoFailedCount;
	}

	public Boolean getFacebookEnable() {
		return facebookEnable;
	}

	public void setFacebookEnable(Boolean facebookEnable) {
		this.facebookEnable = facebookEnable;
	}

	public Boolean getGoogleEnable() {
		return googleEnable;
	}

	public void setGoogleEnable(Boolean googleEnable) {
		this.googleEnable = googleEnable;
	}

	public Boolean getMongoEnable() {
		return mongoEnable;
	}

	public void setMongoEnable(Boolean mongoEnable) {
		this.mongoEnable = mongoEnable;
	}

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		FeedRetry clickRetry = new FeedRetry();
		for (int i = 0; i < 10; i++) {
			clickRetry.setSupc(UUID.randomUUID().toString());
			clickRetry.setPogId(new Random().nextLong());
			System.out.println(new ObjectMapper().writeValueAsString(clickRetry));
		}
	}

	@Override
	public String toString() {
		return "ClickRetry [googleFailedCount=" + googleFailedCount + ", facebookFailedCount=" + facebookFailedCount + ", mongoFailedCount="
				+ mongoFailedCount + ", facebookEnable=" + facebookEnable + ", googleEnable=" + googleEnable + ", mongoEnable=" + mongoEnable
				+ ", toString()=" + super.toString() + ", getTrackerId()=" + getTrackerId() + ", getClicked()=" + getClicked() + ", getPogId()="
				+ getPogId() + ", getSupc()=" + getSupc() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
