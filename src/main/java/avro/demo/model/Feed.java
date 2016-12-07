package avro.demo.model;

import java.io.Serializable;

public class Feed implements Serializable {

	private static final long serialVersionUID = -3685011001734654135L;

	private String trackerId;
	private Boolean clicked;
	private Long pogId;
	private String supc;

	@Override
	public String toString() {
		return "Click [trackerId=" + trackerId + ", clicked=" + clicked + "]";
	}

	public String getTrackerId() {
		return trackerId;
	}

	public void setTrackerId(String trackerId) {
		this.trackerId = trackerId;
	}

	public Boolean getClicked() {
		return clicked;
	}

	public void setClicked(Boolean clicked) {
		this.clicked = clicked;
	}

	public Long getPogId() {
		return pogId;
	}

	public void setPogId(Long pogId) {
		this.pogId = pogId;
	}

	public String getSupc() {
		return supc;
	}

	public void setSupc(String supc) {
		this.supc = supc;
	}

}
