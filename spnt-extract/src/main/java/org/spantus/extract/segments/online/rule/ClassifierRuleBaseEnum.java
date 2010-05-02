package org.spantus.extract.segments.online.rule;

public class ClassifierRuleBaseEnum {
	public enum action{changePoint, changePointLastApproved, changePointCurrentApproved, processNoise, processSignal, join, delete};
	
	public enum state{start, segment, end, noise};
}
