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
package org.spantus.work.wav.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.DefaultExtractorInputReader;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class PlotMergedWav extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2800396484363931372L;
	AbstractSwingChart chart;

	/**
	 * Demo
	 */

	public PlotMergedWav() {
		IExtractorInputReader reader = null;
		try {
			reader = readSignal();
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		if (reader == null) {
			throw new RuntimeException();
		}
		chart = ChartFactory.createChart(reader);
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
		URL mainSignal = new File("/home/mindas/temp/521.wav").toURI().toURL();
		URL noiseSignal = new File("/home/mindas/temp/noise11k.wav").toURI().toURL();
		List<URL> urls =new ArrayList<URL>();
		urls.add(mainSignal);
		urls.add(noiseSignal);
		AudioReader reader = AudioReaderFactory.createAudioReader();
		IExtractorInputReader bufferedReader = new DefaultExtractorInputReader();
		ExtractorUtils.register(bufferedReader, new ExtractorEnum[]{
				ExtractorEnum.SIGNAL_EXTRACTOR,
		}, null);
		reader.readSignal(urls, bufferedReader);
		return bufferedReader;
	}




	public static void main(String[] args) {

		JFrame demo = new PlotMergedWav();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}
}
