/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.segment.online;

import org.spantus.core.threshold.SegmentEvent;
import org.spantus.logger.Logger;
import org.spantus.segment.online.rule.DecisionCtx;
import org.spantus.segment.online.rule.RuleBaseEnum;
import org.spantus.segment.online.rule.RuleBaseService;
import org.spantus.segment.online.rule.RuleServiceFactory;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created 2008.11.27
 * 
 */

public class DecisionSegmentatorOnline extends MultipleSegmentatorListenerOnline {

	
	private DecisionCtx decisionContext;
	private RuleBaseService ruleBaseService;
	
	
	
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(DecisionSegmentatorOnline.class);
	/*
	 * (non-Javadoc)
	 * @see org.spantus.segment.online.MultipleSegmentatorListenerOnline#segmentDetected(org.spantus.core.threshold.SegmentEvent)
	 */
	@Override
	protected void segmentDetected(SegmentEvent event){
		getDecisionContext().setState(true);
		processState(event);
	}
	/*
	 * (non-Javadoc)
	 * @see org.spantus.segment.online.MultipleSegmentatorListenerOnline#noiseDetected(org.spantus.core.threshold.SegmentEvent)
	 */
	@Override
	protected void noiseDetected(final SegmentEvent event){
		getDecisionContext().setState(false);
		processState(event);
	}
	
	
	public void processState(SegmentEvent event){//Long time, IGeneralExtractor extractor, Float val) {
//		DecisionSegmentatorOnlinebug("[onSegmentedProcessed] {0}", event);
		Long time = event.getTime();//calculateTime(extractor, sample);
		Long sample = event.getSample();
		DecisionCtx ctx = getDecisionContext();
		ctx.setTime(time);
		ctx.setSample(sample);
//		ctx.setState(state);//getVoteForState(time, extractor, val));
		if(ctx.getState() == null) return;
		
		String actionStr = getRuleBaseService().testOnRuleBase(ctx);

		RuleBaseEnum.action action = RuleBaseEnum.action.valueOf(actionStr);
		
		switch (action) {
		case processNoise:
			onProcessNoise(ctx, event);
			break;
		case startSegmentFound:
			onStartSegmentFound(ctx, event);
			break;
		case startSegmentApproved:
			onStartSegmentApproved(ctx, event);
			break;
		case processSegment:
			onProcessSegment(ctx, event);
			break;
		case endSegmentFound:
			onEndSegmentFound(ctx, event);
			break;
		case endSegmentApproved:
			onEndSegmentApproved(ctx, event);
			break;
		case joinToSegment:
			onJoinToSegment(ctx, event);
			break;
		case deleteSegment:
			onDeleteSegment(ctx, event);
			break;
		default:
			throw new RuntimeException("Not implemented");
		}
//		log.debug("[processState][{0}ms] on {1} for frame:{2} state:{3}->{4}", 
//				time, action.toString(), ctx.getState(), prevState, ctx.getSegmentState());

	}
	
	public void onProcessNoise(DecisionCtx ctx, SegmentEvent event){}
	
	public void onStartSegmentFound(DecisionCtx ctx, SegmentEvent event){
		ctx.setMarker(createMarker(event));
		finazlizeMarker(ctx.getMarker(), event);
		ctx.setSegmentState(RuleBaseEnum.state.start);
		debugAction("onStartSegmentFound", ctx);
	}
	
	public void onStartSegmentApproved(DecisionCtx ctx, SegmentEvent event){
		super.onStartSegment(ctx.getMarker());
		finazlizeMarker(ctx.getMarker(), event);
		ctx.setSegmentState(RuleBaseEnum.state.segment);
		debugAction("onStartSegmentApproved", ctx);
	}
	
	public void onProcessSegment(DecisionCtx ctx, SegmentEvent event){
		finazlizeMarker(ctx.getMarker(), event);
		ctx.setSegmentState(RuleBaseEnum.state.segment);
		debugAction("onProcessSegment" , ctx);
	}
	
	public void onEndSegmentFound(DecisionCtx ctx, SegmentEvent event){
		finazlizeMarker(ctx.getMarker(), event);
		ctx.setSegmentState(RuleBaseEnum.state.end);
		debugAction("onEndSegmentFound", ctx);
	}
	public void onEndSegmentApproved(DecisionCtx ctx, SegmentEvent event){
		Long expandedStart =ctx.getMarker().getStart()-getParam().getExpandStart();
		Long expandedLength = ctx.getMarker().getLength()+getParam().getExpandEnd();
		ctx.getMarker().setStart(expandedStart);
		ctx.getMarker().setLength(expandedLength);
		debugAction("onEndSegmentApproved", ctx);
		onSegmentEnded(ctx.getMarker());
		ctx.setMarker(null);
		ctx.setSegmentState(null);
	}
	
	public void onJoinToSegment(DecisionCtx ctx, SegmentEvent event){
		finazlizeMarker(ctx.getMarker(), event);
		ctx.setSegmentState(RuleBaseEnum.state.segment);
		debugAction("onJoinToSegment", ctx);
	}
	public void onDeleteSegment(DecisionCtx ctx, SegmentEvent event){
		if(ctx.getMarker() != null)debugAction("onDeleteSegment", ctx);
		setCurrentMarker(null);
		ctx.setMarker(null);
		ctx.setSegmentState(null);
		
	}
		
	public DecisionCtx getDecisionContext() {
		if(decisionContext == null){
			decisionContext = new DecisionCtx();
		}
		return decisionContext;
	}


	public OnlineDecisionSegmentatorParam getParam() {
		return getDecisionContext().getParam();
	}

	public void setParam(OnlineDecisionSegmentatorParam param) {
		getDecisionContext().setParam(param);
	}
	public RuleBaseService getRuleBaseService() {
		if(ruleBaseService == null){
			ruleBaseService = RuleServiceFactory.createRuleBaseService();
		}
		return ruleBaseService;
	}

	public void setRuleBaseService(RuleBaseService ruleBaseService) {
		this.ruleBaseService = ruleBaseService;
	}	
	protected void debugAction(String msg, DecisionCtx ctx){
//		if(log.isDebugMode()){
//			RuleBaseEnum.state previous = ctx.getPreviousState();
//			if(previous == null ||
//					!previous.equals(ctx.getSegmentState())){
//			log.debug("{0}: {1}",msg, ctx);
//			}
//		}
	}
}
