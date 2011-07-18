package org.spantus.work.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseEnum;
import org.spantus.work.extractor.segments.online.rule.ClassifierRuleBaseServiceMvelImpl;

public class ClassifierRuleBaseServiceMvelImplTest {
	ClassifierRuleBaseServiceMvelImpl impl;
	
	@Before
	public void onSetup() {
		impl = new ClassifierRuleBaseServiceMvelImpl();
	}
	
	@Test
	public void testCurentNull(){
		//given
		ExtremeSegmentsOnlineCtx ctx = new ExtremeSegmentsOnlineCtx(); 
		ctx.setSegmentEntry(new ExtremeEntry(0,1D,FeatureStates.min));
		
		//when
		String decision = impl.testOnRuleBase(ctx);
		//then
		Assert.assertEquals(ClassifierRuleBaseEnum.action.changePointCurrentApproved.name(), decision);
	}
	@Test
	public void testLastNull(){
		//given
		ExtremeSegmentsOnlineCtx ctx = new ExtremeSegmentsOnlineCtx(); 
		ctx.setSegmentEntry(new ExtremeEntry(0,1D,FeatureStates.min));
		ctx.setCurrentSegment(new ExtremeSegment());
		ctx.getCurrentSegment().setValues(new FrameValues());
		//when
		String decision = impl.testOnRuleBase(ctx);
		//then
		Assert.assertEquals(ClassifierRuleBaseEnum.action.changePointCurrentApproved.name(), decision);
	}
	@Test
	public void testLastNotNull(){
		//given
		ExtremeSegmentsOnlineCtx ctx = new ExtremeSegmentsOnlineCtx(); 
		ctx.setSegmentEntry(new ExtremeEntry(0,1D,FeatureStates.max));
		ctx.setExtremeSegments(new LinkedList<ExtremeSegment>());
		ctx.setCurrentSegment(createExtremeSegment());
		//when
		String decision = impl.testOnRuleBase(ctx);
		//then
		Assert.assertEquals(ClassifierRuleBaseEnum.action.processSignal.name(), decision);
	}
	@Test
	public void testIsmin(){
		//given
		ExtremeSegmentsOnlineCtx ctx = new ExtremeSegmentsOnlineCtx(); 
		ctx.setSegmentEntry(new ExtremeEntry(0,1D,FeatureStates.min));
		ctx.setExtremeSegments(new LinkedList<ExtremeSegment>());
		ctx.getExtremeSegments().add(createExtremeSegment());
		ctx.setCurrentSegment(createExtremeSegment());
		//when
		String decision = impl.testOnRuleBase(ctx);
		//then
		Assert.assertEquals(ClassifierRuleBaseEnum.action.changePoint.name(), decision);
	}
	
	@Test
	public void testDistanceBetweenPeaks(){
		//given
		ExtremeSegmentsOnlineCtx ctx = new ExtremeSegmentsOnlineCtx(); 
		ctx.setSegmentEntry(new ExtremeEntry(0,1D,FeatureStates.max));
		
		ctx.setExtremeSegments(new LinkedList<ExtremeSegment>());
		ctx.getExtremeSegments().add(createExtremeSegment());
		
		ctx.setCurrentSegment(createExtremeSegment());
		
		
		//when
		String decision = impl.testOnRuleBase(ctx);
		//then
		Assert.assertEquals(ClassifierRuleBaseEnum.action.join.name(), decision);
	}
	
	@Test
	public void testNPEHandling(){
		//given
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("test", null);
		//when
		Boolean decision = impl.execute("test.test",params);
		//then
		Assert.assertEquals(Boolean.FALSE, decision);
	}
	
	
	private ExtremeSegment createExtremeSegment() {
		ExtremeSegment extremeSegment = new ExtremeSegment();
		extremeSegment.setValues(new FrameValues());
		extremeSegment.setPeakEntries(new LinkedList<ExtremeEntry>());
		extremeSegment.getPeakEntries().add(new ExtremeEntry(0,0D,FeatureStates.max));
		extremeSegment.getPeakEntries().add(new ExtremeEntry(0,1D,FeatureStates.max));
		extremeSegment.getPeakEntries().add(new ExtremeEntry(0,0D,FeatureStates.max));
		extremeSegment.setPeakEntry(new ExtremeEntry(0,1D,FeatureStates.max));
		extremeSegment.setStartEntry(new ExtremeEntry(0,0D,FeatureStates.min));
		extremeSegment.setEndEntry(new ExtremeEntry(0,0D,FeatureStates.min));

		return extremeSegment;
	}
	
	
	
	
}
