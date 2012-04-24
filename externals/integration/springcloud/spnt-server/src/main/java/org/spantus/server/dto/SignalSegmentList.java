package org.spantus.server.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SignalSegmentList")
public class SignalSegmentList implements Iterable<SignalSegmentEntry>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2970300138769115213L;
	
	@XmlElement(name = "SignalSegment")
	private List<SignalSegmentEntry> signalSegment = new ArrayList<SignalSegmentEntry>();

	
	public SignalSegmentList() {
	}
	
	
	public SignalSegmentList(List<SignalSegmentEntry> signalSegment) {
		this.signalSegment=signalSegment;
	}
	
	
	public List<SignalSegmentEntry> getSignalSegment() {
		return signalSegment;
	}

	public void setSignalSegment(List<SignalSegmentEntry> signalSegment) {
		this.signalSegment = signalSegment;
	}

	@Override
	public Iterator<SignalSegmentEntry> iterator() {
		return getSignalSegment().iterator();
	}

}
