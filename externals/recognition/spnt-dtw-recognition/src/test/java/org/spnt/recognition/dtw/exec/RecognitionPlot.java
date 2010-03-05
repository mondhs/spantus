/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spnt.recognition.dtw.exec;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.DefaultAudioReader;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.core.threshold.AbstractClassifier;
import org.spantus.externals.recognition.segment.RecognitionSegmentatorOnline;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.ExtractorParamUtils;

public class RecognitionPlot extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	
	AbstractSwingChart chart;
	
	Logger log = Logger.getLogger(getClass());

	/**
	 * Demo
	 */

	public RecognitionPlot() {
		IExtractorInputReader reader = null;
		try {
			reader = readSignal();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (reader == null) {
			throw new RuntimeException();
		}
		chart = ChartFactory.createChart(reader);
		chart.initialize();
//		chart.addSignalSelectionListener(new TestSignalSelectionListener());
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		File wavFile = new File("../../../data/t_1_2.wav");
//		File wavFile = new File("../data/test/1_2_l.wav");
//		File wavFile = new File("/home/mindas/src/spnt-code/spnt-work-ui/ijunk_ishjunk/ijunk_isjunk.wav");
		
		URL urlFile = wavFile.toURI().toURL();
		DefaultAudioReader audioReader = (DefaultAudioReader)AudioFactory.createAudioReader();
		ExtractorInputReader bufferedReader = (ExtractorInputReader)ExtractorsFactory.createReader(audioReader.getAudioFormat(urlFile));

		WraperExtractorReader wraperExtractorReader =  new WraperExtractorReader(bufferedReader);
		wraperExtractorReader.setFormat(audioReader.getAudioFormat(urlFile).getFormat());
		
		bufferedReader.getConfig().setBufferSize(3000);
		
		RecognitionSegmentatorOnline multipleSegmentator = new RecognitionSegmentatorOnline(bufferedReader);
		multipleSegmentator.setParam(createParam());
		
		AbstractClassifier segmentator = null;
		ExtractorParam param = new ExtractorParam();
		ExtractorParamUtils.setBoolean(param, 
				ExtractorModifiersEnum.smooth.name(), Boolean.TRUE);
		segmentator = (AbstractClassifier)ExtractorUtils.registerThreshold(bufferedReader, ExtractorEnum.ENERGY_EXTRACTOR, param);
		segmentator.addClassificationListener(multipleSegmentator);
		segmentator.setCoef(4f);
		
//		segmentator = OnlineSegmentationUtils.register(bufferedReader, ExtractorEnum.WAVFORM_EXTRACTOR);
//		segmentator.setOnlineSegmentator(multipleSegmentator);

		ExtractorUtils.register(bufferedReader, ExtractorEnum.LPC_EXTRACTOR, null);
		
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

		
		
		audioReader.readAudio(urlFile, wraperExtractorReader);
		log.debug("Markers: " + multipleSegmentator.getMarkSet().getMarkers());
		return bufferedReader;
	}
	
	protected OnlineDecisionSegmentatorParam createParam(){
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(300L);
		param.setMinSpace(100L);
		param.setExpandStart(60L);
//		param.set(50L);
		return param;
	}

	public static void main(String[] args) {
		JFrame demo = new RecognitionPlot();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
		demo.repaint();
	}


}
