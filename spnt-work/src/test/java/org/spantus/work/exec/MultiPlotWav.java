/*
// * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.work.exec;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.work.test.SignalSelectionListenerMock;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class MultiPlotWav extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	AbstractSwingChart chart;

	/**
	 * Demo
	 */

	public MultiPlotWav() {
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
		if (reader == null) {
			throw new RuntimeException();
		}
		chart = ChartFactory.createChart(reader);
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		chart.initialize();
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		File wavFile = new File("../data/text1.wav");
		URL urlFile = wavFile.toURI().toURL();
		AudioReader reader = AudioReaderFactory.createAudioReader();
		IExtractorInputReader bufferedReader = ExtractorsFactory.createReader(reader.getAudioFormat(urlFile));
//		ExtractorUtils.register(bufferedReader, ExtractorEnum.values());
		ExtractorUtils.register(bufferedReader, new ExtractorEnum[]{
//				ExtractorEnum.FFT_EXTRACTOR,
				ExtractorEnum.ENVELOPE_EXTRACTOR,				
//				ExtractorEnum.LOUDNESS_EXTRACTOR,
				ExtractorEnum.WAVFORM_EXTRACTOR,
				ExtractorEnum.ENERGY_EXTRACTOR,
//				ExtractorEnum.SIGNAL_EXTRACTOR,

		}, null);
		reader.readSignal(urlFile, bufferedReader);
		return bufferedReader;
	}

	public void repaint() {
		if (chart != null) chart.repaint();
		super.repaint();
	}


	public static void main(String[] args) {

		final JFrame demo = new MultiPlotWav();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
		Timer timer = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				demo.repaint();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
}
