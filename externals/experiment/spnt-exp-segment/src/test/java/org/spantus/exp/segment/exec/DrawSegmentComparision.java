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
package org.spantus.exp.segment.exec;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.io.SignalReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.StringUtils;
import org.spantus.work.io.WorkAudioFactory;
import org.spantus.work.services.WorkServiceFactory;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created Oct 14, 2008
 * 
 */
public class DrawSegmentComparision extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String FILE_wavName = "../../../data/t_1_2.wav";
	public static String FILE_markerName = "../../../data/t_1_2_expert.mspnt.xml";

	/**
	 * A demonstration application showing an XY series containing a null value.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public DrawSegmentComparision(final String title, List<String> wavName,
			String markerName) {
		super(title);
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(
				new NumberAxis("Time"));
		plot.setGap(10.0);
		plot.setOrientation(PlotOrientation.VERTICAL);

		XYSeries[] seriesArr = createSeries(wavName, markerName);
		for (XYSeries series : seriesArr) {
			final XYSeriesCollection data1 = new XYSeriesCollection(series);
			final XYItemRenderer renderer1 = new StandardXYItemRenderer();
			final NumberAxis rangeAxis1 = new NumberAxis(series
					.getDescription());
			final XYPlot subplot = new XYPlot(data1, null, rangeAxis1,
					renderer1);
			plot.add(subplot, 1);
		}

		final JFreeChart chart = new JFreeChart("Segmentation Result",
				JFreeChart.DEFAULT_TITLE_FONT, plot, false);

		final ChartPanel chartPanel = new ChartPanel(chart);

		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}

	public IExtractorInputReader readSignal(List<String> wavName) {
		try {
			List<URL> urlFiles = new ArrayList<URL>();
			for (String name : wavName) {
				if (StringUtils.hasText(name)) {
					File wavFile = new File(name);
					urlFiles.add(wavFile.toURI().toURL());
				}
			}
			SignalReader reader = WorkAudioFactory.createAudioReader(urlFiles
					.get(0));
			SignalFormat format = reader.getFormat(urlFiles.get(0));
			IExtractorConfig config = ExtractorConfigUtil.defaultConfig(format
					.getSampleRate(), 30, 66);
			IExtractorInputReader bufferedReader = ExtractorsFactory
					.createReader(config);

			ExtractorUtils
					.registerThreshold(
							bufferedReader,
							new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR, },
							null, ClassifierEnum.rules);
			reader.readSignal(urlFiles, bufferedReader);
			return bufferedReader;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return The dataset.
	 */
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	private XYSeries[] createSeries(List<String> wavName, String markerName) {

		IExtractorInputReader reader = readSignal(wavName);
		Set<IClassifier> thresholds = new HashSet<IClassifier>();
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if (extractor instanceof IClassifier)
				thresholds.add((IClassifier) extractor);
		}
		MarkerSetHolder testMarkerSet = extractSegments(thresholds);
		MarkerSetHolder holder = WorkServiceFactory.createMarkerDao().read(
				new File(markerName));
		ComparisionResult result = ExpServiceFactory.createMakerComparison()
				.compare(holder, testMarkerSet);
		WorkServiceFactory.createMarkerDao().write(testMarkerSet,
				new File("tst.mspnt.xml"));

		final XYSeries[] series = new XYSeries[] {
				new XYSeries("Segmentation Result"), new XYSeries("Original"),
				new XYSeries("Test"), };

		int i = 0;
		series[0].setDescription("Result");
		for (Float f1 : result.getSequenceResult()) {
			series[0].add(Float.valueOf(i), f1);
			i++;
		}
		i = 0;
		series[1].setDescription("Original");
		for (Float f1 : result.getOriginal()) {
			series[1].add(Float.valueOf(i), f1);
			i++;
		}
		i = 0;
		series[2].setDescription("Test");
		for (Float f1 : result.getTest()) {
			series[2].add(Float.valueOf(i), f1);
			i++;
		}

		return series;

	}

	/**
	 * extractuion with params
	 * 
	 * @param classifiers
	 * @return
	 */
	protected MarkerSetHolder extractSegments(Set<IClassifier> classifiers) {
		MarkerSetHolder testMarkerSet = null;
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinSpace(0L);
		param.setMinLength(0L);
		param.setExpandStart(0L);
		param.setExpandEnd(0L);
		ISegmentatorService online = (ISegmentatorService) SegmentFactory
				.createSegmentator(SegmentatorServiceEnum.online.name());
		testMarkerSet = online.extractSegments(classifiers, param);
		return testMarkerSet;
	}

	public static void main(final String[] args) {

		String wavName = FILE_wavName;
		String markerName = FILE_markerName;
		String noise = null;
		//    	
		String root = "/home/studijos/wav/data/";
		wavName = root + "accelerometer.txt";
//		noise = root + "accelerometer.noises.txt";
//		noise = root + "accelerometer.noises.1-2.txt";
		noise = root + "accelerometer.noises.2-0.txt";
//		noise = root + "accelerometer.noises.5-0.txt";
//		noise = root + "accelerometer.noises.10-0.txt";
		
		markerName = root + "accelerometer.mspnt.xml";
		
		
		List<String> fileNames = new ArrayList<String>();
		fileNames.add(wavName);
		fileNames.add(noise);

		final DrawSegmentComparision demo = new DrawSegmentComparision(
				"Segmenation Result", fileNames, markerName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}
