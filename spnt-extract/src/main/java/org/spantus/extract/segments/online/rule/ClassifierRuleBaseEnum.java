package org.spantus.extract.segments.online.rule;

public class ClassifierRuleBaseEnum {
	public enum action{startMarker, startMarkerApproved, endMarker, 
		endMarkerApproved, processNoise, processSignal, join};
	
	public enum state{start, segment, end};
}
