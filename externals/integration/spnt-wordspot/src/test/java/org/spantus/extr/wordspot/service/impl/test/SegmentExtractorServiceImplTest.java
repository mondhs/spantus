package org.spantus.extr.wordspot.service.impl.test;

//import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.extractor.impl.ExtractorEnum;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class SegmentExtractorServiceImplTest extends AbstractSegmentExtractorTest{

    @Override
    public void setUp() throws Exception {
        setRepositoryPath(new File(getRepositoryPathRoot(),"CORPUS/word"));
        super.setUp();
    }


        
    
	@Test
	public void testExtractSegmentOnline() throws MalformedURLException {
		//given
		URL url = getWavFile().toURI().toURL() ;
		//when
		Collection<SignalSegment> resultOnline = getSegmentExtractorService().extractSegmentsOnline(url);
		//then
		Assert.assertEquals("Total segments", 3, resultOnline.size());
		Iterator<SignalSegment> iterator = resultOnline.iterator();
		SignalSegment firstSegment = iterator.next();
//		SignalSegment secondSegment = iterator.next();
		Map<String, FrameValuesHolder> valueMap = firstSegment.getFeatureFrameValuesMap();
		Map<String, FrameVectorValuesHolder> vectorMap = firstSegment.getFeatureFrameVectorValuesMap();
		Assert.assertNotNull("First segment",firstSegment.getMarker());
		Assert.assertEquals("First segment starts",297,firstSegment.getMarker().getStart(), 0);
		Assert.assertEquals("First segment length",479,firstSegment.getMarker().getLength(), 0);
		Assert.assertEquals("Extracted feature",3, valueMap.size());
		Assert.assertEquals("Extracted feature length",141, valueMap.get("smooth_"+ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()).getValues().size(),1);
		Assert.assertEquals("Extracted vector feature",1, vectorMap.size());
//		Assert.assertEquals("Extracted feature length",141, vectorMap.get(MarkerSegmentatorListenerImpl.SIGNAL_WINDOWS).getValues().size(),1);
//		Assert.assertEquals("first segment recognition",firstSegment.getName(), "padeda");
//		Assert.assertEquals("second segment recognition",secondSegment.getName(), "skirti");
		
	}
	@Test
	public void testExtractSegmentOffline() throws MalformedURLException {
		//given
		URL url = getWavFile().toURI().toURL() ;
		//when
		Collection<SignalSegment> result = getSegmentExtractorService().extractSegmentsOffline(url);
		SignalSegment firstSegment = result.iterator().next();
		Map<String, FrameValuesHolder> valueMap = firstSegment.getFeatureFrameValuesMap();
//		Map<String, FrameVectorValuesHolder> vectorMap = firstSegment.getFeatureFrameVectorValuesMap();
		//then
		Assert.assertEquals("Total segments", 4, result.size());
		Assert.assertNotNull("First segment",firstSegment.getMarker());
		Assert.assertEquals("First segment starts",331, firstSegment.getMarker().getStart(), 0);
		Assert.assertEquals("First segment length",413, firstSegment.getMarker().getLength(), 0);
		Assert.assertEquals("Extracted feature",3, valueMap.size());
		Assert.assertEquals("Extracted feature",122, valueMap.get("smooth_"+ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name()).getValues().size());
//		Assert.assertEquals("Extracted vector feature",1, vectorMap.size());
	}

}
