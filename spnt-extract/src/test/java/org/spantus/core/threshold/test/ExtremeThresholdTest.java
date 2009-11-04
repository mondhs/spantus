package org.spantus.core.threshold.test;

import java.util.Map;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtremeEntry;
import org.spantus.core.threshold.ExtremeThresholdServiceImpl;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;

public class ExtremeThresholdTest extends TestCase {
	ExtremeThresholdServiceImpl extremeThresholdService;
	
	public static final Float[] singleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F}; 
	public static final Float[] doubleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F}; 

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		extremeThresholdService = new ExtremeThresholdServiceImpl();
	}
	
	public void testExtractExtremes() throws Exception {
		Map<Integer, ExtremeEntry> extemes = extremeThresholdService.extractExtremes(createValues(singleMax));
		assertEquals(3, extemes.size());
		assertMinState(1, extemes);
		assertMaxState(4, extemes);
		assertMinState(7, extemes);
		extemes = extremeThresholdService.extractExtremes(createValues(doubleMax));
		assertEquals(5, extemes.size());
		assertMinState(1, extemes);
		assertMaxState(4, extemes);
		assertMinState(7, extemes);
		assertMaxState(11, extemes);
		assertMinState(14, extemes);
	}
	public void testProcessExtremes() throws Exception { 
		FrameValues singleMaxFv = createValues(singleMax);
		Map<Integer, ExtremeEntry> extemes = extremeThresholdService.extractExtremes(singleMaxFv);
		extemes = extremeThresholdService.processExtremes(extemes, singleMaxFv);
		assertEquals(3, extemes.size());
		assertMinState(1, extemes);
		assertMaxState(4, extemes);
		assertMinState(7, extemes);
	}

	
	
	protected FrameValues createValues(Float[] fvArr){
		FrameValues fv = new FrameValues();
		for (Float float1 : fvArr) {
			fv.add(float1);
		}
		return fv;
	}
	
	public void assertMinState(int index, Map<Integer, ExtremeEntry> extemes){
		assertEquals(SignalStates.minExtream, extemes.get(index).getSignalState());
	}
	public void assertMaxState(int index, Map<Integer, ExtremeEntry> extemes){
		assertEquals(SignalStates.maxExtream, extemes.get(index).getSignalState());
	}
}
