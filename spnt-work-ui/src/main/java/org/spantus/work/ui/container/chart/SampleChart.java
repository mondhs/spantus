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

package org.spantus.work.ui.container.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.SignalSelectionListener;
import org.spantus.chart.bean.ChartInfo;
import org.spantus.chart.impl.MarkeredTimeSeriesMultiChart;
import org.spantus.chart.marker.MarkerComponent;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.container.marker.MarkerPopupMenuShower;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;

/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created Jun 11, 2008
 * 
 */
public class SampleChart extends JPanel {

	Logger log = Logger.getLogger(getClass());

	private static final long serialVersionUID = 1L;
	private IExtractorInputReader reader;
	SignalSelectionListener selectionListener;
	SpantusWorkInfo info;
	AbstractSwingChart chart;
	SpantusWorkCommand handler;

	public SpantusWorkInfo getInfo() {
		if (info == null) {
			info = new SpantusWorkInfo();
		}
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	/**
	 * This is the default constructor
	 */
	public SampleChart() {
		super(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.black));
		this.setSize(300, 200);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		initializeChart(getReader());
	}
	
	public void updateContent() {
		initializeChart(getReader());
	}

	private void initializeChart(IExtractorInputReader reader) {
		removeAll();
		if (reader != null) {
			chart = ChartFactory.createChart(reader,
					new I18NChartDescriptionResolver());
			chart.setCharInfo(createChartInfo());
			chart.setPreferredSize(getSize());
			chart.addSignalSelectionListener(getSelectionListener());
			add(chart, BorderLayout.CENTER);
			if(!ProjectTypeEnum.feature.name().equals(getInfo().getProject().getCurrentType())
					&&
					getInfo().getProject().getCurrentSample().getMarkerSetHolder() != null){
				if( chart instanceof MarkeredTimeSeriesMultiChart ){
					MarkeredTimeSeriesMultiChart cart_ = ((MarkeredTimeSeriesMultiChart)chart);
					cart_.initialize(
						getInfo().getProject().getCurrentSample().getMarkerSetHolder(), 
						createMouseListener(),
						createKeyListener());
					
				}
			}
			
			
		}
		repaint();
	}
	
	protected ChartInfo createChartInfo(){
		ChartInfo chartInfo = new ChartInfo();
		chartInfo.setGrid(getInfo().getEnv().getGrid());
		chartInfo.setSelfZoomable(false);
		return chartInfo;
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
			getInfo().getProject().setFrom(from);
			getInfo().getProject().setLength(length);
		}

		public void play() {

		}

	}

	public AbstractSwingChart getChart() {
		return chart;
	}


	protected MouseListener createMouseListener() {
		return new MarkerPopupMenuShower(getInfo(), getHandler());
	}
	
	protected KeyListener createKeyListener(){
		KeyListener listener = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if(32 == keyChar){
                	if(e.getComponent() instanceof MarkerComponent){
                		Marker m = ((MarkerComponent)e.getComponent()).getMarker();
                		getInfo().getProject().setFrom(m.getStart()/1000f);
                		getInfo().getProject().setLength(m.getLength()/1000f);
                		getHandler().execute(GlobalCommands.sample.play.name(), getInfo());
                	}
                		
                }else{
                	log.debug("[keyTyped] name{0}; keyChar{1};", getName(), keyChar);	
                }
        		
            }
        };
        return listener;
	}

	public SpantusWorkCommand getHandler() {
		return handler;
	}

	public void setHandler(SpantusWorkCommand handler) {
		this.handler = handler;
	}

}
