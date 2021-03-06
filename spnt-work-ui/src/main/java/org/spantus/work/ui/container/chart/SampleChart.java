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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.SignalSelectionListener;
import org.spantus.chart.bean.ChartInfo;
import org.spantus.chart.impl.MarkeredTimeSeriesMultiChart;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventMulticaster;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.container.marker.MarkerComponentEventHandler;
import org.spantus.work.ui.dto.SelectionDto;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.i18n.I18nFactory;

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

	Logger log = Logger.getLogger(SampleChart.class);

	public static final String CHOOSE_SAMPLE = "clickToChooseSample";
	private static final long serialVersionUID = 1L;
	private IExtractorInputReader reader;
	private SignalSelectionListener selectionListener;
	private SpantusWorkInfo info;
	private AbstractSwingChart chart;
	private SpantusEventMulticaster eventMulticaster;
	private MarkerComponentEventHandler markerComponentEventHandler;

	
	
	public SampleChart(SpantusEventMulticaster eventMulticaster) {
		super(new BorderLayout());
		this.eventMulticaster = eventMulticaster;
		setBorder(BorderFactory.createLineBorder(Color.black));
		markerComponentEventHandler = new MarkerComponentEventHandler(getInfo(), eventMulticaster);
		this.setSize(300, 200);

	}

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
        public void updateMarker() {
            if( chart instanceof MarkeredTimeSeriesMultiChart ){
            	MarkeredTimeSeriesMultiChart _chart = ((MarkeredTimeSeriesMultiChart)chart);
                _chart.getMarkerGraph().repaint(30L);
            }
        }


	private void initializeChart(IExtractorInputReader reader) {
		removeAll();
		if (reader != null) {
			chart = ChartFactory.createChart(reader,
					new I18NChartDescriptionResolver());
			chart.setCharInfo(createChartInfo());
			chart.setPreferredSize(getSize());
			add(chart, BorderLayout.CENTER);
			if(!ProjectTypeEnum.feature.name().equals(getInfo().getProject().getType())
					&&
					getInfo().getProject().getSample().getMarkerSetHolder() != null){
				if( chart instanceof MarkeredTimeSeriesMultiChart ){
					MarkeredTimeSeriesMultiChart _chart = ((MarkeredTimeSeriesMultiChart)chart);
					getMarkerComponentEventHandler().setChart(_chart);
					_chart.initialize(getInfo().getProject().getSample().getMarkerSetHolder()
							,getMarkerComponentEventHandler()
							,getMarkerComponentEventHandler()
							,getMarkerComponentEventHandler());
//					_chart.addMouseListener(getMarkerComponentEventHandler());
//					_chart.addKeyListener(getMarkerComponentEventHandler());
					
				}else{
					chart.initialize();
				}
				
				
			}else{
				chart.initialize();
			}
			chart.addSignalSelectionListener(getSelectionListener());

			
			
		}else{
			JButton btn = new JButton(I18nFactory.createI18n().getMessage(CHOOSE_SAMPLE));
			btn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SpantusEvent event = SpantusEvent.createEvent(
							this,
							GlobalCommands.file.open.name());
					getEventMulticaster().multicastEvent(event);
				}
			});
			btn.setBorder(BorderFactory.createEmptyBorder());
			add(btn, BorderLayout.CENTER);
			
		}
		repaint(30L);
	}
	
	protected ChartInfo createChartInfo(){
		ChartInfo chartInfo = new ChartInfo();
		chartInfo.setGrid(Boolean.TRUE.equals(getInfo().getEnv().getGrid()));
		chartInfo.setSelfZoomable(false);
		chartInfo.setColorSchema(getInfo().getEnv().getVectorChartColorTypes());
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
			log.debug("repaint");
			getChart().setSize(getSize());
//			getChart().repaint();
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

		public void selectionChanged(Double  from, Double length) {
			SelectionDto selectionDto = new SelectionDto(from, length);
			SpantusEvent event = SpantusEvent.createEvent(
					this,
					GlobalCommands.sample.selectionChanged.name(), selectionDto );
			getEventMulticaster().multicastEvent(event);
		}

		public void play() {

		}

	}

	public AbstractSwingChart getChart() {
		return chart;
	}


	protected MarkerComponentEventHandler getMarkerComponentEventHandler() {
		return markerComponentEventHandler;
	}
	

	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}

//	public void setEventMulticaster(SpantusEventMulticaster eventMulticaster) {
//		this.eventMulticaster = eventMulticaster;
//	}

}
