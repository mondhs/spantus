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
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.ProcessReader;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.Assert;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.ReaderDao;
import org.spantus.work.services.WorkServiceFactory;

/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 * 
 */
public abstract class AbstractGraphGenerator {

	protected Logger log = Logger.getLogger(getClass());

	private ReaderDao readerDao = null;

	private MarkerDao markerDao = null;

	private ProcessReader processReader = null;

	private MakerComparison makerComparison = null;

	private ISegmentatorService segmentator;

	private String experimentName;

	private String testPath = null;

	private String expertMarksPath = null;

	private boolean generateCharts = true;

	public static final String DEFAULT_TEST_DATA_PATH = "../../../data/t_1_2.wav.sspnt.xml";
	public static final String DEFAULT_EXPERT_MARKS_PATH = "../../../data/t_1_2_expert.mspnt.xml";

	public abstract List<ComparisionResult> compare();

	public AbstractGraphGenerator() {
		readerDao = WorkServiceFactory.createReaderDao();
		markerDao = WorkServiceFactory.createMarkerDao();
		processReader = ExpServiceFactory.createProcessReader();
		// makerComparison = new MakerComparisonDtwImpl();
	}

	/**
	 * 
	 */
	public void process() {
		process(getExpertMarksPath(), getTestPath());
	}

	/**
	 * 
	 * @param expertMarksPath
	 * @param testPath
	 */
	public void process(String expertMarksPath, String testPath) {
		setExpertMarksPath(expertMarksPath);
		setTestPath(testPath);
		List<ComparisionResult> results = compare();
		Assert.isTrue(results != null, "Result should not be null");
		Map<String, Double> totals = new LinkedHashMap<String, Double>();
		for (ComparisionResult comparisionResult : results) {
			if (isGenerateCharts()) {
				draw(getChart(comparisionResult), comparisionResult);
			}
			totals.put(comparisionResult.getName(), comparisionResult
					.getTotalResult());
		}

		if (isGenerateCharts()) {
			drawTotals(totals);
		}
		Toolkit.getDefaultToolkit().beep();

	}

	protected void drawTotals(Map<String, Double> result) {
		sortTotals(result);
		DrawLabeledVector drawVector = new DrawLabeledVector(result);
		Double heightCoef = (double) (result.size() / 12);
		heightCoef = heightCoef < 1 ? 1 : heightCoef;
		draw(drawVector.createBarChart("Comparison results: "
				+ getExperimentName(), "Features"), "_totals_"
				+ getExperimentName(), 1D, heightCoef);
		log.info("; Totals: " + result);
	}

