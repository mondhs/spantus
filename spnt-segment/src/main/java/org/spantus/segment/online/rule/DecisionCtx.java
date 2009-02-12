package org.spantus.segment.online.rule;

import java.math.BigDecimal;
import java.text.MessageFormat;

import org.spantus.core.marker.Marker;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public class DecisionCtx {
	Marker marker;
	BigDecimal time;
	Boolean state;
	Long sample;
	RuleBaseEnum.state segmentState;
	
	private OnlineDecisionSegmentatorParam param;
	

	public boolean isSegmentInit() {
		return getSegmentState() == null
				|| getSegmentState().equals(RuleBaseEnum.state.start);
	}

	public boolean isSegmentStartState() {
		return getSegmentState().equals(RuleBaseEnum.state.start);
	}

	public boolean isSegmentEndState() {
		return getSegmentState().equals(RuleBaseEnum.state.end);
	}

	public boolean isSegmentState() {
		return getSegmentState().equals(RuleBaseEnum.state.segment);
	}

	public boolean isNoiseState() {
		return getSegmentState() == null;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public BigDecimal getTime() {
		return time;
	}

	public void setTime(BigDecimal time) {
		this.time = time;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean curentState) {
		this.state = curentState;
	}

	public Long getSample() {
		return sample;
	}

	public void setSample(Long sample) {
		this.sample = sample;
	}

	public RuleBaseEnum.state getSegmentState() {
		return segmentState;
	}

	public void setSegmentState(RuleBaseEnum.state segmentState) {
		this.segmentState = segmentState;
	}
	
	public OnlineDecisionSegmentatorParam getParam() {
		if(param == null){
			param = new OnlineDecisionSegmentatorParam();
		}
		return param;
	}

	public void setParam(OnlineDecisionSegmentatorParam param) {
		this.param = param;
	}

	public BigDecimal getSegmentLength() {
		BigDecimal substr = null;
		if (getMarker() != null && getMarker().getStart() != null) {
			substr = getMarker().getStart();
		}
		substr = substr == null ? getTime() : substr;
		return getTime().subtract(substr);
	}

	public BigDecimal getNoiseLength() {
		BigDecimal substr = getTime();
		if (getMarker() != null) {
			substr = getMarker().getStart().add(getMarker().getLength());
		}
		return getTime().subtract(substr);
	}
	
	@Override
	public String toString() {
		return MessageFormat.format( "{0}:[{1} ms] segmentState:{2} frame: {3}", 
				getClass().getSimpleName(), getTime(), getSegmentState(), getState());
	}
}
