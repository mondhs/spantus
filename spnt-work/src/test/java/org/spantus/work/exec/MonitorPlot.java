package org.spantus.work.exec;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.work.test.SignalSelectionListenerMock;

public class MonitorPlot extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractSwingChart chart;
	private Timer timer = new Timer("Sound Monitor Plot");
	Logger log = Logger.getLogger(getClass());
	AudioCapture capture;

	private MonitorPlot() {
		IExtractorInputReader reader = ExtractorsFactory
				.createReader(getFormat());
		ExtractorUtils.registerThreshold(reader,
				new ExtractorEnum[] { 
					ExtractorEnum.ENERGY_EXTRACTOR, 
					ExtractorEnum.ENERGY_EXTRACTOR,
//					ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
//					ExtractorEnum.WAVFORM_EXTRACTOR, 
				}, null);
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
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);
	}



	public static void main(String[] args) {

		JFrame monitorPlot = new MonitorPlot();
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
