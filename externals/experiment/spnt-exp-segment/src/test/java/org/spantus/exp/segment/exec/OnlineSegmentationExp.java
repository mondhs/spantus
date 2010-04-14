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
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.offline.SimpleDecisionSegmentatorServiceImpl;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.OnlineSegmentaitonService;

public class OnlineSegmentationExp extends DecisionSegmentationExp {

	private ISegmentatorService simpleSegmentator;

	private SimpleDecisionSegmentatorServiceImpl decisionSegmentator;

	protected String getGeneratePath() {
		return super.getGeneratePath() + "online/";
	}

	@Override
	public List<ComparisionResult> compare() {
		List<ComparisionResult> results = new ArrayList<ComparisionResult>();

		MarkerSet experMS = getWordMarkerSet(getExpertMarkerSet());

		experMS.getMarkers();
		
		ProcessReaderInfo processReaderInfo = new ProcessReaderInfo();
		Double[] threasholdArr = new Double[] {
				1.4,
//				0.8, 
		// 1.4,
		// 1.6,
//		1.8,
		// 2.0,
		// 2.2
		};

		Long[] segmentLengths = new Long[] {120L, 
		// 0L
		// 50L, 100L,
		// 100L
		};
		Long[] segmentsSpaces = new Long[] {60L, 
		// 0L
		// 30L, 100L,
		// 20L
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
		for (Double thresholdVal : threasholdList) {
			SampleInfo info = getProcessReader().processReader(getTestReader(),
					processReaderInfo);
			processReaderInfo.setThresholdCoef(thresholdVal);

			Set<IClassifier> set = new HashSet<IClassifier>(info.getThresholds());
			for (IClassifier threshold : set) {

				if (threshold.getName().contains("MFCC")
						|| threshold.getName().contains("FFT")
						|| threshold.getName().contains("LPC")
						|| threshold.getName().contains("AUTOCORRELATION")
						|| threshold.getName().contains("SPECTRAL_CENTROID")
						|| threshold.getName().contains("LOG_ATTACK_TIME")
						|| threshold.getName().contains("SPECTRAL_ENTROPY"))
					continue;

				// if(!threshold.getName().contains("SIGNAL_ENTROPY")) continue;

				info.getThresholds().clear();
				info.getThresholds().add(threshold);
				OnlineDecisionSegmentatorParam onlineParam = new OnlineDecisionSegmentatorParam();
				onlineParam.setExpandStart(0L);

				for (int i = 0; i < segmentsSpaces.length; i++) {
					Long segmentsSpace = segmentsSpaces[i];
					Long segmentLength = segmentLengths[i];
					segmentLength.longValue();
					onlineParam.setMinSpace(segmentsSpace);
					onlineParam.setMinLength(segmentsSpace);

					MarkerSetHolder simpleMS = getSimpleSegmentator()
					 .extractSegments(info.getThresholds());
					MarkerSetHolder onlineMS = getOnlineSegmentator()
							.extractSegments(info.getThresholds(), onlineParam);
					
					ComparisionResult result = getMakerComparison().compare(
//							experMS
							 simpleMS
							, 
							onlineMS
//					 		simpleMS
							);

					if (signal != null) {
						result.setSignal(signal);
					}
					result.setThreshold(threshold);

					// result.setSequenceResult(getVals(info.getThresholds()
					// .iterator().next().getOutputValues()));

					result.setName(getProcessReader().getName(info.getThresholds().iterator()
							.next()));
					results.add(result);
					log.debug("Result:" + 
							result.getName()
							+ result.getTotalResult());
				}

			}
			Float sumTotal = 0f;
			Float sumSqrTotal = 0f;

			for (ComparisionResult r : results) {
				sumTotal += r.getTotalResult();
				sumSqrTotal += (r.getTotalResult() * r.getTotalResult());
			}
			int n = results.size();
			Float meanTotal = sumTotal/n;
			
			Float varTotal = (sumSqrTotal - (n*meanTotal*meanTotal))/(n-1);
			
			
//			s: : m=0.16849764; var=0.0022765219
//			m: m=0.15642011; var=0.0042884587
			log.error("total Results: m=" + meanTotal + "; var=" + varTotal);

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
		Float min = Float.MAX_VALUE, max = -Float.MAX_VALUE;
		for (Float float1 : vals) {
			min = Math.min(min, float1);
			max = Math.max(max, float1);
		}
		newVals.setSampleRate(vals.getSampleRate());
		Float delta = max - min;
		for (Float float1 : vals) {
			newVals.add((float1 - min) / delta);
		}
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

		float sr = result.getThreshold().getOutputValues().getSampleRate();
		sr *= 0.95;
		XYSeriesCollection[] collections = new XYSeriesCollection[4];
		for (int i = 0; i < collections.length; i++) {
			collections[i] = new XYSeriesCollection();
		}

		float i = 0;
		// series[0].setDescription("Signal");
		// for (Float f1 : result.getSequenceResult()) {
		// series[0].add(Float.valueOf(i/4200), f1);
		// i++;
		// }

		series = new XYSeries("Comparision");
		series = newSeries("Result" + result.getTotalResult(), collections[0]);
		for (Float f1 : result.getSequenceResult()) {
			series.add(Float.valueOf(i / sr), f1);
			i++;
		}
		// if(result.getSignal() != null){
		// series = getSignal(result.getSignal());
		// }
		// `collections[0].addSeries(series);

		i = 0;
		series = newSeries("Expert", collections[2]);
		for (Float f1 : result.getOriginal()) {
			series.add(Float.valueOf(i / sr), f1);
			i++;
		}

		i = 0;
		series = newSeries("Online", collections[1]);
		for (Float f1 : result.getTest()) {
			series.add(Float.valueOf(i / sr), f1);
			i++;
		}

		i = 0;
		series = newSeries("Feture", collections[3]);
		for (Float f1 : result.getThreshold().getOutputValues()) {
			series.add(Float.valueOf(i / sr), f1);
			i++;
		}

		i = 0;
		series = newSeries("Threshold", collections[3]);
		for (Float f1 : result.getThreshold().getThresholdValues()) {
			series.add(Float.valueOf(i / sr), f1);
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

	public ISegmentatorService getOnlineSegmentator() {
		if (simpleSegmentator == null) {
			simpleSegmentator = new OnlineSegmentaitonService();
		}
		return simpleSegmentator;
	}

	public static void main(final String[] args) {
//		String testPath = DEFAULT_TEST_DATA_PATH;
//		String expertPath = DEFAULT_EXPERT_MARKS_PATH;
//		if (args.length == 2) {
//			expertPath = args[0];
//			testPath = args[1];
//		}
		String expertPath = "c:/home/studijos/wav/on_off_up_down_wav/on_off_up_down.mspnt.xml";
//		String _testPath = "E:/home/studijos/wav/on_off_up_down_wav/on_off_up_down.sspnt.xml";
		String _testPath = "c:/home/studijos/wav/on_off_up_down_wav/on_off_up_down_8.sspnt.xml";
//		String _testPath = "E:/home/studijos/wav/on_off_up_down_wav/shower_on_off_up_down.prep.sspnt.xml";

		new OnlineSegmentationExp().process(expertPath, _testPath);
	}

	class ExComparisionResult extends ComparisionResult {

		FrameValues values;
	}

}
