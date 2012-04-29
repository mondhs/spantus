package org.spantus.work.services.calc.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.exception.ProcessingException;
import org.spantus.math.VectorUtils;
import org.spantus.work.services.calc.CalculateSnr;

import scikit.util.Pair;

public class CalculateSnrImpl implements CalculateSnr {


	@Override
	public Double calculate(IExtractor iExtractor, MarkerSet segments) {
		
		List<Double> noise = new LinkedList<Double>();
		List<Double> speech = new LinkedList<Double>();
		Pair<List<Double>, List<Double>> speechNoise= new Pair<List<Double>, List<Double>>(speech, noise);
//		BigDecimal sumSpeech = BigDecimal.ZERO;
//		BigDecimal sumNoise = BigDecimal.ZERO;
		
		
		if(segments == null || segments.getMarkers() == null){
			return 0.0;
		}
		
		speechNoise = extractSpeechNoise(speechNoise, segments, iExtractor.getOutputValues());
		BigDecimal avgSpeech = VectorUtils.avgBigDecimal(speechNoise.fst());
		BigDecimal avgNoise = VectorUtils.avgBigDecimal(speechNoise.snd());
		
		BigDecimal stdSpeech = VectorUtils.stdBigDecimal(speechNoise.fst(),avgSpeech);
		BigDecimal stdNoise = VectorUtils.stdBigDecimal(speechNoise.snd(), avgNoise);
		
		
//		BigDecimal avgSpeech = sumSpeech.divide(BigDecimal.valueOf(speechCount),RoundingMode.HALF_UP);
//		BigDecimal avgNoise = sumNoise.divide(BigDecimal.valueOf(noiseCount),RoundingMode.HALF_UP);
//		BigDecimal avgSpeechDenoised = avgSpeech.subtract(avgNoise);
//		double ratio = (sumSpeech.doubleValue()/speechCount)/(sumNoise.doubleValue()/noiseCount);
//		double ratio = (avgSpeechDenoised.doubleValue())/(avgNoise.doubleValue());
//		double ratio = (avgSpeech-avgNoise)/avgNoise;
		BigDecimal ratio = stdSpeech.divide(stdNoise,RoundingMode.HALF_UP);
		double snr = 10 * Math.log10(ratio.doubleValue());
		return BigDecimal.valueOf(snr).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	/**
	 * 
	 * @param speechNoise
	 * @param segments
	 * @param outputValues
	 * @return
	 */
	protected Pair<List<Double>, List<Double>> extractSpeechNoise(
			Pair<List<Double>, List<Double>> speechNoise, MarkerSet segments,
			FrameValues outputValues) {
		long current=0;
		FrameValues vals = outputValues;
		ListIterator<Double> iter = vals.listIterator();
		for (Marker marker : segments.getMarkers()) {
			int start = vals.toIndex((double) marker.getStart() / 1000);
			int end = vals.toIndex((double) marker.getEnd() / 1000);

			for (long i = current; i < start; i++) {
				if(!iter.hasNext()){
					throw new ProcessingException("No element");
				}
				speechNoise.snd().add(iter.next());
			}
			for (int i = start; i < end; i++) {
				if(!iter.hasNext()){
					throw new ProcessingException("No element");
				}
				speechNoise.fst().add(iter.next());
//				BigDecimal val = BigDecimal.valueOf(iter.next());
//				sumSpeech = sumSpeech.add(val) ;
//				speechCount++;
			}
			current = end;
		}

		for (; iter.hasNext();) {
			speechNoise.snd().add(iter.next());
		}
		return speechNoise;
	}
	
	@Override
	public Map<segmentStatics, Double> calculateStatistics(
			IExtractor iExtractor, Long start, Long length) {
		Map<segmentStatics, Double> result = new HashMap<CalculateSnr.segmentStatics, Double>();
		FrameValues vals = iExtractor.getOutputValues();
		int startIndex = vals.toIndex((double) start / 1000);
		int lengthIndex = vals.toIndex((double) length / 1000);
		ListIterator<Double> iter = vals.listIterator(startIndex);
		Double min = Double.MAX_VALUE;
		Double max = -Double.MAX_VALUE;
		Double sum = 0D;
		for (int i = 0; i < lengthIndex; i++) {
			if(!iter.hasNext()){
				throw new ProcessingException("No element");
			}
			Double val = iter.next();
			min = Math.min(min, val);
			max = Math.max(max, val);
			sum += val;
		}
		result.put(segmentStatics.min, min);
		result.put(segmentStatics.max, max);
		result.put(segmentStatics.mean, sum/lengthIndex);
		return result;
	}

}
