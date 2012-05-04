package org.spantus.extr.wordspot.service.impl.test;

//import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.extr.wordspot.service.impl.SegmentExtractorServiceImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.online.MarkerSegmentatorListenerImpl;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public class SegmentExtractorServiceImplTest {

	private SegmentExtractorServiceImpl segmentExtractorService;
	private File wavFile = new File("../../../data/text1.8000.wav");
	private File repositoryPath = new File("../../../data/corpus");
	
	@Before
	public void onSetup(){
	
		Assert.assertTrue("repositoryPath exists", repositoryPath.exists());
		Assert.assertTrue("repositoryPath is directory", repositoryPath.isDirectory());
		Assert.assertTrue("wavFile exists", wavFile.exists());
		segmentExtractorService = new SegmentExtractorServiceImpl();
		segmentExtractorService.setRepositoryPath(repositoryPath.getAbsolutePath());
		segmentExtractorService.updateParams();
	}
	
	@Test
	public void testExtractSegmentOnline() throws MalformedURLException {
		//given
		URL url = wavFile.toURI().toURL() ;
		//when
		Collection<SignalSegment> resultOnline = segmentExtractorService.extractSegmentsOnline(url);
		Iterator<SignalSegment> iterator = resultOnline.iterator();
		SignalSegment firstSegment = iterator.next();
		SignalSegment secondSegment = iterator.next();
		Map<String, FrameValuesHolder> valueMap = firstSegment.getFeatureFrameValuesMap();
		Map<String, FrameVectorValuesHolder> vectorMap = firstSegment.getFeatureFrameVectorValuesMap();
		//then
		Assert.assertEquals("Total segments", 2, resultOnline.size());
		Assert.assertNotNull("First segment",firstSegment.getMarker());
		Assert.assertEquals("First segment starts",firstSegment.getMarker().getStart(),300, 0);
		Assert.assertEquals("First segment length",firstSegment.getMarker().getLength(),476, 0);
		Assert.assertEquals("Extracted feature",3, valueMap.size());
		Assert.assertEquals("Extracted feature length",141, valueMap.get("smooth_"+ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()).getValues().size());
		Assert.assertEquals("Extracted vector feature",2, vectorMap.size());
		Assert.assertEquals("Extracted feature length",141, vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS).getValues().size());
		Assert.assertEquals("first segment recognition",firstSegment.getName(), "padeda");
		Assert.assertEquals("second segment recognition",secondSegment.getName(), "skirti");
		
	}
	@Test
	public void testExtractSegmentOffline() throws MalformedURLException {
		//given
		URL url = wavFile.toURI().toURL() ;
		//when
		Collection<SignalSegment> result = segmentExtractorService.extractSegmentsOffline(url);
		SignalSegment firstSegment = result.iterator().next();
		Map<String, FrameValuesHolder> valueMap = firstSegment.getFeatureFrameValuesMap();
		Map<String, FrameVectorValuesHolder> vectorMap = firstSegment.getFeatureFrameVectorValuesMap();
		//then
		Assert.assertEquals("Total segments", 4, result.size());
		Assert.assertNotNull("First segment",firstSegment.getMarker());
		Assert.assertEquals("First segment starts",firstSegment.getMarker().getStart(),332, 0);
		Assert.assertEquals("First segment length",firstSegment.getMarker().getLength(),412, 0);
		Assert.assertEquals("Extracted feature",3, valueMap.size());
		Assert.assertEquals("Extracted feature",122, valueMap.get("smooth_"+ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()).getValues().size());
//		Assert.assertEquals("Extracted vector feature",1, vectorMap.size());
	}

}
