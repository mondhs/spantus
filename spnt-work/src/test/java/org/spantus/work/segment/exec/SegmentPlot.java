package org.spantus.work.segment.exec;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.DefaultAudioReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.segment.online.ThresholdSegmentatorOnline;
import org.spantus.utils.Assert;
import org.spantus.work.segment.OnlineSegmentationUtils;
import org.spantus.work.test.SignalSelectionListenerMock;

public class SegmentPlot extends AbstractSegmentPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	
	Logger log = Logger.getLogger(getClass());
	DefaultAudioReader audioReader = (DefaultAudioReader)AudioFactory.createAudioReader();

	public static final String DEFAULT_FILE_NAME = 
//		"../data/t_1_2.wav"
//		"../data/test/1_2_l.wav"
//		"/home/studijos/wav/ijunk_isjunk_wav/ijunk_isjunk.wav"
//		"/home/studijos/wav/ijunk_isjunk_wav/white_ijunk_isjunk.wav"
//		"/home/studijos/wav/on_off_up_down_wav/on_off_up_down.wav"
		""
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (reader == null) {
			throw new RuntimeException();
		}
		chart = ChartFactory.createChart(reader);
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		reader = ExtractorsFactory.createReader(getFormat());
		reader.getConfig().setBufferSize(3000);
		
		MultipleSegmentatorOnline multipleSegmentator = 
			getSegmentatorRecordable();
//			getSegmentatorDefault();
		
		
		
		
		
		ThresholdSegmentatorOnline segmentator = null;
		segmentator = OnlineSegmentationUtils.register(reader, ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR);
		segmentator.setOnlineSegmentator(multipleSegmentator);
		segmentator.setCoef(2f);
		
		segmentator = OnlineSegmentationUtils.register(reader, ExtractorEnum.WAVFORM_EXTRACTOR);
		segmentator.setOnlineSegmentator(multipleSegmentator);

		segmentator = OnlineSegmentationUtils.register(reader, ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR);
		segmentator.setOnlineSegmentator(multipleSegmentator);
		segmentator.setCoef(2f);
		
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

		
		
		audioReader.readAudio(getFileUrl(), wraperExtractorReader);
		log.debug("Markers: " + multipleSegmentator.getMarkSet().getMarkers());
		return reader;
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
		segmentPlot.showChart();
	}

	public URL getFileUrl() {
		return fileUrl;
	}


}
