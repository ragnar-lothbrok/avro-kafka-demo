package avro.demo.model;

import java.io.Serializable;

public class Click implements Serializable {

	private static final long serialVersionUID = -3685011001734654135L;

	private String trackerId;
	private Boolean clicked;

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

}
