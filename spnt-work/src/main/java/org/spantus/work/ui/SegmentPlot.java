package org.spantus.work.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.DefaultAudioReader;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.utils.Assert;

public class SegmentPlot extends AbstractSegmentPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	
	Logger log = Logger.getLogger(getClass());
	DefaultAudioReader audioReader = (DefaultAudioReader)AudioFactory.createAudioReader();

	public static final String DEFAULT_FILE_NAME = 
		"../data/t_1_2.wav"
//		"../data/test/1_2_l.wav"
//		"/home/studijos/wav/ijunk_isjunk_wav/ijunk_isjunk.wav"
//		"/home/studijos/wav/ijunk_isjunk_wav/white_ijunk_isjunk.wav"
//		"/home/studijos/wav/on_off_up_down_wav/on_off_up_down.wav"
//		""
		;
	
//	private File wavFile = null;
	
	public URL fileUrl = null;

	/**
	 * Demo
	 */

	public SegmentPlot(URL fileUrl) {
		Assert.isTrue(fileUrl!=null,"filePath can not be null");
//		IExtractorInputReader reader = null;
		this.fileUrl = fileUrl;
		
		try {
			readSignal();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (getReader() == null) {
			throw new RuntimeException();
		}
		
		initGraph(getReader());
	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		setReader(ExtractorsFactory.createReader(getFormat()));
		getReader().getConfig().setBufferSize(3000);
		
		DecisionSegmentatorOnline multipleSegmentator = 
//			createSegmentatorRecordable();
			createSegmentatorDefault();
		
		
		IClassifier segmentator = null;
		segmentator =ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.ENERGY_EXTRACTOR); 
		segmentator.addClassificationListener(multipleSegmentator);
//		segmentator.setCoef(2f);
		
		segmentator = ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.WAVFORM_EXTRACTOR);
//		segmentator.addClassificationListener(multipleSegmentator);

		segmentator = ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR);
		segmentator.addClassificationListener(multipleSegmentator);
//		segmentator.setCoef(2f);
		
//		segmentator = OnlineSegmentationUtils.register(bufferedReader, ExtractorEnum.LPC_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);

		
//		segmentator = OnlineSegmentationUtils.register(bufferedReader, ExtractorEnum.LOUDNESS_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(1.1f);

//		segmentator = OnlineSegmentationUtils.register(bufferedReader, ExtractorEnum.ENERGY_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(0.9f);

//		segmentator = OnlineSegmentationUtils.register(bufferedReader, ExtractorEnum.CROSSING_ZERO_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(0.9f);

		audioReader.readAudio(getFileUrl(), getWraperExtractorReader());
		log.debug("Markers: " + multipleSegmentator.getMarkSet().getMarkers());
		return getReader();
	}
	/**
	 * 
	 */
	@Override
	public AudioFormat getFormat() {
		return audioReader.getAudioFormat(getFileUrl()).getFormat();
	}

	public static void main(String[] args) {
		String filePath = DEFAULT_FILE_NAME;
		if(args.length > 0){
			filePath = args[0];
		}
		File file = new File(filePath);
		Assert.isTrue(file.exists(), "File not exists");
		URL urlFile = null;
		try {
			urlFile = file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		
		AbstractSegmentPlot segmentPlot = new SegmentPlot(urlFile);
		segmentPlot.showChartFrame();
	}

	public URL getFileUrl() {
		return fileUrl;
	}


}
