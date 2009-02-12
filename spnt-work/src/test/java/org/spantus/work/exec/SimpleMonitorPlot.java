package org.spantus.work.exec;

import java.awt.Color;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Graph;

import org.spantus.chart.InteractiveChart;
import org.spantus.chart.functions.FrameValueFuncton;
import org.spantus.core.FrameValues;
import org.spantus.logger.Logger;

public class SimpleMonitorPlot extends JFrame {

	/**
	 * Demo
	 */
	private static final long serialVersionUID = 6494242089681118096L;
	private Graph graph = new InteractiveChart();
	private FrameValueFuncton function1 = null;
	private Timer timer = new Timer("QN Plot demo");
	Logger log = Logger.getLogger(getClass());

	private SimpleMonitorPlot(FrameValues vals) {
		initGraph();
		initFunctions(vals);
		getContentPane().add(graph);

	}

	private void initGraph() {
		graph.getXAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		graph.getYAxis().setZigZaginess(BigDecimal.valueOf(7L, 1));
		graph.setBackground(Color.WHITE);
	}

	private void initFunctions(FrameValues vals) {
		ChartStyle style1 = new ChartStyle();
		style1.setUpperLimitEnabled(true);
		style1.setLowerLimitEnabled(true);
		style1.setPaint(Color.RED);
		function1 = new FrameValueFuncton("function 1", vals, style1);
		graph.addFunction(function1, style1);
		timer.schedule(new SoundFeed(function1), 100L, 800L);
	}

	public static void main(String[] args) {
		FrameValues vals = new FrameValues();
		vals.add(1f);
		vals.add(-1f);
		for (int i = 0; i < 1000; i++) {
//			vals.add(new Float(Math.sin(i * .3 * Math.random())));
			vals.add(0f);
		}
		
		JFrame demo = new SimpleMonitorPlot(vals);
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}

	public AudioFormat getFormat() {
		float sampleRate = 8000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		graph.render();
		graph.repaint();
	}

	class SoundFeed extends TimerTask {

		SoundFeed(FrameValueFuncton f) {
			function = f;
		}

		
		public void run() {
			final AudioFormat format = getFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			TargetDataLine line = null;
			float amplitude = 1 << (getFormat().getSampleSizeInBits() - 1);
			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format);
				line.start();
				int bufferSize = (int) format.getSampleRate()
						* format.getFrameSize();
				byte buffer[] = new byte[bufferSize];
				boolean running = true;
				Float min=Float.MAX_VALUE, max = Float.MIN_VALUE;
				while (running) {
					FrameValues vals = new FrameValues();
					float avg = 0;
					int count = line.read(buffer, 0, buffer.length);
					if (count > 0) {
						int i = 0;
						for (byte b : buffer) {
							float f = Byte.valueOf(b).floatValue() /amplitude;
							avg += f;
							if(i == count/1000){
								Float f1 = avg/i; 
								min = Math.min(min, f1);
								max = Math.max(max, f1);
								vals.add(f1);
								i = 0; avg = 0;
							}
							i++;
						}
						
					}
					function.addFrameValues(vals);
					graph.render();
					graph.repaint();

					log.debug("min: " + min + "; max: " + max);
				}
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			}
			line.drain();
			line.close();

//			FrameValues vals = new FrameValues();
//			for (int i = 0; i < 1000; i++) {
//				vals.add(new Float(Math.sin(i * .03 * Math.random())));
//			}
//			function.addFrameValues(vals);
		}

		final FrameValueFuncton function;
	}

}
