package org.spantus.work.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;

public class SinglePlotWav extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	AbstractSwingChart chart;

	/**
	 * Demo
	 */

	public SinglePlotWav() {
		IExtractorInputReader reader = null;
		try {
			reader = readSignal();
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		chart = ChartFactory.createChart(reader);
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		File wavFile = new File("../data/t_1_2.wav");
		URL urlFile = wavFile.toURI().toURL();
		AudioReader reader = AudioReaderFactory.createAudioReader();
		IExtractorInputReader bufferedReader = ExtractorsFactory
				.createReader(reader.findAudioFormat(urlFile));
//		ExtractorUtils.register(bufferedReader, ExtractorEnum.values());
		ExtractorUtils.register(bufferedReader, new ExtractorEnum[] {
				ExtractorEnum.ENERGY_EXTRACTOR,
		}, null);
		reader.readSignal(urlFile, bufferedReader);
		return bufferedReader;
	}

	public static void main(String[] args) {

		JFrame demo = new SinglePlotWav();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}
	@Override
	public void repaint() {
		super.repaint();
		chart.repaint();
	}
}
