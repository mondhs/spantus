package org.spantus.extractor.segments.online.test;

import static org.spantus.extractor.segments.online.test.FeedUtill.feedData;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.IClassificationListener;
import org.spantus.core.threshold.LogClassificationListener;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.offline.test.ExtremeClassifierTest;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterServiceSimpleImpl;
import org.spantus.extractor.segments.online.rule.ClassifierRuleBaseServiceBaseImpl;
import org.spantus.logger.Logger;

public class ClassifierRuleBaseServiceOnlineImplTest {

	private static final Logger LOG = Logger
			.getLogger(ClassifierRuleBaseServiceOnlineImplTest.class);

	private static final Double[] singleMaxShortReallife = new Double[] {
			// initial nose
			0.191, 0.19, 0.18, 0.17, 0.16, 0.15, 0.14, 0.13, 0.12,
			0.1, 0.0,
			// 1 segment
			0.1, 0.0, 1.0, 2.0, 3.0, 2.0, 1.0, 0.0, 0.11, 0.12, 0.13 };
	public static final Double[] doubleMaxShortReallife = new Double[] {
			// initial nose
			0.191D, 0.19D, 0.18D, 0.17D, 0.16D, 0.15D, 0.14D, 0.13D, 0.12D,
			0.1D, 0D,
			// segment 1
			1D, 1.2D, 2D, 2.2D, 3D, 2.2D, 2D, 1.2D, 1D, 0.1D, 0D,
			// segment 2
			1D, 2D, 3.1D, 2D, 1.5D, 1D, 0D, 0.11D, 0.12D, 0.13D, };

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
	public static final Double[] complexMinMaxReallife = new Double[] {
			// initial nose
			0.191D, 0.192D, 0.18D, 0.17D, 0.16D, 0.15D, 0.14D, 0.13D, 0.12D,
			0.1D, 0D,
			// segments
			0D, 1D, 0D, 1D, 3D, 2D, 4D, 3D, 6D, 4D, 5D, 3D, 4D, 0D, 1D, 0D, 1D,
			0D, 1.1D, 0D, 1.2D, 0D, 1.3D, 0D, 1.4D, 0D };

	private ClassifierRuleBaseServiceBaseImpl ruleBaseService;

	@Before
	public void setup() {
		ExtremeOnlineClusterService clusterService = new ExtremeOnlineClusterServiceSimpleImpl();
		ruleBaseService = new ClassifierRuleBaseServiceBaseImpl();
		ruleBaseService.setClusterService(clusterService);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldSegment_Empty() throws Exception {
		// given

		// when
		ExtremeOnlineRuleClassifier classifier = feedData(
				ExtremeClassifierTest.empty, ruleBaseService);
		// then
		Assert.assertEquals(0, classifier.getMarkSet().getMarkers().size());
	}

	@Test
	public void shouldSegment_Single() throws Exception {
		// given

		// when
		ExtremeOnlineRuleClassifier classifier = feedData(
				singleMaxShortReallife, ruleBaseService);
		// then
		
		Assert.assertEquals("segments", 1, classifier.getOnlineCtx().getExtremeSegments().size());
		ExtremeSegment firstSegment = classifier.getOnlineCtx().getExtremeSegments().getFirst();
		Assert.assertEquals("start", 100, firstSegment.getStart(), 0);
		Assert.assertEquals("length", 80, firstSegment.getLength(), 0);
//		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
//		Marker marker1 = classifier.getMarkSet().getMarkers().get(0);
//		Assert.assertEquals("start", 100, marker1.getStart(), 0);
//		Assert.assertEquals("length", 80, marker1.getLength(), 0);
	}

	@Test
	public void shouldSegment_Dobule() throws Exception {
		// given

		// when
		ExtremeOnlineRuleClassifier classifier = feedData(
				doubleMaxShortReallife, ruleBaseService);
		// then
		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
		Marker marker1 = classifier.getMarkSet().getMarkers().get(0);
//		Marker marker2 = classifier.getMarkSet().getMarkers().get(1);
		Assert.assertEquals("start", 100, marker1.getStart(), 0);
		Assert.assertEquals("length", 110, marker1.getLength(), 0);
//		Assert.assertEquals("start", 210, marker2.getStart(), 0);
//		Assert.assertEquals("length", 70, marker2.getLength(), 0);
	}
	
	@Test
	public void shouldSegment_Complex() throws Exception {
		// given

		// when
		ExtremeOnlineRuleClassifier classifier = feedData(
				complexMinMaxReallife, ruleBaseService);
		LOG.debug("[testOnlineMarkersExtractionRule] markers {0}", classifier
				.getMarkSet().getMarkers());
		// then
//		Assert.assertEquals(1, classifier.getExtremeSegments().size());
//		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
		Assert.assertEquals("segments", 3, classifier.getOnlineCtx().getExtremeSegments().size());
		Iterator<ExtremeSegment> segIter = classifier.getOnlineCtx().getExtremeSegments().iterator();
		ExtremeSegment firstSegment = segIter.next();
		Assert.assertEquals("start", 0, firstSegment.getStart(), 0);
		Assert.assertEquals("length", 110, firstSegment.getLength(), 0);
		ExtremeSegment secondSegment = segIter.next();
		Assert.assertEquals("start", 110, secondSegment.getStart(), 0);
		Assert.assertEquals("length", 110, secondSegment.getLength(), 0);

	}

	@Test
	public void shouldTrigerListener_Double() throws Exception {
		// given
		IClassificationListener classificationListener = Mockito.mock(IClassificationListener.class);
		
		IClassificationListener logListener = new LogClassificationListener();
		

		// when
		ExtremeOnlineRuleClassifier classifier = feedData(
				doubleMaxShortReallife, ruleBaseService, classificationListener, logListener);
		LOG.debug("[testOnlineMarkersExtractionRule] markers {0}", classifier
				.getMarkSet().getMarkers());
		// then
		Assert.assertEquals(1, classifier.getMarkSet().getMarkers().size());
		Marker marker1 = classifier.getMarkSet().getMarkers().get(0);
//		Marker marker2 = classifier.getMarkSet().getMarkers().get(1);
		Assert.assertEquals("start", 100, marker1.getStart(), 0);
		Assert.assertEquals("length", 110, marker1.getLength(), 0);
//		Assert.assertEquals("start", 210, marker2.getStart(), 0);
//		Assert.assertEquals("length", 70, marker2.getLength(), 0);
//		Mockito.verify(classificationListener, Mockito.times(2)).onSegmentEnded(Mockito.any(SegmentEvent.class));
	}

}
