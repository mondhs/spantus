package org.spantus.segment.online.rule;

public class RuleBaseEnum {
	
	public enum action{startSegmentFound, startSegmentApproved, processSegment, endSegmentFound, endSegmentApproved, joinToSegment,
		deleteSegment, processNoise};
	
	public enum state{start, segment, end};

}
