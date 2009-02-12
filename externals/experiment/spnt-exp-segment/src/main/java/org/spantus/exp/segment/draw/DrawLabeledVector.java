/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 *
 */
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
