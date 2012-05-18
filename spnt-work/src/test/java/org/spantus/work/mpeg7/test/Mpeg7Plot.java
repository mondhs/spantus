/*
 * Part of program for analyze speech signal 
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
package org.spantus.work.mpeg7.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.exception.ProcessingException;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.config.Mpeg7ExtractorConfig;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Factory;
import org.spantus.work.test.SignalSelectionListenerMock;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.05.03
 *
 */
public class Mpeg7Plot extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7967175758052867793L;
	/**
	 * 
	 */
	AbstractSwingChart chart;

	/**
	 * Demo
	 */

	public Mpeg7Plot() {
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
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);

	}

	public IExtractorInputReader readSignal()
			throws UnsupportedAudioFileException, IOException {
//		String fileName = "C:\\Temp\\wav\\trafic.wav";
//		String fileName = "./target/test-classes/text1.wav";
//		String fileName = "./target/test-classes/fadein.xml";
		String fileName = "../data/single_tone.xml";
		
		File mp7File = new File(fileName);
		
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		Mpeg7ExtractorConfig mp7conf = new Mpeg7ExtractorConfig();
		mp7conf.getExtractors().add(Mpeg7ExtractorEnum.AudioWaveform.name());
		reader.setConfig(mp7conf);

//		reader.setConfig(Mpeg7ConfigUtil.createConfig(Mpeg7ExtractorEnum.values()));
		Mpeg7Factory.createAudioReader().readSignal(mp7File.toURI().toURL(), reader);
		return reader;
	}




	public static void main(String[] args) {

		JFrame demo = new Mpeg7Plot();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}
}
