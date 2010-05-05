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

import java.awt.Dimension;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.segment.ExpUtils;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.impl.ComarisionFacadeImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.CollectionUtils;
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

	private List<String> wavName = null;
	private String markerName = null;
	private OnlineDecisionSegmentatorParam param;
	private ExtractorEnum[] extractors; 
	private ComarisionFacadeImpl comarisionFacade; 
	private Map<String, ExtractorParam> extractorParams;

	public DrawSegmentComparision(final String title) {
		super(title);
		comarisionFacade = new ComarisionFacadeImpl();
	}
	/**
	 * A demonstration application showing an XY series containing a null value.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public DrawSegmentComparision(final String title, List<String> wavName,
			String markerName) {
		super(title);
		this.wavName = wavName;
		this.markerName = markerName;
		comarisionFacade = new ComarisionFacadeImpl(); 
	}
	
	/**
	 * initialize
	 */
	public void init(){
		final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(
				new NumberAxis("Time"));
		plot.setGap(10.0);
		plot.setOrientation(PlotOrientation.VERTICAL);

		XYSeries[] seriesArr = createSeries(getWavName(), getMarkerName());
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
		setContentPane(chartPanel);
		chartPanel.setPreferredSize(new Dimension(500, 270));
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
	private XYSeries[] createSeries(List<String> signalName, String markerName) {
		comarisionFacade.setExtractorParams(getExtractorParams());
		
		MarkerSetHolder testMarkerSet = comarisionFacade.calculateMarkers(signalName, getExtractors(), getParam());
		
		MarkerSetHolder holder = WorkServiceFactory.createMarkerDao().read(
				new File(markerName));
		ComparisionResult result = ExpServiceFactory.createMakerComparison()
				.compare(holder, testMarkerSet);
//		WorkServiceFactory.createMarkerDao().write(testMarkerSet,
//				new File("tst.mspnt.xml"));

		final XYSeries[] series = new XYSeries[] {
				new XYSeries("Segmentation Result"), new XYSeries("Original"),
				new XYSeries("Test"), };

		ExpUtils.fillSeries(result.getSequenceResult(), series[0], "Result");
		ExpUtils.fillSeries(result.getOriginal(), series[1], "Original");
		ExpUtils.fillSeries(result.getTest(), series[2], "Test");
		
		return series;

	}
	

	
	
	public static void populateAcceleromerData(DrawSegmentComparision demo){
		String wavName = null;
		String markerName = null;
		String noise = null;
		
		String root = "/home/studijos/wav/data/";
		wavName = root + "iaccelerometer.txt";
		markerName = root + "iaccelerometer_system.mspnt.xml";
		root += "noises/";
//		noise = root + "accelerometer.noises.txt";
//		noise = root + "accelerometer.noises.1-2.txt";
		noise = root + "accelerometer.noises.2-0.txt";
//		noise = root + "accelerometer.noises.5-0.txt";
//		noise = root + "accelerometer.noises.10-0.txt";
		demo.getParam().setExpandEnd(30L);
		demo.getParam().setExpandStart(30L);
		
		demo.setWavName(CollectionUtils.toList(wavName, noise));
		demo.setMarkerName(markerName);
	}
	public static void populateWav(DrawSegmentComparision demo){
		String wavName = null;
		String markerName = null;
		String noise = null;
		Map<String, ExtractorParam> extractorParams = new HashMap<String, ExtractorParam>();
		
		String root = "/home/studijos/wav/on_off_up_down_wav/";
//		wavName = root + "dentist.wav";
		wavName = root + "hammer.wav";
//		wavName = root + "keyboard.wav";
//		wavName = root + "original.wav";
//		wavName = root + "plane.wav";
//		wavName = root + "rain.wav";
//		wavName = root + "shower.wav";
//		wavName = root + "traffic.wav";

		markerName = root + "exp/_on_off_up_down.mspnt.xml";
		demo.setWavName(CollectionUtils.toList(wavName, noise));
		demo.setMarkerName(markerName);
		demo.getParam().setMinSpace(60L);
		demo.getParam().setMinLength(90L);
//		demo.getParam().setExpandEnd(60L);
//		demo.getParam().setExpandStart(60L);
		demo.setExtractors(new ExtractorEnum[] { 
				ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR});
		
		
		ExtractorParam extractorParam = new ExtractorParam();
		extractorParam.getProperties().put(ExtractorModifiersEnum.mean.name(), Boolean.TRUE);
		extractorParam.setClassName(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name());
		extractorParams.put(extractorParam.getClassName(), extractorParam);

		demo.setExtractorParams(extractorParams);

	}
	////////// MAIN ///////////////
	
	public static void main(final String[] args) {

		String wavName = FILE_wavName;
		String markerName = FILE_markerName;
		String noise = null;
		
		
		DrawSegmentComparision demo = new DrawSegmentComparision(
				"Segmenation Result");
		
		demo.setWavName(CollectionUtils.toList(wavName, noise));
		demo.setMarkerName(markerName);
		populateAcceleromerData(demo);
//		populateWav(demo);
		demo.init();
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}
	
	
	
//////////////Getters setters
	public List<String> getWavName() {
		return wavName;
	}
	public void setWavName(List<String> wavName) {
		this.wavName = wavName;
	}
	public String getMarkerName() {
		return markerName;
	}
	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}
	public ExtractorEnum[] getExtractors() {
		if(extractors == null){
			extractors = new ExtractorEnum[] { ExtractorEnum.ENERGY_EXTRACTOR};
		}
		return extractors;
	}
	public void setExtractors(ExtractorEnum[] extractors) {
		this.extractors = extractors;
	}
	public OnlineDecisionSegmentatorParam getParam() {
		if(param == null){
			OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
			param.setMinSpace(0L);
			param.setMinLength(0L);
			param.setExpandStart(0L);
			param.setExpandEnd(0L);
			this.param = param;
		}
		return param;
	}
	public void setParam(OnlineDecisionSegmentatorParam param) {
		this.param = param;
	}
	public Map<String, ExtractorParam> getExtractorParams() {
		return extractorParams;
	}
	public void setExtractorParams(Map<String, ExtractorParam> params) {
		this.extractorParams = params;
	}
	
	

}
