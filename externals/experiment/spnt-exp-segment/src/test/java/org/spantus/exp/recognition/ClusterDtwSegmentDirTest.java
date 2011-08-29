/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
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

import scikit.util.Pair;

/**
 * 
 * @author mondhs
 */
public class ClusterDtwSegmentDirTest extends AbstractSegmentDirTest {

	private static final Logger log = Logger
			.getLogger(ClusterDtwSegmentDirTest.class);

	@Test
	public void testClassify() {
		Dataset data = new DefaultDataset();

		List<VectorInstnace> aList = new ArrayList<VectorInstnace>();
		List<VectorInstnace> eList = new ArrayList<VectorInstnace>();

		for (CorpusFileEntry entry : getCorpusRepository().getRepository()
				.values()) {
			IValues values = entry.getFeatureMap()
					.get(ExtractorEnum.LOUDNESS_EXTRACTOR.name()).getValues();
			FrameVectorValues vectors = (FrameVectorValues) values;
			TimeSeries tsSample = JavaMLSupport.toTimeSeries(vectors,
					vectors.getDimention());

			VectorInstnace instance = new VectorInstnace();
			instance.setId(entry.getId().toString());
			instance.setTimeSeries(tsSample);
			// JavaMLSupport.createInstanceVectors(vectors,
			// vectors.getDmention());
			if ("a".equals(entry.getName())) {
				aList.add(instance);
			} else if ("e".equals(entry.getName())) {
				eList.add(instance);
			} else {
				throw new IllegalArgumentException();
			}
			// instance.setClassValue(entry.getName());
			// data.add(instance);
			log.debug(entry.getName());
		}
		Map<String, Pair<Double, Double>> eLengths = new LinkedHashMap<String,  Pair<Double, Double>>();
		for (VectorInstnace eInstnace : eList) {
			Double aSum = 0D;
			int aCount = 0;
			for (VectorInstnace aInstnace : aList) {
				aSum += calcualte(eInstnace.getTimeSeries(), aInstnace.getTimeSeries());
				aCount++;
			}
			Double eSum = 0D;
			int eCount = 0;
			for (VectorInstnace eNInstnace : eList) {
				if(eNInstnace.getId().equals(eInstnace.getId())){
					continue;
				}
				eSum += calcualte(eInstnace.getTimeSeries(), eNInstnace.getTimeSeries());
				eCount++;
			}
			eLengths.put(eInstnace.getId(), Pair.newPair(aSum / aCount, eSum / eCount));
		}


		Map<String, Pair<Double, Double>> aLengths = new LinkedHashMap<String,  Pair<Double, Double>>();
		for (VectorInstnace aNInstnace : aList) {
			Double aSum = 0D;
			int aCount = 0;
			for (VectorInstnace aInstnace : aList) {
				if(aNInstnace.getId().equals(aInstnace.getId())){
					continue;
				}
				aSum += calcualte(aNInstnace.getTimeSeries(), aInstnace.getTimeSeries());
				aCount++;
			}
			Double eSum = 0D;
			int eCount = 0;
			for (VectorInstnace eInstnace : eList) {
				eSum += calcualte(eInstnace.getTimeSeries(), aNInstnace.getTimeSeries());
				eCount++;
			}
			aLengths.put(aNInstnace.getId(), Pair.newPair(aSum / aCount, eSum / eCount));
		}
		
		for (Entry<String, Pair<Double, Double>> vectorInstnace : eLengths.entrySet()) {
			System.out.printf("%s\te\t %s\t%s\n", vectorInstnace.getKey(), 
					vectorInstnace.getValue().fst(), vectorInstnace.getValue().snd());
		}
		for (Entry<String, Pair<Double, Double>> vectorInstnace : aLengths.entrySet()) {
			System.out.printf("%s\ta\t %s\t%s\n", vectorInstnace.getKey(), 
					vectorInstnace.getValue().fst(), vectorInstnace.getValue().snd());
		}
	}
	/**
	 * 
	 * @param tsSample
	 * @param tsTarget
	 * @return
	 */
	Double calcualte(TimeSeries tsSample, TimeSeries tsTarget){
		double twi = DTW
				.getWarpDistBetween(
						tsSample,
						tsTarget,
						org.spantus.math.dtw.DtwServiceJavaMLImpl
								.createSearchWindow(
										tsSample,
										tsTarget,
										3,
										org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow.ExpandedResWindow));
		return twi;
	}

}
