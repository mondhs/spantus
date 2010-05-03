package org.spantus.extract.segments.online.test;

import junit.framework.Assert;

import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.threshold.test.ExtremeClassifierTest;
import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.online.ExtremeOnlineClassifier;
import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extract.segments.online.cluster.ExtremeOnlineClusterServiceSimpleImpl;
import org.spantus.extract.segments.online.rule.ClassifierPostProcessServiceBaseImpl;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extract.segments.online.rule.ClassifierRuleBaseServiceImpl;
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
	public void testOnlineExtremeSegmentExtraction() throws Exception {
		ExtremeOnlineClassifier classifier = null;
		
		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		
		classifier  = feedData(ExtremeClassifierTest.empty, ruleBaseService);
		Assert.assertEquals(0, classifier.getExtremeSegments().size());

		classifier = feedData(ExtremeClassifierTest.singleMax, ruleBaseService);
		Assert.assertEquals(1, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull("start entry null for " +es, es.getStartEntry());
			Assert.assertNotNull("Peak entry null for " +es, es.getPeakEntry());
			Assert.assertNotNull("end entry null for " +es, es.getEndEntry());
		}
		


		classifier = feedData(ExtremeClassifierTest.doubleMax, ruleBaseService);
		Assert.assertEquals(2, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull(es.getStartEntry());
			Assert.assertNotNull(es.getPeakEntry());
			Assert.assertNotNull(es.getEndEntry());
		}


		classifier = feedData(ExtremeClassifierTest.complexMinMax, ruleBaseService);
		Assert.assertEquals(12, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull(es.getStartEntry());
			Assert.assertNotNull(es.getPeakEntry());
			Assert.assertNotNull(es.getEndEntry());
		}


	}
	
	@Test
	public void testOnlineMarkersExtractionBase() throws Exception {
		ExtremeOnlineClassifier classifier = null;

		ClassifierRuleBaseService ruleBaseService = new ClassifierPostProcessServiceBaseImpl();
		
		classifier  = feedData(ExtremeClassifierTest.empty, ruleBaseService);
		Assert.assertEquals(0, classifier.getMarkSet().getMarkers().size());

		classifier = feedData(ExtremeClassifierTest.singleMax, ruleBaseService);
		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
		
		classifier = feedData(ExtremeClassifierTest.doubleMax, ruleBaseService);
		Assert.assertEquals(2, classifier.getMarkSet().getMarkers().size());

		classifier = feedData(ExtremeClassifierTest.complexMinMax, ruleBaseService);
		Assert.assertEquals(12, classifier.getMarkSet().getMarkers().size());

	}
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOnlineMarkersExtractionRule() throws Exception {
		ExtremeOnlineClassifier classifier = null;
		ExtremeOnlineClusterService clusterService = new ExtremeOnlineClusterServiceSimpleImpl(); 
		
		ClassifierRuleBaseServiceImpl ruleBaseService = new ClassifierRuleBaseServiceImpl();
		ruleBaseService.setClusterService(clusterService);
		
		classifier  = feedData(ExtremeClassifierTest.empty, ruleBaseService, clusterService);
		Assert.assertEquals(0, classifier.getMarkSet().getMarkers().size());
		
		classifier = feedData(ExtremeClassifierTest.singleMax, ruleBaseService, clusterService);
		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
		
		classifier = feedData(ExtremeClassifierTest.doubleMax, ruleBaseService, clusterService);
		Assert.assertEquals(2, classifier.getMarkSet().getMarkers().size());


	}
	
	@Test 
	public void testMarkersExtractionRuleComplex() throws Exception {
		ExtremeOnlineClassifier classifier = null;
		ExtremeOnlineClusterService clusterService = new ExtremeOnlineClusterServiceSimpleImpl(); 
		ClassifierRuleBaseServiceImpl ruleBaseService = new ClassifierRuleBaseServiceImpl();
		ruleBaseService.setClusterService(clusterService);
		
		classifier = feedData(ExtremeClassifierTest.complexMinMax, ruleBaseService, clusterService);
		log.debug("[testOnlineMarkersExtractionRule] markers {0}",classifier.getMarkSet().getMarkers());

		Assert.assertEquals(10, classifier.getExtremeSegments().size());
		
		
		Assert.assertEquals(10, classifier.getMarkSet().getMarkers().size());
	}
	
	protected ExtremeOnlineClusterService createClusterService(){
		return new ExtremeOnlineClusterServiceSimpleImpl();
	}

	
	protected void logData(Float[] data){
		StringBuilder sb = new StringBuilder();
		for (Float f1 : data) {
			sb.append(";").append(f1);
//			sb.append(f1).append("\n");
		}
		log.debug("[logData] arr: \n" +sb);
		
	}
	protected ExtremeOnlineClassifier feedData(Float[] data) {
		return feedData(data, null, createClusterService());
	}
	protected ExtremeOnlineClassifier feedData(Float[] data, 
			ClassifierRuleBaseService ruleBase) {
		return feedData(data, ruleBase, createClusterService());
	}
		
	protected ExtremeOnlineClassifier feedData(Float[] data, 
			ClassifierRuleBaseService ruleBase, 
			ExtremeOnlineClusterService clusterService) {
		logData(data);
		ExtremeOnlineClassifier classifier = new ExtremeOnlineClassifier();
		classifier.setExtractor(new MockOnlineExtractor());
		classifier.getExtractor().getOutputValues().setSampleRate(100);//10ms
		classifier.setRuleBaseService(ruleBase);
		classifier.setClusterService(clusterService);
		FrameValues values = new FrameValues();
		for (Float windowValue : data) {
			values.add(windowValue);
		}
		classifier.afterCalculated(0L, values);
		classifier.flush();
		return classifier;
	}

}
