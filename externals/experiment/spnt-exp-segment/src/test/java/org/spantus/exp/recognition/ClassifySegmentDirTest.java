/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

import org.junit.Test;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.math.services.javaml.JavaMLSupport;
import org.spantus.math.services.javaml.SpantusSimilarity;
import org.spantus.math.services.javaml.VectorInstnace;

/**
 * 
 * @author mondhs
 */
public class ClassifySegmentDirTest extends AbstractSegmentDirTest {

	private static final Logger log = Logger
			.getLogger(ClassifySegmentDirTest.class);

	
	@Test
	public void testClassify() {
		Dataset data = new DefaultDataset();
		for (CorpusFileEntry entry : getCorpusRepository().getRepository()
				.values()) {
			IValues values = entry.getFeatureMap().get(
					ExtractorEnum.PLP_EXTRACTOR.name()).getValues();
			FrameVectorValues vectors = (FrameVectorValues) values;
	        TimeSeries tsSample = JavaMLSupport.toTimeSeries(vectors, vectors.getDmention());

	        VectorInstnace instance = new VectorInstnace();
	        instance.setTimeSeries(tsSample);
//	        	JavaMLSupport.createInstanceVectors(vectors, vectors.getDmention());
			instance.setClassValue(entry.getName());
			data.add(instance);
			log.debug(entry.getName());
		}
		KNearestNeighbors knn = new KNearestNeighbors(2, new SpantusSimilarity());
		knn.buildClassifier(data);
		CrossValidation cv = new CrossValidation(knn);
//		Map<Object, PerformanceMeasure> p = cv.crossValidation(data);
//		log.debug(p.toString());
		
		int correct = 0, wrong = 0;
		for (Instance instance : data) {
			 Object predictedClassValue = knn.classify(instance);
			log.debug("[testClassify] {0} == {1}" , instance.classValue(), predictedClassValue);
		}

	}

}

