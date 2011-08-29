package org.spantus.extractor.segments.online.test;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.offline.test.ExtremeClassifierTest;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterServiceSimpleImpl;
import org.spantus.extractor.segments.online.rule.ClassifierPostProcessServiceBaseImpl;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseServiceImpl;
import org.spantus.logger.Logger;
/**
 *  
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created Mar 16, 2010
 *
 */
public class ExtremeOnlineClassifierTest {

	Logger log = Logger.getLogger(getClass());	
	
	@Test 
	public void testOnlineExtremeSegmentExtractionEmpty() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		classifier  = feedData(ExtremeClassifierTest.empty, ruleBaseService);
		Assert.assertEquals(0, classifier.getExtremeSegments().size());
	}
	
	@Test 
	public void testOnlineExtremeSegmentExtractionSinleMax() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		classifier  = feedData(ExtremeClassifierTest.singleMax, ruleBaseService);
		Assert.assertEquals(1, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull("start entry null for " +es, es.getStartEntry());
			Assert.assertNotNull("Peak entry null for " +es, es.getPeakEntry());
			Assert.assertNotNull("end entry null for " +es, es.getEndEntry());
		}
	}
	
	@Test 
	public void testOnlineExtremeSegmentExtractionSilendDoubleMax() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		classifier  = feedData(ExtremeClassifierTest.silentDoubleMax, ruleBaseService);
		Assert.assertEquals(2, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull(es.getStartEntry());
			Assert.assertNotNull(es.getPeakEntry());
			Assert.assertNotNull(es.getEndEntry());
		}
	}
	
	@Test 
	public void testOnlineExtremeSegmentExtractionDoubleMax() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		classifier  = feedData(ExtremeClassifierTest.doubleMax, ruleBaseService);
		Assert.assertEquals(2, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull(es.getStartEntry());
			Assert.assertNotNull(es.getPeakEntry());
			Assert.assertNotNull(es.getEndEntry());
		}
	}
	
	
	@Test 
	public void testOnlineExtremeSegmentExtractionComplexMinMax() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		
		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		
		classifier = feedData(ExtremeClassifierTest.complexMinMax, ruleBaseService);
		Assert.assertEquals(12, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull(es.getStartEntry());
			Assert.assertNotNull(es.getPeakEntry());
			Assert.assertNotNull(es.getEndEntry());
		}


	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test @Ignore
	public void testOnlineMarkersExtractionRule() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ExtremeOnlineClusterService clusterService = new ExtremeOnlineClusterServiceSimpleImpl(); 
		
		ClassifierRuleBaseServiceImpl ruleBaseService = new ClassifierRuleBaseServiceImpl();
		ruleBaseService.setClusterService(clusterService);
		
		classifier  = feedData(ExtremeClassifierTest.empty, ruleBaseService);
		Assert.assertEquals(0, classifier.getMarkSet().getMarkers().size());
		
		classifier = feedData(ExtremeClassifierTest.singleMax, ruleBaseService);
		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
		
		classifier = feedData(ExtremeClassifierTest.doubleMax, ruleBaseService);
		Assert.assertEquals(2, classifier.getMarkSet().getMarkers().size());


	}
	
	@Test @Ignore
	public void testMarkersExtractionRuleComplex() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ExtremeOnlineClusterService clusterService = new ExtremeOnlineClusterServiceSimpleImpl(); 
		ClassifierRuleBaseServiceImpl ruleBaseService = new ClassifierRuleBaseServiceImpl();
		ruleBaseService.setClusterService(clusterService);
		
		classifier = feedData(ExtremeClassifierTest.complexMinMax, ruleBaseService);
		log.debug("[testOnlineMarkersExtractionRule] markers {0}",classifier.getMarkSet().getMarkers());

		Assert.assertEquals(3, classifier.getExtremeSegments().size());
		
		
		Assert.assertEquals(3, classifier.getMarkSet().getMarkers().size());
	}
	
	protected ExtremeOnlineClusterService createClusterService(){
		return new ExtremeOnlineClusterServiceSimpleImpl();
	}

	
	protected void logData(Double[] data){
		log.debug("[logData] arr: \n" + Arrays.asList(data));
	}
	protected ExtremeOnlineRuleClassifier feedData(Double[] data) {
		return feedData(data, null );
	}
	protected ExtremeOnlineRuleClassifier feedData(Double[] data, 
			ClassifierRuleBaseService ruleBase) {
		logData(data);
		ExtremeOnlineRuleClassifier classifier = new ExtremeOnlineRuleClassifier();
		classifier.setExtractor(new MockOnlineExtractor());
		classifier.getExtractor().getOutputValues().setSampleRate(100D);//10ms
		classifier.setRuleBaseService(ruleBase);
//		classifier.setClusterService(clusterService);
		FrameValues values = new FrameValues();
		for (Double windowValue : data) {
			values.add(windowValue);
		}
		classifier.afterCalculated(0L, values);
		classifier.flush();
		return classifier;
	}

}
