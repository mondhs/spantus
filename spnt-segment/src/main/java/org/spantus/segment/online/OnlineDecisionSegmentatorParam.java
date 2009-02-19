package org.spantus.segment.online;

import org.spantus.segment.offline.SimpleDecisionSegmentatorParam;

public class OnlineDecisionSegmentatorParam extends SimpleDecisionSegmentatorParam {
	
	private Long expandStart;
	private Long expandEnd;
	
	
	
	public Long getExpandStart() {
		if(expandStart == null){
			expandStart = 0L;
		}
		return expandStart;
	}

	public void setExpandStart(Long latency) {
		this.expandStart = latency;
	}
	
	public Long getExpandEnd() {
		if(expandEnd == null){
			expandEnd = 0L;
		}
		return expandEnd;
	}
	
	public void setExpandEnd(Long expandEnd) {
		this.expandEnd = expandEnd;
	}
}
