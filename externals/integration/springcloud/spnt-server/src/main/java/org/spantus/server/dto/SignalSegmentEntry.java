package org.spantus.server.dto;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.spantus.core.beans.SignalSegment;
import org.springframework.data.annotation.Id;

public class SignalSegmentEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2853737768312375288L;
	@Id
	private ObjectId objectId;
	private SignalSegment signalSegment;
	/**
	 * If this is recognizable 
	 */
	private Boolean recognizable; 

	public SignalSegmentEntry() {
	}

	public SignalSegmentEntry(SignalSegment signalSegment) {
		this.signalSegment = signalSegment;
		signalSegment.setId(get_id());
	}

	public SignalSegment getSignalSegment() {
		return signalSegment;
	}

	public void setSignalSegment(SignalSegment signalSegment) {
		this.signalSegment = signalSegment;
		this.signalSegment.setId(get_id());
	}

	
	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
		this.signalSegment.setId(get_id());
	}

	public String get_id() {
		if(objectId == null){
			return null;
		}
		return objectId.toStringMongod();
	}

	public Boolean getRecognizable() {
		return recognizable;
	}

	public void setRecognizable(Boolean recognizable) {
		this.recognizable = recognizable;
	}

}
