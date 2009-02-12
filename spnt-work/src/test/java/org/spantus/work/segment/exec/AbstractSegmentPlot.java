package org.spantus.work.segment.exec;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.io.RecordWraperExtractorReader;
import org.spantus.segment.online.DecistionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

public abstract class AbstractSegmentPlot extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	IExtractorInputReader reader = null;
	WraperExtractorReader wraperExtractorReader = null;
	AbstractSwingChart chart = null;	
	
	protected MultipleSegmentatorOnline getSegmentatorRecordable(){
		wraperExtractorReader =  new RecordWraperExtractorReader(reader);
		wraperExtractorReader.setFormat(getFormat());
		RecordSegmentatorOnline multipleSegmentator = new RecordSegmentatorOnline();
		multipleSegmentator.setParam(createParam());
		multipleSegmentator.setReader((RecordWraperExtractorReader)wraperExtractorReader);
		return multipleSegmentator;
	}
	
	protected MultipleSegmentatorOnline getSegmentatorDefault(){
		wraperExtractorReader = new WraperExtractorReader(reader);
		DecistionSegmentatorOnline multipleSegmentator = new DecistionSegmentatorOnline();
		multipleSegmentator.setParam(createParam());
		return multipleSegmentator;
	}
	
	protected OnlineDecisionSegmentatorParam createParam(){
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(100L);
		param.setMinSpace(80L);
		param.setExpandMarkerInTime(50L);
		return param;
	}
	
	public abstract AudioFormat getFormat();
	
	public void showChart(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		validate();
		setVisible(true);
		Timer timer = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	public void repaint() {
		if (chart != null) chart.repaint();
		super.repaint();
	}

}
