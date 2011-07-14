package org.spantus.exp.recognition.statistics;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class CorpusSegmentStatsScatterPlot extends ApplicationFrame
{
	static class MyChartMouseListener
		implements ChartMouseListener
	{

		ChartPanel panel;

		public void chartMouseClicked(ChartMouseEvent chartmouseevent)
		{
			int i = chartmouseevent.getTrigger().getX();
			int j = chartmouseevent.getTrigger().getY();
			Point2D point2d = panel.translateScreenToJava2D(new Point(i, j));
			XYPlot xyplot = (XYPlot)panel.getChart().getPlot();
			ChartRenderingInfo chartrenderinginfo = panel.getChartRenderingInfo();
			java.awt.geom.Rectangle2D rectangle2d = chartrenderinginfo.getPlotInfo().getDataArea();
			double d = xyplot.getDomainAxis().java2DToValue(point2d.getX(), rectangle2d, xyplot.getDomainAxisEdge());
			double d1 = xyplot.getRangeAxis().java2DToValue(point2d.getY(), rectangle2d, xyplot.getRangeAxisEdge());
			ValueAxis valueaxis = xyplot.getDomainAxis();
			ValueAxis valueaxis1 = xyplot.getRangeAxis();
			double d2 = valueaxis.valueToJava2D(d, rectangle2d, xyplot.getDomainAxisEdge());
			double d3 = valueaxis1.valueToJava2D(d1, rectangle2d, xyplot.getRangeAxisEdge());
			Point point = panel.translateJava2DToScreen(new java.awt.geom.Point2D.Double(d2, d3));
			System.out.println("Mouse coordinates are (" + i + ", " + j + "), in data space = (" + d + ", " + d1 + ").");
			System.out.println("--> (" + point.getX() + ", " + point.getY() + ")");
		}

		public void chartMouseMoved(ChartMouseEvent chartmouseevent)
		{
		}

		public MyChartMouseListener(ChartPanel chartpanel)
		{
			panel = chartpanel;
		}
	}


	public CorpusSegmentStatsScatterPlot(String s)
	{
		super(s);
		JPanel jpanel = createDemoPanel();
		jpanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(jpanel);
	}

	private static JFreeChart createChart(XYDataset xydataset)
	{
		JFreeChart jfreechart = ChartFactory.createScatterPlot("PLP", "Avg", "Std", xydataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		return jfreechart;
	}

	public static JPanel createDemoPanel()
	{
		JFreeChart jfreechart = createChart(new CorpusSegmentStatsDataset());
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.addChartMouseListener(new MyChartMouseListener(chartpanel));
		chartpanel.setDomainZoomable(true);
		chartpanel.setRangeZoomable(true);
		return chartpanel;
	}

	public static void main(String args[])
	{
		CorpusSegmentStatsScatterPlot scatterplotdemo3 = new CorpusSegmentStatsScatterPlot("Scatter Plot Demo 3");
		scatterplotdemo3.pack();
		RefineryUtilities.centerFrameOnScreen(scatterplotdemo3);
		scatterplotdemo3.setVisible(true);
	}
}