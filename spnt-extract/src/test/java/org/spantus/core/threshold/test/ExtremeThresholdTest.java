package org.spantus.core.threshold.test;

import java.util.Map;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtremeEntry;
import org.spantus.core.threshold.ExtremeThresholdServiceImpl;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;

public class ExtremeThresholdTest extends TestCase {
	ExtremeThresholdServiceImpl extremeThresholdService;
	
	public static final Float[] empty = new Float[]{};
	public static final Float[] singleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F}; 
	public static final Float[] doubleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F}; 

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		extremeThresholdService = new ExtremeThresholdServiceImpl();
	}
	
	public void testExtractExtremes() throws Exception {
		Map<Integer, ExtremeEntry> extemes = null;
		
		extemes = extremeThresholdService.extractExtremes(createValues(empty));
		assertEquals(0, extemes.size());
		
		extemes = extremeThresholdService.extractExtremes(createValues(singleMax));
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
		Map<Integer, ExtremeEntry> extemes  = null;
		FrameValues values = createValues(empty);
		extemes = extremeThresholdService.extractExtremes(values);
		extemes = extremeThresholdService.processExtremes(extemes, values);
		assertEquals(0, extemes.size());
		
		values = createValues(doubleMax);
		extemes = extremeThresholdService.extractExtremes(values);
		extemes = extremeThresholdService.processExtremes(extemes, values);
		assertEquals(5, extemes.size());
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
