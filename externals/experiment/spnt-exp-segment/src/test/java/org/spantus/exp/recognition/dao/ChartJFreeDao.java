package org.spantus.exp.recognition.dao;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import com.google.common.collect.TreeBasedTable;

public class ChartJFreeDao {
	private static final String ATPAZINIMAS = "Atpažinimas";
	private static final String SEGMENTACIJA = "Segmentacija";

	/**
	 * 
	 * @param testData
	 */
	public void draw(SpreadsheetDocument ods) {

		Table segmentationTable = ods.getTableByName(ResultOdsDao.SEGMENTACIJA);
		Table recognitionTable = ods.getTableByName(ResultOdsDao.ATPAZINIMAS);
		Table comparisionTable = ods.getTableByName(ResultOdsDao.LYGINIMAS);

		com.google.common.collect.Table<String, String, Double> segmentationResult = createTable(segmentationTable);
		com.google.common.collect.Table<String, String, Double> recognitionResult = createTable(recognitionTable);
		com.google.common.collect.Table<String, String, Double> comparisionResult = createTable(comparisionTable);

		DefaultStatisticalCategoryDataset segmentationDataset = createStatisticalDataSet(segmentationResult);
		DefaultStatisticalCategoryDataset atpazinimasDataset = createStatisticalDataSet(recognitionResult);
		try {
			drawStatisical(segmentationDataset, SEGMENTACIJA);
			drawStacked(segmentationDataset, SEGMENTACIJA);
			drawStatisical(atpazinimasDataset, ATPAZINIMAS);
			drawStacked(atpazinimasDataset, ATPAZINIMAS);
			drawErrors(segmentationResult, recognitionResult, comparisionResult);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	private void drawErrors(
			com.google.common.collect.Table<String, String, Double> segmentationResult,
			com.google.common.collect.Table<String, String, Double> recognitionResult,
			com.google.common.collect.Table<String, String, Double> comparisionResult) {

		Map<String, Double> recognitionTotals = recognitionResult
				.row("Viso aptikta segmentavime ");
		Map<String, Double> segmentationTotals = segmentationResult
				.row("turėjo būti");


		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		String rowEntry = "Segmentavimo klaidos";
		for (Entry<String, Double> entry : comparisionResult.row(rowEntry)
				.entrySet()) {
			double total = (entry
					.getValue()) / segmentationTotals.get(entry.getKey());
			dataset.addValue(total,
					rowEntry,
					entry.getKey());
		}
		rowEntry = "Atpažinimo klaidos";
		for (Entry<String, Double> entry : comparisionResult.row(rowEntry)
				.entrySet()) {
			double total = ( entry
					.getValue()) / recognitionTotals.get(entry.getKey());
			dataset.addValue(total,
					rowEntry,
					entry.getKey());
		}
		
		rowEntry = "Skiemenų atpažinimo klaidos";
		for (Entry<String, Double> entry : comparisionResult.row(rowEntry)
				.entrySet()) {
			double total = (entry
					.getValue()) / recognitionTotals.get(entry.getKey());
			dataset.addValue(total,
					rowEntry,
					entry.getKey());
		}

		JFreeChart chart = ChartFactory.createLineChart(
				"Segmentų aptikimo ir atpažinimo santykiai", // chart
				// title
				"Klaidų santykis", // domain axis label
				"Tiukšmo Lygis", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);
		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesShapesVisible(2, true);
		saveChart("Segmentavimas ir atpažinimas", "errors", chart);

	}

	/**
	 * 
	 * @param dataset
	 * @param table
	 * @throws IOException
	 */
	private void drawStacked(DefaultStatisticalCategoryDataset dataset,
			String tableName) throws IOException {
		JFreeChart chart2 = ChartFactory.createStackedBarChart(tableName
				+ ": pagal triukšmus", "Atpažinimas arba klaidos", // domain
																	// axis
																	// label
				"% lyginant su aptinktai segmetntais", // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // the plot orientation
				true, // include legend
				true, // tooltips
				false // urls
				);
		chart2.setBackgroundPaint(Color.white);

		final CategoryPlot plot2 = (CategoryPlot) chart2.getPlot();
		final StackedBarRenderer renderer2 = (StackedBarRenderer) plot2
				.getRenderer();
		renderer2.setBaseItemLabelsVisible(true);
		renderer2.setRenderAsPercentages(true);
		saveChart(tableName, "stacked", chart2);
	}

	/**
	 * 
	 * @param dataset
	 * @param table
	 */
	private void drawStatisical(DefaultStatisticalCategoryDataset dataset,
			String tableName) throws IOException {
		CategoryAxis xAxis = new CategoryAxis("Atpažinimas arba klaidos");
		// xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		xAxis.setLowerMargin(0.01d); // percentage of space before first bar
		xAxis.setUpperMargin(0.01d); // percentage of space after last bar
		xAxis.setCategoryMargin(0.3d); // percentage of space between categories
		ValueAxis yAxis = new NumberAxis("% lyginant su aptinktai segmetntais");

		// define the plot
		StatisticalBarRenderer renderer = new StatisticalBarRenderer();
		CategoryPlot plot = new CategoryPlot(tranform(dataset), xAxis, yAxis,
				renderer);
		plot.setOrientation(PlotOrientation.HORIZONTAL);
		JFreeChart chart = new JFreeChart(tableName
				+ ": pagal atpažinimo tipus", new Font("Helvetica", Font.BOLD,
				14), plot, true);
		chart.setBackgroundPaint(Color.white);
		saveChart(tableName, "statistical", chart);
	}

	/**
	 * 
	 * @param ods
	 * @param table
	 * @return
	 */
	private DefaultStatisticalCategoryDataset createStatisticalDataSet(
			com.google.common.collect.Table<String, String, Double> segmentationResult) {
		DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();

		// com.google.common.collect.Table<String, String, Double> table =
		// createTable(segmentationResult);
		Map<String, Double> correct = new HashMap<String, Double>();
		Map<String, Double> confidence = new HashMap<String, Double>();
		Map<String, Double> totals = segmentationResult.row("turėjo būti");
		if (totals.isEmpty()) {
			totals = segmentationResult.row("Viso aptikta segmentavime ");
		}

		for (String column : segmentationResult.columnKeySet()) {
			correct.put(column, 0.0);
		}

		for (Entry<String, Map<String, Double>> rowEntry : segmentationResult
				.rowMap().entrySet()) {
			String rowKey = rowEntry.getKey();
			if (!rowKey.contains("Teisingai ")) {
				continue;
			}
			for (Entry<String, Double> entry : rowEntry.getValue().entrySet()) {
				Double correctVal = correct.get(entry.getKey());
				correct.put(entry.getKey(), correctVal + entry.getValue());
			}
		}
		for (Entry<String, Double> expected : totals.entrySet()) {
			Double correctVal = correct.get(expected.getKey());
			double probability = correctVal / expected.getValue();
			// 95%
			double confidenceVal = 1.96 * Math
					.sqrt( 
							(probability * (1 - probability))/ expected.getValue());
			confidence.put(expected.getKey(), confidenceVal);
		}

		for (Entry<String, Map<String, Double>> rowEntry : segmentationResult
				.rowMap().entrySet()) {
			String rowKey = rowEntry.getKey();
			if (rowKey.contains("turėjo būti") || rowKey.startsWith("Viso")) {
				continue;
			}
			for (Entry<String, Double> entry : rowEntry.getValue().entrySet()) {
				double entryVal = entry.getValue().doubleValue();
				double totalVal = totals.get(entry.getKey());
				double confidenceVal = confidence.get(entry.getKey())
						.doubleValue();
				result.add(entryVal / totalVal, confidenceVal, rowKey,
						entry.getKey());
			}
		}

		//

		return result;
	}

	/**
	 * 
	 * @param name
	 * @param chart
	 */
	private void saveChart(String name, String type, JFreeChart chart) {
		try {
			ChartUtilities.saveChartAsPNG(new File("./target/data/" + name
					+ "_" + type + ".png"), chart, 1000, 700);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param dataset
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private CategoryDataset tranform(DefaultStatisticalCategoryDataset dataset) {
		DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
		List<String> columns = dataset.getColumnKeys();
		List<String> rows = dataset.getRowKeys();
		for (String columnKey : columns) {
			for (String rowKey : rows) {
				if (columnKey.toLowerCase().startsWith("bendrai")) {
					continue;
				}
				result.add(dataset.getMeanValue(rowKey, columnKey),
						dataset.getStdDevValue(rowKey, columnKey), columnKey,
						rowKey);
			}
		}

		return result;
	}

	private com.google.common.collect.Table<String, String, Double> createTable(
			Table odsTable) {
		com.google.common.collect.Table<String, String, Double> result = TreeBasedTable
				.create();
		List<String> series = new ArrayList<String>();
		for (int rowIndex = 0; rowIndex < odsTable.getRowCount(); rowIndex++) {
			Row row = odsTable.getRowByIndex(rowIndex);
			if (rowIndex == 0) {
				for (int colIndex = 1; colIndex < row.getCellCount(); colIndex++) {
					Cell seriesCell = odsTable.getCellByPosition(colIndex,
							rowIndex);
					series.add(seriesCell.getStringValue());
				}
				continue;
			}
			String typeName = row.getCellByIndex(0).getStringValue();
			for (int colIndex = 1; colIndex < row.getCellCount(); colIndex++) {
				Cell newCell = odsTable.getCellByPosition(colIndex, rowIndex);
				result.put(typeName, series.get(colIndex - 1),
						newCell.getDoubleValue());
			}
		}
		return result;
	}

}
