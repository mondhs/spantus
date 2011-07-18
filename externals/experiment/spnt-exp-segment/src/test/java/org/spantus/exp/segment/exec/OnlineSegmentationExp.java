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
import org.spantus.segment.offline.OfflineSegmentatorServiceImpl;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.OnlineSegmentaitonService;

public class OnlineSegmentationExp extends DecisionSegmentationExp {

//	private ISegmentatorService simpleSegmentator;
        private ISegmentatorService onlineSegmentator;

        
	private OfflineSegmentatorServiceImpl decisionSegmentator;

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
			Double sumTotal = 0D;
			Double sumSqrTotal = 0D;

			for (ComparisionResult r : results) {
				sumTotal += r.getTotalResult();
				sumSqrTotal += (r.getTotalResult() * r.getTotalResult());
			}
			int n = results.size();
			Double meanTotal = sumTotal/n;
			
			Double varTotal = (sumSqrTotal - (n*meanTotal*meanTotal))/(n-1);
			
			
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
		Double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
		for (Double float1 : vals) {
			min = Math.min(min, float1);
			max = Math.max(max, float1);
		}
		newVals.setSampleRate(vals.getSampleRate());
		Double delta = max - min;
		for (Double float1 : vals) {
			newVals.add((float1 - min) / delta);
		}
		return newVals;
	}

	XYSeries signalSeries;

	protected XYSeries getSignal(FrameValues values) {
		Double i = 0D;
		if (signalSeries == null) {
			signalSeries = new XYSeries("Signal");
			for (Double f1 : values) {
				signalSeries.setDescription("Signal");
				signalSeries.add(Double.valueOf(i / 4300), f1);
				i++;
			}
		}
		return signalSeries;
	}

	@Override
	protected XYSeriesCollection[] createSeries(ComparisionResult result) {

		XYSeries series;

		Double sr = result.getThreshold().getOutputValues().getSampleRate();
		sr *= 0.95;
		XYSeriesCollection[] collections = new XYSeriesCollection[4];
		for (int i = 0; i < collections.length; i++) {
			collections[i] = new XYSeriesCollection();
		}

		Double i = 0D;
		// series[0].setDescription("Signal");
		// for (Double f1 : result.getSequenceResult()) {
		// series[0].add(Double.valueOf(i/4200), f1);
		// i++;
		// }

		series = new XYSeries("Comparision");
		series = newSeries("Result" + result.getTotalResult(), collections[0]);
		for (Double f1 : result.getSequenceResult()) {
			series.add(Double.valueOf(i / sr), f1);
			i++;
		}
		// if(result.getSignal() != null){
		// series = getSignal(result.getSignal());
		// }
		// `collections[0].addSeries(series);

		i = 0D;
		series = newSeries("Expert", collections[2]);
		for (Double f1 : result.getOriginal()) {
			series.add(Double.valueOf(i / sr), f1);
			i++;
		}

		i = 0D;
		series = newSeries("Online", collections[1]);
		for (Double f1 : result.getTest()) {
			series.add(Double.valueOf(i / sr), f1);
			i++;
		}

		i = 0D;
		series = newSeries("Feture", collections[3]);
		for (Double f1 : result.getThreshold().getOutputValues()) {
			series.add(Double.valueOf(i / sr), f1);
			i++;
		}

		i = 0D;
		series = newSeries("Threshold", collections[3]);
		for (Double f1 : result.getThreshold().getThresholdValues()) {
			series.add(Double.valueOf(i / sr), f1);
			i++;
		}

		return collections;

	}

	public OfflineSegmentatorServiceImpl getDecisionSegmentator() {
		if (decisionSegmentator == null) {
			decisionSegmentator = new OfflineSegmentatorServiceImpl();
			decisionSegmentator.setSegmentator(getSimpleSegmentator());
		}
		return decisionSegmentator;
	}

	public ISegmentatorService getOnlineSegmentator() {
		if (onlineSegmentator == null) {
			onlineSegmentator = new OnlineSegmentaitonService();
		}
		return onlineSegmentator;
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
