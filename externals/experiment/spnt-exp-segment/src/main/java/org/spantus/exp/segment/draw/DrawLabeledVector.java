package org.spantus.exp.segment.draw;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class DrawLabeledVector {
	Map<String, Float> categories;

	public DrawLabeledVector(Map<String, Float> series) {
		this.categories = series;
	}

	public Map<String, Float> getCategories() {
		return categories;
	}

	public void showChart() {
		int width = 600;
		int height = 600;
		JFreeChart chart = createBarChart();
		ChartPanel chartPanel = new ChartPanel(chart, width, height, 16, 16,
				width * 10, height * 10, true, true, true, true, true, true);
		JFrame frame = new JFrame(chart.getTitle().getText());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}

	public JFreeChart createBarChart() {
		return createBarChart("", "");
	}
	
	public JFreeChart createBarChart(String chartName, String categoriesAxisName) {
		CategoryDataset dataSet = createCategoryDataset(getCategories());

		JFreeChart chart = ChartFactory.createBarChart(chartName, // chart title
				"Category", // domain axis label
				"Value", // range axis label
				dataSet, // data
				PlotOrientation.HORIZONTAL, // the plot orientation
				false, // include legend
				true, false);

		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setNoDataMessage("NO DATA!");
		plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(2);
		// change the margin at the top of the range axis...
		final ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setAutoRange(true);
		return chart;
	}

	public static CategoryDataset createCategoryDataset(Map<String, Float> series) {

		DefaultCategoryDataset result = new DefaultCategoryDataset();
		int i = 0;
		for (Entry<String, Float> entry : series.entrySet()) {
			i++;
			String columnKey = entry.getKey();
			result.addValue(entry.getValue() , "", columnKey);
		}
		return result;

	}
}
