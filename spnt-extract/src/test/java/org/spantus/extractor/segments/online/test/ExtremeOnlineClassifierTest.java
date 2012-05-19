package org.spantus.extractor.segments.online.test;

import static org.spantus.extractor.segments.online.test.FeedUtill.feedData;
import junit.framework.Assert;

import org.junit.Test;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.offline.test.ExtremeClassifierTest;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterServiceSimpleImpl;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseService;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseServiceSimpleImpl;
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
		ClassifierRuleBaseService ruleBaseService = new ClassifierRuleBaseServiceSimpleImpl();
		classifier  = feedData(ExtremeClassifierTest.empty, ruleBaseService);
		Assert.assertEquals(0, classifier.getExtremeSegments().size());
	}
	
	@Test 
	public void testOnlineExtremeSegmentExtractionSinleMax() throws Exception {
		ExtremeOnlineRuleClassifier classifier = null;
		ClassifierRuleBaseService ruleBaseService = new ClassifierRuleBaseServiceSimpleImpl();
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
		ClassifierRuleBaseService ruleBaseService = new ClassifierRuleBaseServiceSimpleImpl();
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
		ClassifierRuleBaseService ruleBaseService = new ClassifierRuleBaseServiceSimpleImpl();
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
		
		ClassifierRuleBaseService ruleBaseService = new ClassifierRuleBaseServiceSimpleImpl();
		
		classifier = feedData(ExtremeClassifierTest.complexMinMax, ruleBaseService);
		Assert.assertEquals(12, classifier.getExtremeSegments().size());
		for (ExtremeSegment es : classifier.getExtremeSegments()) {
			Assert.assertNotNull(es.getStartEntry());
			Assert.assertNotNull(es.getPeakEntry());
			Assert.assertNotNull(es.getEndEntry());
		}


	}
	
	
	protected ExtremeOnlineClusterService createClusterService(){
		return new ExtremeOnlineClusterServiceSimpleImpl();
	}

	

}