	protected Map<String, Double> sortTotals(Map<String, Double> result) {
		// Get a list of the entries in the map
		List<Map.Entry<String, Double>> list = new Vector<Map.Entry<String, Double>>(
				result.entrySet());

		// Sort the list using an annonymous inner class implementing Comparator
		// for the compare method
		java.util.Collections.sort(list,
				new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> entry,
							Map.Entry<String, Double> entry1) {
						// Return 0 for a match, -1 for less than and +1 for
						// more then
						return (entry.getValue().equals(entry1.getValue()) ? 0
								: (entry.getValue() > entry1.getValue() ? 1
										: -1));
					}
				});

		// Clear the map
		result.clear();

		// Copy back the entries now in order
		for (Map.Entry<String, Double> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public MarkerSet getWordMarkerSet(MarkerSetHolder holder) {
		return holder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
	}

	protected String getGeneratePath() {
		return "./target/";
	}

	public JFreeChart getChart(ComparisionResult result) {
		CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis(
				"Time"));
		plot.setGap(10.0);
		plot.setOrientation(PlotOrientation.VERTICAL);

		XYSeriesCollection[] seriesArr = createSeries(result);
		for (XYSeriesCollection series : seriesArr) {
			XYSeriesCollection data = series;
			StandardXYItemRenderer renderer = new StandardXYItemRenderer();
			renderer.setAutoPopulateSeriesPaint(false);
			renderer.setBasePaint(Color.BLACK);
			NumberAxis rangeAxis = new NumberAxis();
			rangeAxis.setLabel(((XYSeries) series.getSeries().get(0))
					.getDescription());
			rangeAxis.setAutoRange(true);
			XYPlot subplot = new XYPlot(data, null, rangeAxis, renderer);
			plot.add(subplot);
		}
		String name = result.getName() == null ? "Segmentation"
				: "Segmentation: " + result.getName();

		JFreeChart chart = new JFreeChart(name, JFreeChart.DEFAULT_TITLE_FONT,
				plot, true);

		return chart;

	}

	protected void draw(JFreeChart chart, ComparisionResult result) {
		draw(chart, result.getName(), 1D, 1D);
	}

	protected void draw(JFreeChart chart, String name, Double widthCoef,
			Double heightCoef) {
		Double width = 800 * widthCoef;
		Double height = 270 * heightCoef;

		try {
			new File(getGeneratePath()).mkdirs();
			String _name = name.replaceAll(":", "_");
			_name = _name.replaceAll("^\\s+", "").replaceAll("\\s+$", "")
					.replaceAll("\\s+", "-");
			ChartUtilities.saveChartAsPNG(new File(getGeneratePath() + _name
					+ ".png"), chart, width.intValue(), height.intValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected XYSeriesCollection[] createSeries(ComparisionResult result) {

		XYSeries series;

		int size = 3;
		if (result.getThreshold() != null) {
			size++;
		}

		XYSeriesCollection[] collections = new XYSeriesCollection[size];
		for (int i = 0; i < collections.length; i++) {
			collections[i] = new XYSeriesCollection();
		}

		int i = 0;
		Double sampleRate = result.getSequenceResult().getSampleRate();
		series = newSeries("Result", collections[0]);
		for (Double f1 : result.getSequenceResult()) {
			series.add(Double.valueOf(i) / sampleRate, f1);
			i++;
		}
		i = 0;
		sampleRate = result.getOriginal().getSampleRate();
		series = newSeries("Description", collections[1]);
		for (Double f1 : result.getOriginal()) {
			series.add(Double.valueOf(i) / result.getOriginal().getSampleRate(),
					f1);
			i++;
		}
		i = 0;
		sampleRate = result.getTest().getSampleRate();
		series = newSeries("Test", collections[2]);
		for (Double f1 : result.getTest()) {
			series.add(Double.valueOf(i) / sampleRate, f1);
			i++;
		}

		if (result.getThreshold() != null) {
			i = 0;
			series = newSeries("Feature", collections[3]);
			sampleRate = result.getThreshold().getOutputValues()
					.getSampleRate();
			for (Double f1 : result.getThreshold().getOutputValues()) {
				series.add(Double.valueOf(i) / sampleRate, f1);
				i++;
			}

			i = 0;
			series = newSeries("Threshold", collections[3]);
			for (Double f1 : result.getThreshold().getThresholdValues()) {
				series.add(Double.valueOf(i) / sampleRate, f1);
				i++;
			}
		}

		return collections;

	}

	protected OnlineDecisionSegmentatorParam createDefaultOnlineParam() {
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(200L);
		param.setMinSpace(100L);
		param.setExpandStart(60L);
		param.setExpandEnd(0L);
		return param;
	}

	protected XYSeries newSeries(String name, XYSeriesCollection collection) {
		XYSeries series = new XYSeries(name);
		series.setDescription(name);
		collection.addSeries(series);
		return series;
	}

	public IExtractorInputReader getTestReader() {
		IExtractorInputReader reader = getReaderDao().read(
				new File(getTestPath()));
		return reader;
	}

	public MarkerSetHolder getExpertMarkerSet() {
		MarkerSetHolder expert = getMarkerDao().read(
				new File(getExpertMarksPath()));
		Assert.isTrue(expert != null, "Expert marks not loaded");
		return expert;
	}

	public ReaderDao getReaderDao() {
		return readerDao;
	}

	public void setReaderDao(ReaderDao readerDao) {
		this.readerDao = readerDao;
	}

	public MarkerDao getMarkerDao() {
		return markerDao;
	}

	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}

	public ProcessReader getProcessReader() {
		return processReader;
	}

	public void setProcessReader(ProcessReader processReader) {
		this.processReader = processReader;
	}

	public MakerComparison getMakerComparison() {
		if(makerComparison == null ){
			makerComparison = ExpServiceFactory.createMakerComparison();
		}
		return makerComparison;
	}

	public void setMakerComparison(MakerComparison makerComparison) {
		this.makerComparison = makerComparison;
	}

	public ISegmentatorService getSegmentator() {
		if (segmentator == null) {
			segmentator = SegmentFactory.createSegmentator();
		}
		return segmentator;
	}

	public void setSegmentator(ISegmentatorService segmentator) {
		this.segmentator = segmentator;
	}

	public String getTestPath() {
		return testPath;
	}

	public void setTestPath(String testPath) {
		this.testPath = testPath;
	}

	public String getExpertMarksPath() {
		return expertMarksPath;
	}

	public void setExpertMarksPath(String expertMarksPath) {
		this.expertMarksPath = expertMarksPath;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public boolean isGenerateCharts() {
		return generateCharts;
	}

	public void setGenerateCharts(boolean generateCharts) {
		this.generateCharts = generateCharts;
	}

}
