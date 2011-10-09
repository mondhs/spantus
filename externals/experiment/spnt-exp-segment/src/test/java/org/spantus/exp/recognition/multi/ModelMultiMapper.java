package org.spantus.exp.recognition.multi;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.ExtNameFilter;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.logger.Logger;

import com.google.common.collect.Lists;

public class ModelMultiMapper extends MultiMapper {

	private static final String EXTENSION = ".wav";
	private static final Logger log = Logger.getLogger(ModelMultiMapper.class);

	@Override
	public void recognize() throws MalformedURLException {
//		((QSegmentExpHsqlDao)getqSegmentExpDao()).setRecreate(true);
//		((QSegmentExpHsqlDao)getqSegmentExpDao()).init();
		int counter = 0;
//		File testDir = new File(testDir.);
		int size = getTestDir().listFiles(new ExtNameFilter(EXTENSION)).length;

//		Double totalTime = 0D;
		for (File signalFile : getTestDir().listFiles(new ExtNameFilter(EXTENSION))) {
			if(!signalFile.getName().startsWith("5-")){
				continue;
			}
			counter++;
			log.error("[recognize]Processing " + counter + " from " + size
					+ "; file = " + signalFile);
			File markerFilePath = new File(getTestDir(),  "test.mspnt.xml");
			log.debug("[recognize]reading: {0}", signalFile);
			MarkerSetHolder markerSetHolder = getExtractor().extract(
					signalFile);
			log.debug("[recognize]extracted: {0}", markerSetHolder);
			IExtractorInputReader reader = getExtractor()
					.createReaderWithClassifier(signalFile);
			MarkerSet ms = findSegementedMarkers(markerSetHolder);
			String snr = signalFile.getName().split("-")[0];

			List<QSegmentExp> markerResults = Lists.newArrayList();
			
			for (Marker marker : ms) {
				Long start = System.currentTimeMillis();
				Map<String, RecognitionResult> recogniton = getExtractor()
						.bestMatchesForFeatures(signalFile.toURI().toURL(),
								marker, reader);
//				String label = getExtractor().createLabelByMarkers(markerFilePath, marker);
				if (recogniton == null) {
					log.debug("[recognize]No matches: {0} ", signalFile);
					continue;
				}
//				marker.setLabel(";"+signalFile.getName().split("-")[0]+";"+marker.getLabel());
				Long processingTime = System.currentTimeMillis() - start;
				QSegmentExp exp = createResult(marker, signalFile, markerFilePath, recogniton, processingTime, snr);
				markerResults.add(exp);
			}
			//finds two segments for same sylable and after saves resultes
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
			saveResult(exp );
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
			if(prevExp == null){
				prevExp = exp;
				continue;
			}
			if(exp.getManualName().equals(prevExp.getManualName())){
				QSegmentExp minExp = exp.getLength()>prevExp.getLength()?prevExp:exp;
				long distanceToCurrent = Math.abs(prevExp.getStart()+prevExp.getLength()-exp.getStart());
				long distanceToPrev = Math.abs(exp.getStart()+exp.getLength()-prevExp.getStart());
				if(distanceToCurrent<50){
					minExp.setMarkerLabel("D;" + minExp.getMarkerLabel());
				}else if(distanceToPrev<50){
					minExp.setMarkerLabel("D;" + minExp.getMarkerLabel());
				}
			}
			prevExp = exp;
		}
		return markerResults;
	}
}
