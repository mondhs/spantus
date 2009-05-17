package org.spantus.work.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.io.RecordWraperExtractorReader;
import org.spantus.segment.online.DecistionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorOnline;
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

	protected MultipleSegmentatorOnline getSegmentatorRecordable(){
		RecordSegmentatorOnline multipleSegmentator = new RecordSegmentatorOnline();
		multipleSegmentator.setParam(createParam());
		multipleSegmentator.setReader((RecordWraperExtractorReader)getWraperExtractorReader());
		return multipleSegmentator;
	}
	
	protected DecistionSegmentatorOnline getSegmentatorDefault(){
		DecistionSegmentatorOnline multipleSegmentator = new DecistionSegmentatorOnline();
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
	
	protected void initGraph(IExtractorInputReader reader) {
		chart = ChartFactory.createChart(reader);
//		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		this.add(chart,BorderLayout.CENTER);
	}
	
	public abstract AudioFormat getFormat();
	
	public void showChart(){
		JFrame chartFrame = new JFrame();
		chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chartFrame.getContentPane().add(this);
		chartFrame.setSize(640, 480);
		chartFrame.validate();
		chartFrame.setVisible(true);
		Timer timer = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.setRepeats(false);
		timer.start();
		this.setSize(640, 480);
	}
	
	public AbstractSwingChart getChart() {
		return chart;
	}
	
	public void repaint() {
		if (getChart() != null) {
			try{
				getChart().repaint();
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
