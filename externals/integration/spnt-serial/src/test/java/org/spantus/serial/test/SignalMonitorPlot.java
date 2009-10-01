package org.spantus.serial.test;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.SignalSelectionListener;
import org.spantus.core.WraperExtractorReader;
import org.spantus.core.extractors.ExtractorsFactory;
import org.spantus.core.extractors.IExtractorInputReader;
import org.spantus.core.extractors.impl.ExtractorEnum;
import org.spantus.core.extractors.impl.ExtractorUtils;
import org.spantus.core.io.AudioCapture;
import org.spantus.logger.Logger;
import org.spantus.serial.OutputStaticThresholdSerial;

public class SignalMonitorPlot extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractSwingChart chart;
	private Timer timer = new Timer("Sound Monitor Plot");
	Logger log = Logger.getLogger(getClass());
	AudioCapture capture;

	private SignalMonitorPlot() {
		IExtractorInputReader reader = ExtractorsFactory
				.createReader(getFormat());
		
		OutputStaticThresholdSerial threshold = new OutputStaticThresholdSerial();
		threshold.setCoef(3f);
		try {
			threshold.getWriter().write("L");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ExtractorUtils.registerThreshold(reader,
				ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR, 
				threshold);

		
		capture = new AudioCapture(new WraperExtractorReader(reader));
		capture.setFormat(getFormat());
		initGraph(reader);
		capture.start();
		timer.schedule(new TimerTask() {
			public void run() {
				log.debug("repaint");
//				chart.setPreferredSize(getSize());
				repaint();
			}
		}, 200L, 1000L);

	}

	private void initGraph(IExtractorInputReader reader) {
		chart = ChartFactory.createChart(reader);
		chart.addSignalSelectionListener(new SignalSelectionListener(){
			public void play() {
			}
			public void selectionChanged(float from, float length) {
			}});
		getContentPane().add(chart);
	}



	public static void main(String[] args) {

		JFrame monitorPlot = new SignalMonitorPlot();
		monitorPlot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		monitorPlot.setSize(640, 480);
		monitorPlot.validate();
		monitorPlot.setVisible(true);
	}

	public AudioFormat getFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	
	public void repaint() {
		if (getChart() != null) {
//			getChart().setSize(getSize());
			getChart().repaint();
		}
		super.repaint();
	}

	public AbstractSwingChart getChart() {
		return chart;
	}

}
