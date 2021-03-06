package org.spantus.work.exec;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Graph;

import org.spantus.chart.InteractiveChart;
import org.spantus.chart.functions.FrameValueMatrixFuncton;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorResultBuffer;
import org.spantus.extractor.ExtractorResultBuffer3D;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.FFTExtractor;
import org.spantus.extractor.impl.SignalExtractor;

public class Plot3d extends JFrame {

	/**
	 * Demo
	 */
	private static final long serialVersionUID = 6494242089681118096L;
	private Graph graph = new InteractiveChart();
	private FrameValueMatrixFuncton function1 = null;

	private Plot3d(FrameVectorValues vals) {
		initGraph();
		initFunctions(vals);
		getContentPane().add(graph);

	}

	private void initGraph() {
		graph.getXAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		graph.getYAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		graph.setBackground(Color.WHITE);
	}

	private void initFunctions(FrameVectorValues vals) {
		function1 = new FrameValueMatrixFuncton("function 1", vals);
		ChartStyle style1 = new ChartStyle();
		style1.setUpperLimitEnabled(true);
		style1.setLowerLimitEnabled(true);
		style1.setPaint(Color.RED);
		graph.addFunction(function1, style1);
	}

	public static FrameVectorValues initData() {
		FrameVectorValues vals = null;
		IExtractorInputReader reader = null;
		try {
			reader = readSignal();
		} catch (MalformedURLException e) {
			throw new ProcessingException(e);
		}

	
		vals = reader.getExtractorRegister3D().iterator().next().getOutputValues();
		return vals;
	}
	/**
	 * 
	 * @return
	 * @throws MalformedURLException 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public static IExtractorInputReader readSignal() throws MalformedURLException
			 {
		File wavFile = new File("../data/t_1_2.wav");
		URL urlFile = wavFile.toURI().toURL();
		AudioReader reader = AudioReaderFactory.createAudioReader();
		IExtractorInputReader bufferedReader = ExtractorsFactory.createReader(reader
				.findAudioFormat(urlFile));
		
		ExtractorResultBuffer signal = new ExtractorResultBuffer(
				new SignalExtractor());
		ExtractorResultBuffer3D fft = new ExtractorResultBuffer3D(
				new FFTExtractor());

		bufferedReader.registerExtractor(signal);
		bufferedReader.registerExtractor(fft);

		reader.readSignal(urlFile, bufferedReader);
		
		return bufferedReader;
	}

	
	public void paint(Graphics g) {
		super.paint(g);
		graph.render();
		graph.repaint();
	}

	public static void main(String[] args) {
		JFrame demo = new Plot3d(initData());
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}

}
