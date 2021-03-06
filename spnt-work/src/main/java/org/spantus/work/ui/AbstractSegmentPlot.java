package org.spantus.work.ui;

import java.awt.BorderLayout;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.bean.ChartInfo;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorListenerOnline;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public abstract class AbstractSegmentPlot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IExtractorInputReader reader = null;
	private WraperExtractorReader wraperExtractorReader = null;
	private AbstractSwingChart chart = null;	
	private Logger log = Logger.getLogger(SegmentMonitorPlot.class);

	
	public AbstractSegmentPlot() {
		setLayout(new BorderLayout());
	}
	
	public WraperExtractorReader getWraperExtractorReader() {
		if(wraperExtractorReader==null){
			wraperExtractorReader =  new RecordWraperExtractorReader(reader);
			wraperExtractorReader.setFormat(getFormat());
		}
		return wraperExtractorReader;
	}

	protected MultipleSegmentatorListenerOnline createSegmentatorRecordable(){
		RecordSegmentatorOnline multipleSegmentator = new RecordSegmentatorOnline();
		multipleSegmentator.setParam(createParam());
		multipleSegmentator.setReader((RecordWraperExtractorReader)getWraperExtractorReader());
		return multipleSegmentator;
	}
	
	protected DecisionSegmentatorOnline createSegmentatorDefault(){
		DecisionSegmentatorOnline multipleSegmentator = new DecisionSegmentatorOnline();
		multipleSegmentator.setParam(createParam());
		return multipleSegmentator;
	}
	
	protected OnlineDecisionSegmentatorParam createParam(){
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(100L);
		param.setMinSpace(80L);
		param.setExpandStart(50L);
		return param;
	}
        /**
         * Show only minimum charts: {@link IClassifier} only.
         * @see  {@link #initGraphMaximum(org.spantus.core.extractor.IExtractorInputReader)  }
         * @param reader
         */
        protected void initGraphMinimum(IExtractorInputReader reader) {
            boolean showClassifiersOnly = true;
            initGraph(reader, showClassifiersOnly);
        }
        /**
         * Show all charts. 
         * @see  {@link #initGraphMaximum(org.spantus.core.extractor.IExtractorInputReader) }
         * @param reader
         */
        protected void initGraphMaximum(IExtractorInputReader reader) {
            boolean showClassifiersOnly = false;
            initGraph(reader, showClassifiersOnly);
        }        
        

        
	private void initGraph(IExtractorInputReader reader, boolean showClassifiersOnly) {
		chart = ChartFactory.createChart(reader);
                chart.setShowClassifiersOnly(showClassifiersOnly);
		ChartInfo chartInfo = new ChartInfo();
		chartInfo.setGrid(Boolean.TRUE);
		chartInfo.setSelfZoomable(false);
//		chartInfo.setColorSchema(getInfo().getEnv().getVectorChartColorTypes());
		chart.setCharInfo(chartInfo);
		this.add(chart,BorderLayout.CENTER);
		chart.initialize();
	}
	
	public abstract AudioFormat getFormat();
	
	public void showChartFrame(){
		JFrame chartFrame = new JFrame();
		chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chartFrame.getContentPane().add(this);
		chartFrame.setSize(640, 480);
		chartFrame.validate();
		chartFrame.setVisible(true);
		this.setSize(640, 480);
	}
	
	public void stopRecognition(){
//		timer.cancel();
	}
	
	public void startRecognition(){
	}
	
	public AbstractSwingChart getChart() {
		return chart;
	}
	
	public void repaint() {
		if (getChart() != null) {
			try{
//				getChart().repaint();
				getChart().setSize(getSize());
			}catch(Exception e){
				log.error(e);
			}
		}
		super.repaint();
	}
	
	public IExtractorInputReader getReader() {
		return reader;
	}

	public void setReader(IExtractorInputReader reader) {
		this.reader = reader;
	}

}
