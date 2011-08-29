package org.spantus.exp.recognition.multi;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;

import org.mvel2.conversion.SetCH;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.ExtNameFilter;
import org.spantus.exp.recognition.dao.QSegmentExpHsqlDao;
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

				if (recogniton == null) {
					log.debug("[recognize]No matches: {0} ", signalFile);
					continue;
				}
				// log.debug("[testClassify]matching: {0} => {1} === {2}",
				// wavFilePath,
				// marker.getLabel(),
				// recogniton
				// );
				marker.setLabel(";"+signalFile.getName().split("-")[0]+";"+marker.getLabel());
				Long processingTime = System.currentTimeMillis() - start;
				// save result
				saveResult(marker, markerFilePath, recogniton, processingTime);
			}
//			totalTime += getAudioManager().findLength(
//					wavFilePath.toURI().toURL());
//			log.debug("[recognize]read markers: {0}=>{1}  [totalTime: {2}]",
//					wavFilePath, ms.getMarkers().size(), totalTime);
		}
		log.debug("[recognize]read files: {0}", counter);
	}
}
