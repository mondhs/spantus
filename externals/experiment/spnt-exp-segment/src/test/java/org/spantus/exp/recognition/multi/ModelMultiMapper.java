package org.spantus.exp.recognition.multi;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.exp.recognition.filefilter.ExtNameFilter;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;

import com.google.common.collect.Lists;

public class ModelMultiMapper extends MultiMapper {

	private static final Logger log = Logger.getLogger(ModelMultiMapper.class);

	@Override
	public void recognize() throws MalformedURLException {
		log.error("[recognize]Config " + getExpConfig());
		int counter = 0;
		ExtNameFilter audioNameFilter = new ExtNameFilter(getExpConfig().getAudioFilePrefix());
		File[] files = getExpConfig().getTestDirAsFile().listFiles(audioNameFilter);
		int size = files.length;
		for (File signalFile : files) {
//			if (!signalFile.getName().endsWith("_0_1.wav") 
////					&& !signalFile.getName().endsWith("_1.wav") 
//					) {
//				continue;
//			}
//			Double coef = 2.D;
////			if(signalFile.getName().contains("s_30_")){
////				coef =9.D;
////			}
//			
			
			counter++;
			log.error("[recognize]Processing " + counter + " from " + size
					+ "; file = " + signalFile);
			File markerFilePath = new File(getExpConfig().getTestDirAsFile(),
					FileUtils.replaceExtention(signalFile, getExpConfig().getMarkerFilePrefix()));
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
			snr = snr.replaceAll(".wav", "");

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
	 * Just changes duplicate labels
	 * 
	 * @param markerResults
	 * @return return stame as input.
	 */
	private List<QSegmentExp> fixDuplicate(List<QSegmentExp> markerResults) {
		//
		groupDouplicates(markerResults);
//		for (List<QSegmentExp> duplicates : duplicateList) {
//				markerResults.addAll(duplicates);
//		}
		return markerResults;
	}
	/**
	 * 
	 * @param markerResults
	 * @return
	 */
	private List<List<QSegmentExp>> groupDouplicates(
			List<QSegmentExp> markerResults) {
		QSegmentExp prevExp = null;
		List<List<QSegmentExp>> duplicateList = new ArrayList<List<QSegmentExp>>(markerResults.size());
		int counter = 0;
		for (QSegmentExp exp : markerResults) {
			if (prevExp == null) {
				prevExp = exp;
				duplicateList.add(new LinkedList<QSegmentExp>());
				duplicateList.get(0).add(exp);
				counter++;
				continue;
			}
			if(duplicateList.size()-1<counter){
				duplicateList.add(new LinkedList<QSegmentExp>());
			}
			
			if (exp.getManualName().equals(prevExp.getManualName())) {
				duplicateList.get(counter-1).add(exp);
			}else{
				duplicateList.get(counter).add(exp);
				markDuplicates(duplicateList.get(counter-1));
				counter++;
			}
			prevExp = exp;
		}
		markDuplicates(duplicateList.get(counter-1));
		return duplicateList;
	}
	/**
	 * 
	 * @param list
	 */
	private void markDuplicates(List<QSegmentExp> list) {
		if(list.size()<=1){
			return;
		}
		List<QSegmentExp> clone = new ArrayList<QSegmentExp>();
		clone.addAll(list);
		Collections.sort(clone, new Comparator<QSegmentExp>() {
			@Override
			public int compare(QSegmentExp o1, QSegmentExp o2) {
				if(o1.getLength()>o2.getLength()){
					return -1;
				}else if(o1.getLength()<o2.getLength()){
					return 1;
				}
				return 0;
			}
		});
		QSegmentExp theSegmentExp = clone.get(0);
		for (QSegmentExp qSegmentExp : clone) {
			if(theSegmentExp.equals(qSegmentExp)){
				continue;
			}
			qSegmentExp.setMarkerLabel("D;" + qSegmentExp.getMarkerLabel());
		}
		
		
	}
	
}
