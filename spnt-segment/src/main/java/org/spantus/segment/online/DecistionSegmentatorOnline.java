/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.segment.online;

import java.math.BigDecimal;

import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.logger.Logger;
import org.spantus.segment.online.rule.DecisionCtx;
import org.spantus.segment.online.rule.RuleBaseEnum;
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

public class DecistionSegmentatorOnline extends MultipleSegmentatorOnline {

	
	private DecisionCtx decisionContext;
	
	
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(DecistionSegmentatorOnline.class);
	
	@Override
	public void processState(Long sample, IGeneralExtractor extractor, Float val) {
		BigDecimal time = calculateTime(extractor, sample);
		DecisionCtx ctx = getDecisionContext();
		ctx.setTime(time);
		ctx.setSample(sample);
		ctx.setState(getVoteForState(time, extractor, val));
		if(ctx.getState() == null) return;
//		RuleBaseEnum.state prevState = ctx.getSegmentState();
		RuleBaseEnum.action action = RuleServiceFactory.createRuleBaseService().testOnRuleBase(ctx);
		switch (action) {
		case processNoise:
			onProcessNoise(ctx,time,sample);
			break;
		case startSegmentFound:
			onStartSegmentFound(ctx,time,sample);
			break;
		case startSegmentApproved:
			onStartSegmentApproved(ctx, time, sample);
			break;
		case processSegment:
			onProcessSegment(ctx, time, sample);
			break;
		case endSegmentFound:
			onEndSegmentFound(ctx, time, sample);
			break;
		case endSegmentApproved:
			onEndSegmentApproved(ctx, time, sample);
			break;
		case joinToSegment:
			onJoinToSegment(ctx, time, sample);
			break;
		case deleteSegment:
			onDeleteSegment(ctx, time, sample);
			break;
		default:
			throw new RuntimeException("Not implemented");
		}
//		log.debug("[processState][{0}ms] on {1} for frame:{2} state:{3}->{4}", 
//				time, action.toString(), ctx.getState(), prevState, ctx.getSegmentState());

	}
	
	public void onProcessNoise(DecisionCtx ctx, BigDecimal time, Long sample){}
	
	public void onStartSegmentFound(DecisionCtx ctx, BigDecimal time, Long sample){
		ctx.setMarker(createSegment(sample, time));
		finazlizeSegment(ctx.getMarker(), sample, time);
		ctx.setSegmentState(RuleBaseEnum.state.start);
	}
	
	public void onStartSegmentApproved(DecisionCtx ctx, BigDecimal time, Long sample){
		super.onStartSegment(ctx.getMarker());
		finazlizeSegment(ctx.getMarker(), sample, time);
		ctx.setSegmentState(RuleBaseEnum.state.segment);
	}
	
	public void onProcessSegment(DecisionCtx ctx, BigDecimal time, Long sample){
		finazlizeSegment(ctx.getMarker(), sample, time);
		ctx.setSegmentState(RuleBaseEnum.state.segment);
	}
	
	public void onEndSegmentFound(DecisionCtx ctx, BigDecimal time, Long sample){
		finazlizeSegment(ctx.getMarker(), sample, time);
		ctx.setSegmentState(RuleBaseEnum.state.end);
	}
	public void onEndSegmentApproved(DecisionCtx ctx, BigDecimal time, Long sample){
		BigDecimal expandedStart =ctx.getMarker().getStart().add(getParam().getExpandMarkerInTime().negate()); 
		ctx.getMarker().setStart(expandedStart);
		onSegmentEnded(ctx.getMarker());
		ctx.setMarker(null);
		ctx.setSegmentState(null);
	}
	
	public void onJoinToSegment(DecisionCtx ctx, BigDecimal time, Long sample){
		finazlizeSegment(ctx.getMarker(), sample, time);
		ctx.setSegmentState(RuleBaseEnum.state.segment);
	}
	public void onDeleteSegment(DecisionCtx ctx, BigDecimal time, Long sample){
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
	
}
