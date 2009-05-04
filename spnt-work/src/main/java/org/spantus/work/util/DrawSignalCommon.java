package org.spantus.work.util;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.util.ChartUtils;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;

public class DrawSignalCommon extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7833736854853194647L;

	static Map<String, String> resolveMap = new HashMap<String, String>();
	static {
		resolveMap.put("BUFFERED_AUTOCORRELATION_EXTRACTOR", "Autocorrelation");
		resolveMap.put("BUFFERED_CROSSING_ZERO_EXTRACTOR", "CrossingZero");
		resolveMap.put("BUFFERED_ENVELOPE_EXTRACTOR", "MaxEnvelope");
		resolveMap.put("BUFFERED_FFT_EXTRACTOR", "FFT");
		resolveMap.put("BUFFERED_LOG_ATTACK_TIME", "LogAttackTime");
		resolveMap.put("BUFFERED_LOUDNESS_EXTRACTOR", "Loudness");
		resolveMap.put("BUFFERED_LPC_EXTRACTOR", "LPC");
		resolveMap.put("BUFFERED_MFCC_EXTRACTOR", "MFCC");
		resolveMap.put("BUFFERED_SIGNAL", "Signal");
		resolveMap.put("BUFFERED_SPECTRAL_CENTROID_EXTRACTOR", "SpectralCentroid");
		resolveMap.put("BUFFERED_SPECTRAL_ENTROPY_EXTRACTOR", "Entropy");
		resolveMap.put("BUFFERED_SPECTRAL_FLUX_EXTRACTOR", "SpectralFlux");
	}
	/**
	 * 
	 */
	AbstractSwingChart chart;
	IExtractorInputReader reader;
	List<IGeneralExtractor> extr = new ArrayList<IGeneralExtractor>();
	String path;
	String name;
	String extention;
	String sufix = "_";

	public AbstractSwingChart getChart() {
		return chart;
	}

	public void setChart(AbstractSwingChart chart) {
		this.chart = chart;
	}

	/**
	 * Demo
	 */

	public DrawSignalCommon(String name) {
		File audioFile = new File(name);
		this.path = FileUtils.getPath(audioFile);
		this.name = FileUtils.getOnlyFileName(audioFile);
		try {
			reader = readSignal(audioFile);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		extr.addAll(reader.getExtractorRegister());
		extr.addAll(reader.getExtractorRegister3D());
	}

	public IExtractorInputReader getReader(IExtractorInputReader reader,
			List<IGeneralExtractor> extr, int index) {
		if (index == 0) {
			return null;
		}
		reader.getExtractorRegister().clear();
		reader.getExtractorRegister3D().clear();
		IGeneralExtractor gextr = extr.get(index);
		sufix = gextr.getName();
		if (gextr instanceof IExtractor) {
			reader.getExtractorRegister().add((IExtractor) gextr);
		} else if (gextr instanceof IExtractorVector) {
			reader.getExtractorRegister3D().add((IExtractorVector) gextr);
		}
		return reader;
	}
	public void process(){
		process(getExtractorsSize()-1);
	}
	
	private void process(int index) {
		if (index < 0) {
			this.dispose();
			return;
		}
		IExtractorInputReader _reader = getReader(reader, extr, index);
		if (_reader == null) {
			process(index - 1);
			return;
		}
		setChart(ChartFactory.createChart(_reader));
		getContentPane().add(getChart());
		getChart().addComponentListener(
				new FrameShowedAdapter(this.path + name + "_" + resolveSufix(sufix), index));

	}

	public int getExtractorsSize() {
		return extr.size();
	}

	public IExtractorInputReader readSignal(File audioFile)
			throws UnsupportedAudioFileException, IOException {
		AudioReader reader = AudioFactory.createAudioReader();
		IExtractorInputReader bufferedReader = ExtractorsFactory.createReader(reader
				.getAudioFormat(audioFile.toURI().toURL()).getFormat());
		ExtractorUtils.register(bufferedReader, ExtractorEnum.values());
		reader.readAudio(audioFile.toURI().toURL(), bufferedReader);
		if (reader == null) {
			throw new RuntimeException();
		}
		return bufferedReader;
	}

	public class FrameShowedAdapter extends ComponentAdapter {
		String name;
		int index;

		public FrameShowedAdapter(String name, int index) {
			this.name = name;
			this.index = index;
		}

		
		public void componentResized(ComponentEvent e) {
			try {
				ChartUtils.writeAsPNG(getChart(), new File(name + ".png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getContentPane().remove(getChart());
			process(index - 1);
		}
	}

	public String resolveSufix(String sufix) {

		if (resolveMap.containsKey(sufix)) {
			return resolveMap.get(sufix);
		}
		return sufix;
	}

	public static void main(String[] args) {
		// "./target/test-classes/text1.wav"
		DrawSignalCommon demo = new DrawSignalCommon(args[0]);
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 140);
		demo.validate();
		demo.setVisible(true);
		demo.process(demo.getExtractorsSize() - 1);

	}
}
