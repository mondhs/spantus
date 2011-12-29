package org.spantus.exp.recognition.dao;

import java.awt.Color;
import java.io.FileOutputStream;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.category.DefaultCategoryDataset;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.spantus.exp.recognition.dao.chart.AsymmetricStatisticalBarRenderer;
import org.spantus.exp.recognition.dao.chart.AsymmetricStatisticalCategoryDataset;

import com.google.common.collect.Maps;
import com.google.common.collect.TreeBasedTable;

public class ChartJFreeDao {
	private static final String ATPAZINIMAS = "recognition";
	private static final String SEGMENTACIJA = "segmentation";
	private static final String SEGMENTATION_ERRORS = "segmentationErrors";
	private static final String RECOGNITION_ERRORS = "recognitionErrors";
	
	private static final Map<String,String> translate= Maps.newHashMap();
	private static final String ERROR_RATIOS = "errorRatios";
	private static final String RECOGNITION_SEGMENTATION_ERROR_RATIOS = "RECOGNITION_SEGMENTATION_ERROR_RATIOS";
	private static final String NOISE_LEVEL = "NOISE_LEVEL";
	private static final String COMPARE_WITH_FOUND = "COMPARE_WITH_FOUND";
	private static final String BY_NOISE = "BY_NOISE";
	private static final String RECOGNITION_TYPE = "RECOGNITION_TYPE";
	private static final String BY_RECOGNION_TYPE = "BY_RECOGNION_TYPE";

