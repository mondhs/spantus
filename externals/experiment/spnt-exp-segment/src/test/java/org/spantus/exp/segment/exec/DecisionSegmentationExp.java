/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.exp.segment.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.spantus.core.FrameValues;
import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.draw.AbstractGraphGenerator;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.offline.BaseDecisionSegmentatorParam;
import org.spantus.segment.offline.MergeSegmentatorServiceImpl;
import org.spantus.segment.offline.SimpleDecisionSegmentatorServiceImpl;

public class DecisionSegmentationExp extends AbstractGraphGenerator {

	private ISegmentatorService simpleSegmentator;

	private SimpleDecisionSegmentatorServiceImpl decisionSegmentator;

	protected String getGeneratePath() {
		return super.getGeneratePath() + "decision/";
	}

	@Override
	public List<ComparisionResult> compare() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();

		// MarkerSet experMS = getWordMarkerSet(getExpertMarkerSet());
		// Double thresholdCoef = 1.2;

		ProcessReaderInfo processReaderInfo = new ProcessReaderInfo();
		Double[] threasholdArr = new Double[] {
//		 1.4,
		 1.6, 
		 1.8,
//		 2.0 
		};

		Long[] segmentLengths = new Long[] { 40L, 
//				50L, 50L, 
				};
		Long[] segmentsSpaces = new Long[] { 30L, 
//				30L, 40L, 
				};

		List<Double> threasholdList = Arrays.asList(threasholdArr);
		FrameValues signal = null;
		for (IExtractor extractor : getTestReader().getExtractorRegister()) {
			if (extractor.getName().endsWith(
					ExtractorEnum.SIGNAL_EXTRACTOR.name())) {
				signal = getVals(extractor.getOutputValues());
				break;
			}
		}
		for (Double threashold : threasholdList) {
			SampleInfo info = getProcessReader().processReader(getTestReader(),
					processReaderInfo);
			processReaderInfo.setThresholdCoef(threashold);

			Set<IClassifier> set = new HashSet<IClassifier>(info.getThresholds());
			for (IClassifier threshold : set) {
				info.getThresholds().clear();
				info.getThresholds().add(threshold);
				BaseDecisionSegmentatorParam param = new BaseDecisionSegmentatorParam();

				for (int i = 0; i < segmentsSpaces.length; i++) {
					Long segmentsSpace = segmentsSpaces[i];
					Long segmentLength = segmentLengths[i];
					param.setMinLength(segmentLength);
					param.setMinSpace(segmentsSpace);

					MarkerSetHolder decisionMS = getDecisionSegmentator()
							.extractSegments(info.getThresholds(), param);
					MarkerSetHolder simpleMS = getSimpleSegmentator()
							.extractSegments(info.getThresholds());
					ComparisionResult result = getMakerComparison().compare(
							simpleMS, decisionMS);
					
					if (signal != null) {
						result.setSignal(signal);
					} 
					result.setThreshold(threshold);
					
					result.setSequenceResult(getVals(info.getThresholds()
								.iterator().next().getOutputValues()));
					
					result.setName(getProcessReader().getName(info.getThresholds().iterator()
							.next())
							+ "_" + threashold + "_" +  segmentsSpace + "_" + segmentLength);
					results.add(result);
					log.debug("Result:" + result.getName()
							+ result.getParams());
				}

			}

		}

		return results;
	}

	/**
	 * 
	 * @param vals
	 * @return
	 */
	protected FrameValues getVals(FrameValues vals) {
		FrameValues newVals = new FrameValues();
		newVals.setSampleRate(vals.getSampleRate());
		Float min = Float.MAX_VALUE, max = -Float.MAX_VALUE;
		for (Float float1 : vals) {
			min = Math.min(min, float1);
			max = Math.max(max, float1);
		}
		Float delta = max - min;

		for (Float float1 : vals) {
			newVals.add((float1 - min) / delta);
		}
		// for (float i = 0f; i < vals.size(); i+=1) {
		// Float float1 = vals.get((int)i);
		// newVals.add((float1 - min) / delta);
		// }

		return newVals;
	}

	XYSeries signalSeries;

	protected XYSeries getSignal(FrameValues values) {
		float i = 0f;
		if (signalSeries == null) {
			signalSeries = new XYSeries("Signal");
			for (Float f1 : values) {
				signalSeries.setDescription("Signal");
				signalSeries.add(Float.valueOf(i / 4300), f1);
				i++;
			}
		}
		return signalSeries;
	}

	@Override
	protected XYSeriesCollection[] createSeries(ComparisionResult result) {

		XYSeries series;
		
		XYSeriesCollection[] collections = new XYSeriesCollection[4];
		for (int i = 0; i < collections.length; i++) {
			collections[i] = new XYSeriesCollection();
		}
		

		int i = 0;
		// series[0].setDescription("Signal");
		// for (Float f1 : result.getSequenceResult()) {
		// series[0].add(Float.valueOf(i/4200), f1);
		// i++;
		// }
		
		series = new XYSeries("Signal");
		if(result.getSignal() != null){
			series = getSignal(result.getSignal());
		}
		collections[0].addSeries(series);

		i = 0;
		series = newSeries("Initial", collections[1]);
		for (Float f1 : result.getOriginal()) {
			series.add(Float.valueOf(i / 105), f1);
			i++;
		}
		i = 0;
		series = newSeries("Processed", collections[2]);
		for (Float f1 : result.getTest()) {
			series.add(Float.valueOf(i / 105), f1);
			i++;
		}

		i = 0;
		series = newSeries("Feture", collections[3]);
		for (Float f1 : result.getThreshold().getOutputValues()) {
			series.add(Float.valueOf(i / 105), f1);
			i++;
		}
		
		i = 0;
		series = newSeries("Threshold", collections[3]);
		for (Float f1 : result.getThreshold().getThresholdValues()) {
			series.add(Float.valueOf(i / 105), f1);
			i++;
		}
		
		return collections;

	}
	
	public SimpleDecisionSegmentatorServiceImpl getDecisionSegmentator() {
		if (decisionSegmentator == null) {
			decisionSegmentator = new SimpleDecisionSegmentatorServiceImpl();
			decisionSegmentator.setSegmentator(getSimpleSegmentator());
		}
		return decisionSegmentator;
	}

	public ISegmentatorService getSimpleSegmentator() {
		if (simpleSegmentator == null) {
			simpleSegmentator = new MergeSegmentatorServiceImpl();
		}
		return simpleSegmentator;
	}

	public static void main(final String[] args) {
		String _testPath = "c:/home/studijos/wav/on_off_up_down_wav/on_off_up_down.sspnt.xml";
		if (args.length > 0) {
			_testPath = args[0];
		}
		new DecisionSegmentationExp().process(null, _testPath);
	}

	class ExComparisionResult extends ComparisionResult{
		
		FrameValues values;
	}
	
}
