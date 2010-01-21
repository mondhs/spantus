package org.spantus.core.threshold.test;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtremeCtx;
import org.spantus.core.threshold.ExtremeEntry;
import org.spantus.core.threshold.ExtremeSegment;
import org.spantus.core.threshold.ExtremeClassifierServiceImpl;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;

public class ExtremeClassifierTest extends TestCase {
	ExtremeClassifierServiceImpl extremeThresholdService;
	
	public static final Float[] empty = new Float[]{};
	public static final Float[] singleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F}; 
	public static final Float[] doubleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F};
	
	public static final Float[] complexMinMax = new Float[]{
		0F, 1F, 0F,
		1F, 3F, 2F, 4F, 3F, 6F,
		4F, 5F, 3F, 4F,
		0F, 1F, 0F,
		1F, 0F,
		1F, 0F,
		1F, 0F,
		1F, 0F,
		1F, 0F};

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		extremeThresholdService = new ExtremeClassifierServiceImpl();
	}
	/**
	 * 
	 * @throws Exception
	 */
	public void testExtractExtremes() throws Exception {
		Map<Integer, ExtremeEntry> extremes = null;
		extremes = extremeThresholdService.extractExtremes(createExtremeCtx(empty));
		assertEquals(0, extremes.size());
		extremes = extremeThresholdService.extractExtremes(createExtremeCtx(singleMax));
		assertEquals(3, extremes.size());
		assertMinState(1, extremes);
		assertMaxState(4, extremes);
		assertMinState(7, extremes);
		extremes = extremeThresholdService.extractExtremes(createExtremeCtx(doubleMax));
		assertEquals(5, extremes.size());
		assertMinState(1, extremes);
		assertMaxState(4, extremes);
		assertMinState(7, extremes);
		assertMaxState(11, extremes);
		assertMinState(14, extremes);
	}
	/**
	 * 
	 * @throws Exception
	 */
	public void testExtractSegments() throws Exception {
		ExtremeCtx ctx = null;
		ctx = extremeThresholdService.calculateSegments(createValues(empty));
		assertEquals(0, ctx.getSegments().size());
		ctx = extremeThresholdService.calculateSegments(createValues(singleMax));
		assertEquals(1, ctx.getSegments().size());
		ctx = extremeThresholdService.calculateSegments(createValues(doubleMax));
		assertEquals(2, ctx.getSegments().size());
		assertNotNull(ctx.getMarkerSet());
		
	}
	
	public void testProcessSegments() throws Exception { 
		Map<Integer, ExtremeEntry> extemes  = null;
		ExtremeCtx ctx = createExtremeCtx(complexMinMax);
		
		extemes = extremeThresholdService.extractExtremes(ctx);
		assertEquals(25, extemes.size());
		List<ExtremeSegment> segments = extremeThresholdService.extractSements(ctx);
		assertEquals(12, segments.size());
		segments = extremeThresholdService.initialCleanup(ctx);
		assertEquals(9, segments.size());
	}

	protected ExtremeCtx createExtremeCtx(Float[] fvArr){
		ExtremeCtx extremeCtx = new ExtremeCtx();
		extremeCtx.setValues(createValues(fvArr));
		return extremeCtx;
	}
	
	protected FrameValues createValues(Float[] fvArr){
		FrameValues fv = new FrameValues();
		for (Float float1 : fvArr) {
			fv.add(float1);
		}
		fv.setSampleRate(1000);
		return fv;
	}
	
	public void assertMinState(int index, Map<Integer, ExtremeEntry> extemes){
		assertEquals(SignalStates.min, extemes.get(index).getSignalState());
	}
	public void assertMaxState(int index, Map<Integer, ExtremeEntry> extemes){
		assertEquals(SignalStates.max, extemes.get(index).getSignalState());
	}
}
