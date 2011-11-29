package org.spantus.exp.recognition.multi;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.ExtNameFilter;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;

import com.google.common.collect.Lists;

public class ModelMultiMapper extends MultiMapper {

	private static final String EXTENSION = ".wav";
	private static final Logger log = Logger.getLogger(ModelMultiMapper.class);

	@Override
	public void recognize() throws MalformedURLException {
		int counter = 0;
		int size = getExpConfig().getTestDirAsFile().listFiles(
				new ExtNameFilter(EXTENSION)).length;

		for (File signalFile : getExpConfig().getTestDirAsFile().listFiles(
				new ExtNameFilter(EXTENSION))) {
//			if (!signalFile.getName().endsWith("10_44.wav") 
////					&& !signalFile.getName().endsWith("_1.wav") 
//					) {
//				continue;
//			}
			Double coef = 7.D;
			if(signalFile.getName().contains("s_30_")){
				coef =9.D;
			}
			
			ExpConfig.updateCoefAndModifier(getExpConfig(),coef , getExpConfig().getModifier());
			
			counter++;
			log.error("[recognize]Processing " + counter + " from " + size
					+ "; file = " + signalFile);
			File markerFilePath = new File(getExpConfig().getTestDirAsFile(),
					FileUtils.replaceExtention(signalFile, ".mspnt.xml"));
			if(!markerFilePath.exists()){
				 markerFilePath = new File(getExpConfig().getTestDirAsFile(),
							"test.mspnt.xml");
				
			}
			log.debug("[recognize]reading: {0}", signalFile);
			IExtractorInputReader reader = getExtractor()
					.createReaderWithClassifier(signalFile);
			MarkerSetHolder markerSetHolder = getExtractor().extract(
					signalFile, reader);
			log.debug("[recognize]extracted: {0}", markerSetHolder);

			MarkerSet ms = findSegementedMarkers(markerSetHolder);
			String snr = signalFile.getName().split("_")[2];

			List<QSegmentExp> markerResults = Lists.newArrayList();

			for (Marker marker : ms) {
				Long start = System.currentTimeMillis();
				Map<String, RecognitionResult> recogniton = getExtractor()
						.bestMatchesForFeatures(signalFile.toURI().toURL(),
								marker, reader);
				// String label =
				// getExtractor().createLabelByMarkers(markerFilePath, marker);
				if (recogniton == null) {
					log.debug("[recognize]No matches: {0} ", signalFile);
					continue;
				}
				// marker.setLabel(";"+signalFile.getName().split("-")[0]+";"+marker.getLabel());
				Long processingTime = System.currentTimeMillis() - start;
				QSegmentExp exp = createResult(marker, signalFile,
						markerFilePath, recogniton, processingTime, snr);
				if(exp!=null){
					markerResults.add(exp);
				}
			}
			// finds two segments for same sylable and after saves resultes
			saveExps(fixDuplicate(markerResults));

		}
		log.debug("[recognize]read files: {0}", counter);
	}

	/**
	 * 
	 * @param markerResults
	 */
	private void saveExps(List<QSegmentExp> markerResults) {
		for (QSegmentExp exp : markerResults) {
			saveResult(exp);
		}
	}

	/**
	 * 
	 * @param markerResults
	 * @return
	 */
	private List<QSegmentExp> fixDuplicate(List<QSegmentExp> markerResults) {
		QSegmentExp prevExp = null;
		for (QSegmentExp exp : markerResults) {
			if (prevExp == null) {
				prevExp = exp;
				continue;
			}
			if (exp.getManualName().equals(prevExp.getManualName())) {
				QSegmentExp minExp = exp.getLength() > prevExp.getLength() ? prevExp
						: exp;
				long distanceToCurrent = Math.abs(prevExp.getStart()
						+ prevExp.getLength() - exp.getStart());
				long distanceToPrev = Math.abs(exp.getStart() + exp.getLength()
						- prevExp.getStart());
				if (distanceToCurrent < 50) {
					minExp.setMarkerLabel("D;" + minExp.getMarkerLabel());
				} else if (distanceToPrev < 50) {
					minExp.setMarkerLabel("D;" + minExp.getMarkerLabel());
				}
			}
			prevExp = exp;
		}
		return markerResults;
	}
}
