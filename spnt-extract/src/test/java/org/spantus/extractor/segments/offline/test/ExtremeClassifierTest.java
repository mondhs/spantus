package org.spantus.extractor.segments.offline.test;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.segments.offline.ExtremeClassifierServiceImpl;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeOfflineCtx;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;

public class ExtremeClassifierTest{
	ExtremeClassifierServiceImpl extremeThresholdService;
	
	public static final Double[] empty = new Double[]{};
	public static final Double[] singleMax = new Double[]{0D, 0D, 1D, 2D, 3D, 2D, 1D, 0D, 0D}; 
	public static final Double[] doubleMax = new Double[]{0D, 0D, 1D, 2D, 3D, 2D, 1D, 0D, 0D, 1D, 2D, 3.1D, 2D, 1.5D, 1D, 0D, 0D};
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
	public static final Double[] complexMinMax = new Double[]{
		0D, 1D, 0D,
		1D, 3D, 2D, 4D, 3D, 6D,
		4D, 5D, 3D, 4D,
		0D, 1D, 0D,
		1D, 0D,
		1D, 0D,
		1D, 0D,
		1D, 0D,
		1D, 0D};

	
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
		Assert.assertEquals(11, segments.size());
	}

	protected ExtremeOfflineCtx createExtremeCtx(Double[] fvArr){
		ExtremeOfflineCtx extremeCtx = new ExtremeOfflineCtx();
		extremeCtx.setValues(createValues(fvArr));
		return extremeCtx;
	}
	
	protected FrameValues createValues(Double[] fvArr){
		FrameValues fv = new FrameValues();
		for (Double float1 : fvArr) {
			fv.add(float1);
		}
		fv.setSampleRate(1000D);
		return fv;
	}
	
	public void assertMinState(int index, Map<Integer, ExtremeEntry> extemes){
		Assert.assertEquals(FeatureStates.min, extemes.get(index).getSignalState());
	}
	public void assertMaxState(int index, Map<Integer, ExtremeEntry> extemes){
		Assert.assertEquals(FeatureStates.max, extemes.get(index).getSignalState());
	}
}
