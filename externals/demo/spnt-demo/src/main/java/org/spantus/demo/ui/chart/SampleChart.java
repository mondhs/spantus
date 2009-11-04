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

package org.spantus.demo.ui.chart;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.SignalSelectionListener;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.demo.audio.AudioManagerFactory;
import org.spantus.demo.dto.DemoAppletInfo;
/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created May 21, 2008
 *
 */
public class SampleChart extends JPanel {

	private static final long serialVersionUID = 1L;
	private IExtractorInputReader reader;
	SignalSelectionListener selectionListener;
	DemoAppletInfo info;
	AbstractSwingChart chart;

	public DemoAppletInfo getInfo() {
		if (info == null) {
			info = new DemoAppletInfo();
		}
		return info;
	}

	public void setInfo(DemoAppletInfo info) {
		this.info = info;
	}

	/**
	 * This is the default constructor
	 */
	public SampleChart() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300, 200);

		initializeChart(getReader());
	}

	private void initializeChart(IExtractorInputReader reader) {
		removeAll();
		if (reader != null) {
			chart = ChartFactory.createChart(reader);
			if(!getInfo().getRecording()){
				chart.getCharInfo().setPlayable(getInfo().getCurrentSample().isSamplePlayable());
			}else{
				chart.getCharInfo().setPlayable(false);
			}
			chart.setPreferredSize(getSize());
			chart.addSignalSelectionListener(getSelectionListener());
			add(chart, BorderLayout.CENTER);
			chart.initialize();
			repaint();
		}
	}

	public IExtractorInputReader getReader() {
		return reader;
	}

	public void setReader(IExtractorInputReader reader) {
		this.reader = reader;
		initializeChart(reader);
	}

	
	public void repaint() {
		if (getChart() != null) {
			getChart().setSize(getSize());
			getChart().repaint();
		}
		super.repaint();
	}

	private SignalSelectionListener getSelectionListener() {
		if (selectionListener == null) {
			selectionListener = new LocalSignalSelectionListener();
		}
		return selectionListener;
	}

	private class LocalSignalSelectionListener implements
			SignalSelectionListener {
		
		public void selectionChanged(float from, float length) {
			getInfo().setFrom(from);
			getInfo().setLength(length);
		}

		
		public void play() {
			AudioManagerFactory.createAudioManager().play(getInfo().getCurrentSample().getUrl(), 
					getInfo().getFrom(),
					getInfo().getLength()
					);	
			
		}

	}

	public AbstractSwingChart getChart() {
		return chart;
	}

}
