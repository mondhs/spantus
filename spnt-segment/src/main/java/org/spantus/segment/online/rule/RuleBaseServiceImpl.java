package org.spantus.segment.online.rule;

import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public class RuleBaseServiceImpl implements RuleBaseService{
	/**
	 * 
	 */
	public RuleBaseEnum.action testOnRuleBase(DecisionCtx ctx){
		Long segmentLength = ctx.getSegmentLength();
		Long noiseLength = ctx.getNoiseLength();
		Boolean state = ctx.getState();
		boolean isNoiseFrame = Boolean.FALSE.equals(state);
		boolean isSignalFrame = Boolean.TRUE.equals(state);
		
		OnlineDecisionSegmentatorParam param = ctx.getParam();
		 
		if(isNoiseFrame && ctx.isNoiseState()){
			return RuleBaseEnum.action.processNoise;
		}else if(isSignalFrame && ctx.isNoiseState()){
			return RuleBaseEnum.action.startSegmentFound;
		}else if(isSignalFrame
				&& ctx.isSegmentStartState()
				&& segmentLength > param.getMinLength()){
			return RuleBaseEnum.action.startSegmentApproved;
		}else if(isSignalFrame
				&& ctx.isSegmentStartState()){
			return RuleBaseEnum.action.processNoise;
		}else if(isSignalFrame && ctx.isSegmentState() ){
			return RuleBaseEnum.action.processSegment;
		}else if(isSignalFrame && ctx.isSegmentEndState() ){
			return RuleBaseEnum.action.joinToSegment;
		}else if(isNoiseFrame && ctx.isSegmentStartState() 
				&& !(noiseLength > param.getMinSpace())){
			return RuleBaseEnum.action.deleteSegment;
		}else if(isNoiseFrame && ctx.isSegmentStartState() ){
			return RuleBaseEnum.action.processNoise;
		}else if(isNoiseFrame && ctx.isSegmentState() ){
			return RuleBaseEnum.action.endSegmentFound;
		}else if(isNoiseFrame
				&& ctx.isSegmentEndState()
				&& (noiseLength > param.getMinSpace()) 
				&& !(ctx.getMarker().getLength() > param.getMinLength())){
			return RuleBaseEnum.action.deleteSegment;
		}else if(isNoiseFrame && ctx.isSegmentEndState() &&
				noiseLength < param.getMinSpace()){
			return RuleBaseEnum.action.processNoise;
		}else if(isNoiseFrame && ctx.isSegmentEndState()){
			return RuleBaseEnum.action.endSegmentApproved;
		}
		throw new RuntimeException("Rule not impl: " + ctx);
	}
	


}
