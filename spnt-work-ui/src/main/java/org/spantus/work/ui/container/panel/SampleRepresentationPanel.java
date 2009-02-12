package org.spantus.work.ui.container.panel;

import java.awt.BorderLayout;
import java.text.MessageFormat;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class SampleRepresentationPanel extends JPanel implements SampleChangeListener, ReloadableComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5470586542419788864L;
	
	Logger log = Logger.getLogger(getClass());
	
	SpantusWorkInfo info;
	SampleChart sampleChart;
	JPanel statusBar;
	JProgressBar progress;
	
	public SampleRepresentationPanel() {
		setLayout(new BorderLayout());
	}
	
	public void initialize() {
		removeAll();
		add(getSampleChart(),BorderLayout.CENTER);
		add(getStatusBar(), BorderLayout.SOUTH);
	}
	public void reload() {
		// TODO Auto-generated method stub
	}


	public SampleChart getSampleChart() {
		if(sampleChart == null){
			sampleChart = new SampleChart();
			sampleChart.setInfo(getInfo());
			sampleChart.initialize();
		}
		return sampleChart;
	}
	
	public JPanel getStatusBar() {
		if(statusBar == null){
			statusBar = new JPanel(new BorderLayout());
			progress = new JProgressBar(0, 100);
			progress.setValue(0);
			progress.setStringPainted(true);
			statusBar.add(progress, BorderLayout.CENTER);
			statusBar.setVisible(false);
		}
		return statusBar;
	}
	
	public void setSampleChart(SampleChart sampleChart) {
		this.sampleChart = sampleChart;
	}
	
	public SpantusWorkInfo getInfo() {
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		getSampleChart().setInfo(info);
		this.info = info;
	}

	public SpantusWorkCommand getHandler() {
		return getSampleChart().getHandler();
	}

	public void setHandler(SpantusWorkCommand handler) {
		getSampleChart().setHandler(handler);
	}

	
	
	public void changedReader(IExtractorInputReader reader) {
		getSampleChart().setReader(reader);
	}


	public void refresh() {
		if(getSampleChart() != null && getSampleChart().getChart()!=null){
			getSampleChart().
			getChart().
			repaint();
		}
	}

	short processedPercent = 0;
	
	long startedTime = 0;
	
	public void processed(Long current, Long total){
		if(total != null){
			Float percentL = (current.floatValue()/total)*100;
			Short percent = Short.valueOf(percentL.shortValue());
			if(percent.shortValue() != processedPercent){
				progress.setValue(percent);
				long processedTime = System.currentTimeMillis() - startedTime; 
				long timePerUnit = processedTime/percent;
				short percentLeft = 100;
				percentLeft -= percent.shortValue();
				Float timeToLeft = ((float)timePerUnit*percentLeft)/1000f;
//				log.debug(MessageFormat.format("processed {0}s", timeToLeft));
				progress.setToolTipText(MessageFormat.format("{0} ms", timeToLeft));
				processedPercent = percent;
			}
		}
	}

	public void registerProcessedFrameLinstener(
			ProcessedFrameLinstener linstener) {
		throw new RuntimeException("Not impl");
	}

	public void ended() {
		getStatusBar().setVisible(false);		
		processedPercent = 0;
		startedTime = 0;
	}

	public void started(Long total) {
		getStatusBar().setVisible(true);
		processedPercent = 0;
		startedTime = System.currentTimeMillis();
	}
	
}
