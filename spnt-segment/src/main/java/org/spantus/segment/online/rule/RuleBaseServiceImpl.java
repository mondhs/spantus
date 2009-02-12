package org.spantus.segment.online.rule;

import java.math.BigDecimal;

import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public class RuleBaseServiceImpl implements RuleBaseService{
	/**
	 * 
	 */
	public RuleBaseEnum.action testOnRuleBase(DecisionCtx ctx){
		BigDecimal segmentLength = ctx.getSegmentLength();
		BigDecimal noiseLength = ctx.getNoiseLength();
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
				&& gt(segmentLength, param.getMinLength())){
			return RuleBaseEnum.action.startSegmentApproved;
		}else if(isSignalFrame
				&& ctx.isSegmentStartState()){
			return RuleBaseEnum.action.processNoise;
		}else if(isSignalFrame && ctx.isSegmentState() ){
			return RuleBaseEnum.action.processSegment;
		}else if(isSignalFrame && ctx.isSegmentEndState() ){
			return RuleBaseEnum.action.joinToSegment;
		}else if(isNoiseFrame && ctx.isSegmentStartState() 
				&& !gt(noiseLength, param.getMinSpace())){
			return RuleBaseEnum.action.deleteSegment;
		}else if(isNoiseFrame && ctx.isSegmentStartState() ){
			return RuleBaseEnum.action.processNoise;
		}else if(isNoiseFrame && ctx.isSegmentState() ){
			return RuleBaseEnum.action.endSegmentFound;
		}else if(isNoiseFrame
				&& ctx.isSegmentEndState()
				&& gt(noiseLength, param.getMinSpace()) 
				&& !gt(ctx.getMarker().getLength(), param.getMinLength())){
			return RuleBaseEnum.action.deleteSegment;
		}else if(isNoiseFrame && ctx.isSegmentEndState()){
			return RuleBaseEnum.action.endSegmentApproved;
		}
		throw new RuntimeException("Rule not impl: " + ctx);
//		return RuleBaseEnum.action.processNoise;
	}
	
	/**
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	private boolean gt(BigDecimal a1, BigDecimal a2){
		return a1.compareTo(a2) > 0;
	}
//	/**
//	 * 
//	 * @param a1
//	 * @param a2
//	 * @return
//	 */
//	private boolean lt(BigDecimal a1, BigDecimal a2){
//		return a1.compareTo(a2) < 0;
//	}

}
