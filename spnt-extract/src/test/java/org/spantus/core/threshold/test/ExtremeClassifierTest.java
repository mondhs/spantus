package org.spantus.core.threshold.test;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extract.segments.offline.ExtremeClassifierServiceImpl;
import org.spantus.extract.segments.offline.ExtremeEntry;
import org.spantus.extract.segments.offline.ExtremeOfflineCtx;
import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.offline.ExtremeEntry.FeatureStates;

public class ExtremeClassifierTest{
	ExtremeClassifierServiceImpl extremeThresholdService;
	
	public static final Float[] empty = new Float[]{};
	public static final Float[] singleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F}; 
	public static final Float[] doubleMax = new Float[]{0F, 0F, 1F, 2F, 3F, 2F, 1F, 0F, 0F, 1F, 2F, 3.1F, 2F, 1.5F, 1F, 0F, 0F};
	/**
	 * 
	 * 
	 * <pre>
	 *            9
	 *            /\ 11
	 *        7  /  \/\ 13
	 *     5  /\/      \/\
	 *     /\/            \
	 * 2  /                \ 15  19  23
	 * /\/                  \/\/\/\/\/\/\
	 * </pre>
	 */
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

	
	@Before
	public void setUp() throws Exception {
		extremeThresholdService = new ExtremeClassifierServiceImpl();
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtractExtremes() throws Exception {
		Map<Integer, ExtremeEntry> extremes = null;
		extremes = extremeThresholdService.extractExtremes(createExtremeCtx(empty));
		Assert.assertEquals(0, extremes.size());
		extremes = extremeThresholdService.extractExtremes(createExtremeCtx(singleMax));
		Assert.assertEquals(3, extremes.size());
		assertMinState(1, extremes);
		assertMaxState(4, extremes);
		assertMinState(7, extremes);
		extremes = extremeThresholdService.extractExtremes(createExtremeCtx(doubleMax));
		Assert.assertEquals(5, extremes.size());
		assertMinState(1, extremes);
		assertMaxState(4, extremes);
		assertMinState(7, extremes);
		assertMaxState(11, extremes);
		assertMinState(15, extremes);
	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtractSegments() throws Exception {
		ExtremeOfflineCtx ctx = null;
		ctx = extremeThresholdService.calculateSegments(createValues(empty));
		Assert.assertEquals(0, ctx.getSegments().size());
		ctx = extremeThresholdService.calculateSegments(createValues(singleMax));
		Assert.assertEquals(1, ctx.getSegments().size());
		ctx = extremeThresholdService.calculateSegments(createValues(doubleMax));
		Assert.assertEquals(2, ctx.getSegments().size());
		Assert.assertNotNull(ctx.getMarkerSet());
		
	}
	
	@Test
	public void testProcessSegments() throws Exception { 
		Map<Integer, ExtremeEntry> extemes  = null;
		ExtremeOfflineCtx ctx = createExtremeCtx(complexMinMax);
		
		extemes = extremeThresholdService.extractExtremes(ctx);
		Assert.assertEquals(25, extemes.size());
		List<ExtremeSegment> segments = extremeThresholdService.extractSements(ctx);
		Assert.assertEquals(12, segments.size());
		segments = extremeThresholdService.initialCleanup(ctx);
		Assert.assertEquals(9, segments.size());
	}

	protected ExtremeOfflineCtx createExtremeCtx(Float[] fvArr){
		ExtremeOfflineCtx extremeCtx = new ExtremeOfflineCtx();
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
		Assert.assertEquals(FeatureStates.min, extemes.get(index).getSignalState());
	}
	public void assertMaxState(int index, Map<Integer, ExtremeEntry> extemes){
		Assert.assertEquals(FeatureStates.max, extemes.get(index).getSignalState());
	}
}
