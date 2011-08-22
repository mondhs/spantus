package org.spantus.extractor.segments.online.rule;

public class ClassifierRuleBaseEnum {
	public enum action{changePoint, changePointLastApproved, initSegment, processNoise, processSignal, join, delete};
	
	public enum state{start, segment, end, noise};
}
