/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.ui.container.panel;

import java.awt.BorderLayout;
import java.text.MessageFormat;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.logger.Logger;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * Represents UI of charts and additional components.
 * @author Mindaugas Greibus
 * 
 * Created Feb 26, 2010
 *
 */
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
		// do nothing
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


	
	
	public void changedReader(IExtractorInputReader reader) {
		getSampleChart().setReader(reader);
	}

	public void refreshValue(Float value) {
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
			Float percentL = ((current.floatValue()+1)/total)*100;
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
