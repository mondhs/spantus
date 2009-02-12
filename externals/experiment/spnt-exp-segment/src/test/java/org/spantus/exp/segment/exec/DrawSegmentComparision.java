/**
 * 
 */
package org.spantus.exp.segment.exec;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
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
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioFactory;
import org.spantus.core.io.AudioReader;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.SegmentFactory;
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

	/**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public DrawSegmentComparision(final String title) {
        super(title);
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Time"));
        plot.setGap(10.0);
        plot.setOrientation(PlotOrientation.VERTICAL);
        
        XYSeries[] seriesArr = createSeries();
        for (XYSeries series : seriesArr) {
        	 final XYSeriesCollection data1 = new XYSeriesCollection(series);
             final XYItemRenderer renderer1 = new StandardXYItemRenderer();
             final NumberAxis rangeAxis1 = new NumberAxis(series.getDescription());	
             final XYPlot subplot = new XYPlot(data1, null, rangeAxis1, renderer1);
             plot.add(subplot,1);
        }
       
        

        
        final JFreeChart chart = new JFreeChart("Segmentation Result",
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        
        
        
        final ChartPanel chartPanel = new ChartPanel(chart);
        
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

	public IExtractorInputReader readSignal()
			{
		File wavFile = new File("./target/test-classes/t_1_2.wav");
		URL urlFile;
		try {
			urlFile = wavFile.toURI().toURL();
			AudioReader reader = AudioFactory.createAudioReader();
			IExtractorInputReader bufferedReader = ExtractorsFactory
					.createReader(reader.getAudioFormat(urlFile));
			ExtractorUtils.registerThreshold(bufferedReader, new ExtractorEnum[] {
					ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR
			});
			reader.readAudio(urlFile, bufferedReader);
			return bufferedReader;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
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
    private XYSeries[] createSeries() {

        IExtractorInputReader reader = readSignal();
		Set<IThreshold> thresholds = new HashSet<IThreshold>();
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if(extractor instanceof IThreshold)
				thresholds.add((IThreshold)extractor);
		}
		MarkerSet testMarkerSet = SegmentFactory.createSegmentator().extractSegments(thresholds);
		MarkerSetHolder holder = WorkServiceFactory.createMarkerDao().read(new File("./target/test-classes/t_1_2.mrk.xml"));
		MarkerSet originalMarkerSet = holder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		ComparisionResult result = ExpServiceFactory.createMakerComparison().compare(originalMarkerSet, testMarkerSet);
    	
        final XYSeries[] series = new XYSeries[]{
        		new XYSeries("Segmentation Result"),
        		new XYSeries("Original"),
        		new XYSeries("Test"),
        };
        
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

    public static void main(final String[] args) {

        final DrawSegmentComparision demo = new DrawSegmentComparision("Segmenation Result");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
