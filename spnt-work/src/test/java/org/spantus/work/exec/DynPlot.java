package org.spantus.work.exec;

import java.awt.Color;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.Graph;

import org.spantus.chart.InteractiveChart;
import org.spantus.chart.functions.FrameValueFuncton;
import org.spantus.core.FrameValues;
import org.spantus.logger.Logger;

public class DynPlot extends JFrame {

	/**
	 * Demo
	 */
	private static final long serialVersionUID = 6494242089681118096L;
	private Graph graph = new InteractiveChart();
	private FrameValueFuncton function1 = null;
	private Timer timer = new Timer("QN Plot demo");
	Logger log = Logger.getLogger(getClass());

	private DynPlot(FrameValues vals) {
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
		timer.schedule(new RandomFeed(function1), 100L, 800L);
		timer.schedule(new TimerTask() {
			
			public void run() {
				graph.render();
				graph.repaint();
			}
		}, 200L, 1000L);

	}

	public static void main(String[] args) {
		FrameValues vals = new FrameValues();
		for (int i = 0; i < 10000; i++) {
			vals.add(new Float(Math.sin(i * .3 * Math.random())));
		}

		JFrame demo = new DynPlot(vals);
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 480);
		demo.validate();
		demo.setVisible(true);
	}

	
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		graph.render();
		graph.repaint();
	}

	class RandomFeed extends TimerTask {

		RandomFeed(FrameValueFuncton f) {
			function = f;
		}

		
		public void run() {
			FrameValues vals = new FrameValues();
			for (int i = 0; i < 1000; i++) {
				vals.add(new Float(Math.sin(i * .03 * Math.random())));
			}
			function.addFrameValues(vals);
		}

		final FrameValueFuncton function;
	}

}
