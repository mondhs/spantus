package org.spantus.work.exec;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.work.test.SignalSelectionListenerMock;

public class SingleThresholdPlot extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	AbstractSwingChart chart;

	/**
	 * Demo
	 */

	public SingleThresholdPlot() {
		IExtractorInputReader reader = null;
		try {
			reader = readSignal();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chart = ChartFactory.createChart(reader);
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		File wavFile = new File("../data/t_1_2.wav");
		URL urlFile = wavFile.toURI().toURL();
		AudioReader reader = AudioFactory.createAudioReader();
		IExtractorInputReader bufferedReader = ExtractorsFactory
				.createReader(reader.getAudioFormat(urlFile));
		ExtractorUtils.registerThreshold(bufferedReader, new ExtractorEnum[] {
				ExtractorEnum.ENERGY_EXTRACTOR,
				ExtractorEnum.CROSSING_ZERO_EXTRACTOR,
				ExtractorEnum.ENVELOPE_EXTRACTOR,
				ExtractorEnum.LOG_ATTACK_TIME,
				ExtractorEnum.LOUDNESS_EXTRACTOR,
				ExtractorEnum.SPECTRAL_ENTROPY_EXTRACTOR,
				ExtractorEnum.AUTOCORRELATION_EXTRACTOR,
				ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR
				
				});
		reader.readAudio(urlFile, bufferedReader);
		return bufferedReader;
	}

	public static void main(String[] args) {

		JFrame demo = new SingleThresholdPlot();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}

//	public void paint(Graphics g) {
//		try {
//			readSignal();
//		} catch (UnsupportedAudioFileException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		chart.repaint();
//
//		super.paint(g);
//	}
}
