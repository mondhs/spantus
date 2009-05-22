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
package org.spantus.demo.cmd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.core.io.ByteListInputStream;
import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.ui.chart.SampleChart;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 *
 */
public class RecordCmd {

	private AudioCapture capture;
//	private RecordWraperExtractorReader wraperExtractorReader;
	private String path = "./";
	private boolean isRecordInitialyzed = false;
	private SampleChart sampleChart;
	private DemoAppletInfo info;
	private Timer timer;
	

	Logger log = Logger.getLogger(RecordCmd.class);

	public RecordCmd(SampleChart sampleChart, DemoAppletInfo info) {
		this.sampleChart = sampleChart;
		this.info = info;
	}

	public void execute() {
		RecordWraperExtractorReader wrapReader = createReader();
		ExtractorUtils.register(wrapReader.getReader(), ExtractorEnum.WAVFORM_EXTRACTOR, null);
		capture = new AudioCapture(wrapReader);
		capture.setFormat(getFormat());
		capture.start();
		getInfo().setRecording(Boolean.TRUE);
		getTimer().schedule(new InitCapture(wrapReader.getReader()), 2000L);
		getTimer().schedule(new UpdateCapture(wrapReader), 2000L, 1000L);
	}

	protected RecordWraperExtractorReader createReader() {
		AudioFormat format = getFormat();
		IExtractorInputReader reader = ExtractorsFactory.createReader(format);
		reader.setConfig(createReaderConfig());
		RecordWraperExtractorReader wraperExtractorReader = new RecordWraperExtractorReader(reader);
		wraperExtractorReader.setFormat(getFormat());
		return wraperExtractorReader;
	}

	public AudioFormat getFormat() {
		Float sampleRate = 16000F;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	protected IExtractorConfig createReaderConfig() {
		IExtractorConfig config = ExtractorConfigUtil
				.defaultConfig(getFormat());
		return config;
	}

	public void saveSignal(String name, RecordWraperExtractorReader wraperExtractorReader) {
		List<Byte> list = wraperExtractorReader.getAudioBuffer();
		InputStream bais = new ByteListInputStream(wraperExtractorReader
				.getAudioBuffer());
		AudioInputStream ais = new AudioInputStream(bais, wraperExtractorReader
				.getFormat(), list.size());
		try {
			if (path != null && !"".equals(path)) {
				String path = getPath() + "/" + name + ".wav";
				AudioSystem.write(ais, AudioFileFormat.Type.WAVE,
						new File(path));
				log.debug("[saveSegmentAccepted] saved{0}", path);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SampleChart getSampleChart() {
		return sampleChart;
	}

	public DemoAppletInfo getInfo() {
		return info;
	}
	protected Timer getTimer() {
		if (timer == null) {
			timer = new Timer("Spantus sound capture");
		}
		return timer;
	}

	public class InitCapture extends TimerTask {
		IExtractorInputReader reader;

		public InitCapture(IExtractorInputReader reader) {
			this.reader = reader;
		}

		@Override
		public void run() {
			getSampleChart().setReader(reader);
			// lisetener.changedReader(reader);
			isRecordInitialyzed = true;
		}
	}

	public class UpdateCapture extends TimerTask {
		RecordWraperExtractorReader wraperExtractorReader; 
		public UpdateCapture(RecordWraperExtractorReader wraperExtractorReader){
			this.wraperExtractorReader = wraperExtractorReader;
		}

		@Override
		public void run() {
			if (!isRecordInitialyzed) {
				return;
			}
			sampleChart.getChart().repaint();

			if (!getInfo().getRecording()) {
				log.debug("repaint");
				capture.finalize();
				this.cancel();

				String fullSingalName = "full" + System.currentTimeMillis();
				saveSignal(fullSingalName, wraperExtractorReader);

			}
		}
	}
	
}