	static{
		translate.put(ATPAZINIMAS, "Atpažinimas");
		translate.put(SEGMENTACIJA, "Segmentacija");
		translate.put(SEGMENTATION_ERRORS, "Segmentavimo klaidos");
		translate.put(RECOGNITION_ERRORS, "Skiemenų atpažinimo klaidos");
		translate.put(RECOGNITION_SEGMENTATION_ERROR_RATIOS, "Segmentų aptikimo ir atpažinimo santykiai");
		translate.put(ERROR_RATIOS,"Klaidų santykis %");
		translate.put(NOISE_LEVEL,"Tiukšmo Lygis");
		translate.put(COMPARE_WITH_FOUND,"% lyginant su aptinktai segmetntais");
		translate.put(BY_NOISE,": pagal triukšmus");
		translate.put(RECOGNITION_TYPE,"Atpažinimas arba klaidos");
		translate.put(BY_RECOGNION_TYPE,": pagal atpažinimo tipus");
		
		
			}
	
	
	/**
	 * 
	 * @param testData
	 */
	public void draw(SpreadsheetDocument ods) {

		Table segmentationTable = ods.getTableByName(ResultOdsDao.SEGMENTACIJA);
		Table recognitionTable = ods.getTableByName(ResultOdsDao.SKIEMENIMIS);
		Table comparisionTable = ods.getTableByName(ResultOdsDao.LYGINIMAS);

		com.google.common.collect.Table<String, String, Double> segmentationResult = createTable(segmentationTable);
		com.google.common.collect.Table<String, String, Double> recognitionResult = createTable(recognitionTable);
		com.google.common.collect.Table<String, String, Double> comparisionResult = createTable(comparisionTable);

		AsymmetricStatisticalCategoryDataset segmentationDataset = createStatisticalDataSet(segmentationResult);
		AsymmetricStatisticalCategoryDataset atpazinimasDataset = createStatisticalDataSet(recognitionResult);
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
	/**
	 * 
	 * @param segmentationResult
	 * @param recognitionResult
	 * @param comparisionResult
	 */
	private void drawErrors(
			com.google.common.collect.Table<String, String, Double> segmentationResult,
			com.google.common.collect.Table<String, String, Double> recognitionResult,
			com.google.common.collect.Table<String, String, Double> comparisionResult) {

		Map<String, Double> recognitionTotals =  extractTotals(recognitionResult);
		Map<String, Double> segmentationTotals = extractTotals(segmentationResult);

		
		

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		String rowEntry = translate.get(SEGMENTATION_ERRORS);
		caclErrorDataset(comparisionResult, segmentationTotals, dataset,
				rowEntry);

		
		rowEntry  = translate.get(RECOGNITION_ERRORS);
		caclErrorDataset(comparisionResult, recognitionTotals, dataset,
				rowEntry);

		JFreeChart chart = ChartFactory.createLineChart(
				 translate.get(RECOGNITION_SEGMENTATION_ERROR_RATIOS), // chart
				// title
				 translate.get(NOISE_LEVEL), // domain axis label
				translate.get(ERROR_RATIOS), // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);
		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
//		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesShapesVisible(2, true);
		saveChart("compare", "errors", chart);

	}
	/**
	 * 
	 * @param comparisionResult
	 * @param segmentationTotals
	 * @param dataset
	 * @param rowEntry
	 */
	private void caclErrorDataset(
			com.google.common.collect.Table<String, String, Double> results,
			Map<String, Double> totals,
			DefaultCategoryDataset dataset, String rowEntry) {
		
		for (Entry<String, Double> entry : results.row(rowEntry)
				.entrySet()) {
			double errorCount = entry.getValue();
			double totalCount = totals.get(entry.getKey());
			double total = errorCount / totalCount;
			dataset.addValue(total*100,
					rowEntry,
					entry.getKey());
		}
	}
	
	/**
	 * 
	 * @param segmentationResult
	 * @param segmentationTotals
	 * @return
	 */
	private Map<String, Double> extractTotals(
			com.google.common.collect.Table<String, String, Double> result) {
		Map<String, Double> result1 = null;
		for (Entry<String, Map<String, Double>> rowEntry : result
				.rowMap().entrySet()) {
			String rowKey = rowEntry.getKey();
			if (rowKey.contains("turėjo būti") || rowKey.startsWith("Viso")) {
				result1 = rowEntry.getValue();
				break;
			}
		}
		return result1;
	}

	/**
	 * 
	 * @param dataset
	 * @param table
	 * @throws IOException
	 */
	private void drawStacked(AsymmetricStatisticalCategoryDataset dataset,
			String tableName) throws IOException {
		JFreeChart chart2 = ChartFactory.createStackedBarChart(translate.get(tableName)
				+ translate.get(BY_NOISE) , translate.get(ERROR_RATIOS), // domain
																	// axis
																	// label
				translate.get(COMPARE_WITH_FOUND), // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // the plot orientation
				true, // include legend
				true, // tooltips
				false // urls
				);

		final CategoryPlot plot2 = (CategoryPlot) chart2.getPlot();
		final StackedBarRenderer renderer2 = (StackedBarRenderer) plot2
				.getRenderer();

//		renderer2.setBaseItemLabelsVisible(true);
//		renderer2.setRenderAsPercentages(true);
		renderer2.setBarPainter(new StandardBarPainter());//remove gradient
		saveChart(tableName, "stacked", chart2);
	}

	/**
	 * 
	 * @param dataset
	 * @param table
	 */
	private void drawStatisical(AsymmetricStatisticalCategoryDataset dataset,
			String tableName) throws IOException {
		

		CategoryAxis xAxis = new CategoryAxis(translate.get(RECOGNITION_TYPE));
		// xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
//		xAxis.setLowerMargin(0.01d); // percentage of space before first bar
//		xAxis.setUpperMargin(0.01d); // percentage of space after last bar
//		xAxis.setCategoryMargin(0.3d); // percentage of space between categories
		NumberAxis yAxis = new NumberAxis(translate.get(COMPARE_WITH_FOUND));

		yAxis.setRangeType(RangeType.POSITIVE);
		
		
		// define the plot
		StatisticalBarRenderer renderer = new AsymmetricStatisticalBarRenderer();
		CategoryPlot plot = new CategoryPlot(tranform(dataset), xAxis, yAxis,
				renderer);
		plot.setOrientation(PlotOrientation.HORIZONTAL);
		renderer.setSeriesPaint(0, Color.white);
		
		
		JFreeChart chart = new JFreeChart(translate.get(tableName)
				+ translate.get(BY_RECOGNION_TYPE), null, plot, true);
		chart.setBackgroundPaint(Color.white);
		saveChart(tableName, "statistical", chart);
	}

	/**
	 * 
	 * @param ods
	 * @param table
	 * @return
	 */
	private AsymmetricStatisticalCategoryDataset createStatisticalDataSet(
			com.google.common.collect.Table<String, String, Double> segmentationResult) {
		AsymmetricStatisticalCategoryDataset result = new AsymmetricStatisticalCategoryDataset();

		// com.google.common.collect.Table<String, String, Double> table =
		// createTable(segmentationResult);
		Map<String, Double> correct = new HashMap<String, Double>();
		Map<String, Double> confidence = new HashMap<String, Double>();
		Map<String, Double> totals = null;
		totals = extractTotals(segmentationResult);

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
				double meanVal = entryVal / totalVal ;
				double confidenceVal = confidence.get(entry.getKey())
						.doubleValue();
				double lowerVal = meanVal - confidenceVal;
				lowerVal = lowerVal<0?0:lowerVal;
				double upperVal = meanVal + confidenceVal;
				

				result.add(meanVal* 100, upperVal* 100,lowerVal* 100, rowKey,
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
		  String writtenFile =  "./target/data/" + name
					+ "_" + type + ".png";
		  try
		   {
			  chart.setBackgroundPaint( new Color(255,255,255,0) );
		         final Plot plot = chart.getPlot();
		         plot.setBackgroundPaint( new Color(255,255,255,0) );
		         plot.setBackgroundImageAlpha(0.0f);

		      final CategoryItemRenderer renderer = chart.getCategoryPlot().getRenderer();
		      renderer.setSeriesPaint(0, Color.blue.brighter());
		      renderer.setSeriesVisible(0, true); // default
		      renderer.setSeriesVisibleInLegend(0, true);  // default

		      ChartUtilities.writeChartAsPNG( new FileOutputStream(writtenFile),
		    		  chart,
		    		  1000, 700,
		                                      null,
		                                      true,    // encodeAlpha
		                                      0 );
		      System.out.println("Wrote PNG (transparent) file " + writtenFile);
		   }
		   catch (IOException ioEx)
		   {
		      System.err.println(  "Error writing PNG file " + writtenFile + ": "
		                  + ioEx.getMessage() );
		   }
		  
	}

	/**
	 * 
	 * @param dataset
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private AsymmetricStatisticalCategoryDataset tranform(AsymmetricStatisticalCategoryDataset dataset) {
		AsymmetricStatisticalCategoryDataset result = new AsymmetricStatisticalCategoryDataset();
		List<String> columns = dataset.getColumnKeys();
		List<String> rows = dataset.getRowKeys();
		for (String columnKey : columns) {
			for (String rowKey : rows) {
				if (columnKey.toLowerCase().startsWith("bendrai")) {
					continue;
				}
				result.add(dataset.getMeanValue(rowKey, columnKey),
						dataset.getUpperValue(rowKey, columnKey), dataset.getLowerValue(rowKey, columnKey), columnKey,
						rowKey);
			}
		}

		return result;
	}
	/**
	 * 
	 * @param odsTable
	 * @return
	 */
	private com.google.common.collect.Table<String, String, Double> createTable(
			Table odsTable) {
		com.google.common.collect.Table<String, String, Double> result = TreeBasedTable
				.create();
		List<String> series = new ArrayList<String>();
		for (int rowIndex = 0; rowIndex < odsTable.getRowCount(); rowIndex++) {
			Row row = odsTable.getRowByIndex(rowIndex);
			//header
			if (rowIndex == 0) {
				for (int colIndex = 1; colIndex < row.getCellCount(); colIndex++) {
					Cell seriesCell = odsTable.getCellByPosition(colIndex,
							rowIndex);
					series.add(seriesCell.getStringValue());
				}
				continue;
			}
			//body
			String typeName = row.getCellByIndex(0).getStringValue();
			for (int colIndex = 1; colIndex < row.getCellCount(); colIndex++) {
				Cell newCell = odsTable.getCellByPosition(colIndex, rowIndex);
				Double value  = newCell.getDoubleValue();
				result.put(typeName, series.get(colIndex - 1),
						value);
			}
		}
		return result;
	}

}
