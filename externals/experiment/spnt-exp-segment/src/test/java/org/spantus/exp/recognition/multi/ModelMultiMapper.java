package org.spantus.exp.recognition.multi;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.ExtNameFilter;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.logger.Logger;

public class ModelMultiMapper extends MultiMapper {

	private static final Logger log = Logger.getLogger(ModelMultiMapper.class);

	@Override
	public void recognize() throws MalformedURLException {
//		((QSegmentExpHsqlDao)getqSegmentExpDao()).setRecreate(true);
//		((QSegmentExpHsqlDao)getqSegmentExpDao()).init();
		int counter = 0;
		File testDir = new File("/home/mgreibus/tmp/garsyno.modelis/TEST");
		int size = testDir.listFiles(new ExtNameFilter("txt")).length;

//		Double totalTime = 0D;
		for (File signalFile : testDir.listFiles(new ExtNameFilter(".txt"))) {
//			if(!signalFile.getName().startsWith("15-")){
//				continue;
//			}
			counter++;
			log.error("[recognize]Processing " + counter + " from " + size
					+ "; file = " + signalFile);
			File markerFilePath = new File(testDir,  "test.mspnt.xml");
			log.debug("[recognize]reading: {0}", signalFile);
			MarkerSetHolder markerSetHolder = getExtractor().extract(
					signalFile);
			log.debug("[recognize]extracted: {0}", markerSetHolder);
			IExtractorInputReader reader = getExtractor()
					.createReaderWithClassifier(signalFile);
			MarkerSet ms = findSegementedMarkers(markerSetHolder);

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
				// save result
				saveResult(marker, signalFile, markerFilePath, recogniton, processingTime);
			}

		}
		log.debug("[recognize]read files: {0}", counter);
	}
}
