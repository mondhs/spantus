package org.spantus.extr.wordspot.service.impl.test;

//import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.online.MarkerSegmentatorListenerImpl;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class SegmentExtractorServiceImplOnlineTest extends AbstractSegmentExtractorTest{


	
	@Override
	protected void changeOtherParams(SegmentExtractorServiceConfig config) {
		super.changeOtherParams(config);
		getSegmentExtractorService().getConfig().setClassifier(ClassifierEnum.rulesOnline);
	}
	
	
	@Test
	public void testExtractSegmentOnline() throws MalformedURLException {
		//given
		URL url = getWavFile().toURI().toURL() ;
		//when
		Collection<SignalSegment> resultOnline = getSegmentExtractorService().extractSegmentsOnline(url);
		ArrayList<SignalSegment> result = new ArrayList<SignalSegment>(resultOnline);
		
		//then
		Assert.assertEquals("Total segments", 5, resultOnline.size());
		Map<String, FrameValuesHolder> valueMap = result.get(0).getFeatureFrameValuesMap();
		Map<String, FrameVectorValuesHolder> vectorMap = result.get(0).getFeatureFrameVectorValuesMap();
		
		Assert.assertNotNull("First segment",result.get(0).getMarker());
		Assert.assertEquals("First segment starts",result.get(0).getMarker().getStart(),243, 0);
		Assert.assertEquals("First segment length",result.get(0).getMarker().getEnd(),429, 1);
		Assert.assertEquals("Extracted feature",2, valueMap.size());
		Assert.assertEquals("Extracted feature length",61, valueMap.get("smooth_"+ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()).getValues().size());
		Assert.assertEquals("Extracted vector feature",2, vectorMap.size());
		Assert.assertEquals("Extracted feature length",60, vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS).getValues().size());
		Assert.assertEquals("segment recognition","pa", result.get(0).getName());
		Assert.assertEquals("segment recognition","ded", result.get(1).getName());
		Assert.assertEquals("segment recognition","da",result.get(2).getName());
		Assert.assertEquals("segment recognition","skirt", result.get(3).getName());
//		Assert.assertEquals("segment recognition", "zhodzh",result.get(4).getName());
		
	}
	@Test 
	public void testExtractSegmentOffline() throws MalformedURLException {
		//given
		URL url = getWavFile().toURI().toURL() ;
		//when
		Collection<SignalSegment> result = getSegmentExtractorService().extractSegmentsOffline(url);
		SignalSegment firstSegment = result.iterator().next();
		Map<String, FrameValuesHolder> valueMap = firstSegment.getFeatureFrameValuesMap();
		//then
		Assert.assertEquals("Total segments", 5, result.size());
		Assert.assertNotNull("First segment",firstSegment.getMarker());
		Assert.assertEquals("First segment starts",firstSegment.getMarker().getStart(),256, 0);
		Assert.assertEquals("First segment length",firstSegment.getMarker().getLength(),191, 0);
		Assert.assertEquals("Extracted feature",3, valueMap.size());
		Assert.assertEquals("Extracted feature",57, valueMap.get("smooth_"+ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()).getValues().size());
//		Assert.assertEquals("Extracted vector feature",1, vectorMap.size());
	}

}
