/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.demo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.spantus.demo.audio.AudioManagerFactory;
import org.spantus.demo.cmd.DemoGlobalCommands;
import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.dto.SampleDto;
import org.spantus.demo.services.ReadersEnum;
import org.spantus.demo.services.ServiceFactory;
import org.spantus.demo.ui.DemoOption;
import org.spantus.demo.ui.DemoToolbar;
import org.spantus.demo.ui.IDemoAppletListener;
import org.spantus.demo.ui.SpantusAbout;
import org.spantus.demo.ui.chart.SampleChart;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class SpantusDemoApplet extends JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger log = Logger.getLogger(getClass());

	private JPanel jContentPane = null;
	private DemoToolbar jJToolBarBar = null;
	private SampleChart sampleChart;
	private AppletEventListener listener = null;
	private SpantusAbout about = null;
	private DemoOption option = null;
	private DemoAppletInfo info;
	/**
	 * This is the xxx default constructor
	 */
	public SpantusDemoApplet() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	
	public void init() {
		this.setSize(600, 400);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJJToolBarBar(), BorderLayout.NORTH);
			jContentPane.add(getSampleChart(),BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJToolBarBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			DemoToolbar demoToolBar = new DemoToolbar();
			demoToolBar.setActionListener(getListener());
			demoToolBar.setInfo(getInfo());
			jJToolBarBar = demoToolBar;
			
		}
		return jJToolBarBar;
	}

	private SampleChart getSampleChart(){
		if(sampleChart == null){
			sampleChart = new SampleChart();
			sampleChart.setInfo(getInfo());
		}
		return sampleChart;
	}
	
	
	private AppletEventListener getListener() {
		if (listener == null) {
			listener = new AppletEventListener();
		}
		return listener;
	}
	private SpantusAbout getAboutPnl(){
		if(about == null){
			about = new SpantusAbout((Frame)SwingUtilities.getAncestorOfClass(Frame.class, this));
		}
		return about;
	}
	private DemoOption getOptionPnl(){
		if(option == null){
			option = new DemoOption((Frame)SwingUtilities.getAncestorOfClass(Frame.class, this));
			option.setInfo(getInfo());
			option.setAppletListener(new DemoAppletListener());
		}
		return option;
	}
	
	private void processLoadedSample(URL sample) {
		log.debug("loading: " + getInfo().getCurrentSample().getUrl());
		if (sample != null) {
			getSampleChart().setReader(
					ServiceFactory.createReaderService().getReader(sample,
							getInfo().getCurrentReader()));
		}
	}
	
	class AppletEventListener implements ActionListener{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			DemoGlobalCommands cmd = DemoGlobalCommands .valueOf(e.getActionCommand());
			switch (cmd) {
			case about:
				getAboutPnl().setVisible(true);				
				break;
			case load:
				getInfo().setCurrentSample(((SampleDto)e.getSource()));
				if(getInfo().isSampleLoaded()){
					processLoadedSample(getInfo().getCurrentSample().getUrl());
				}
				break;
			case play:
				if(getInfo().isSampleLoaded() && getInfo().getCurrentSample().isSamplePlayable()){
					AudioManagerFactory.createAudioManager().play(getInfo().getCurrentSample().getUrl(), 
							getInfo().getFrom(),
							getInfo().getLength()
							);	
				}
				break;
			case options:
				if(!getInfo().getCurrentSample().isSamplePlayable()){
					processLoadedSample(getInfo().getCurrentSample().getUrl());
					break;
				}
				getInfo().setCurrentSample(((SampleDto)e.getSource()));
				getOptionPnl().setVisible(true);
				break;
			default:
				throw new RuntimeException("Not implemented: " + cmd);
			}
			
		}
	}

	public DemoAppletInfo getInfo() {
		if(info == null){
			info = new DemoAppletInfo();
			info.getCurrentReader().setReader(ReadersEnum.common);
			info.getCurrentReader().getExtractors().add(ExtractorEnum.SIGNAL_EXTRACTOR.name());
		}
		return info;
	}

	public void setInfo(DemoAppletInfo info) {
		this.info = info;
	}
	public class DemoAppletListener implements IDemoAppletListener{
		
		public void preferencesChanged(DemoAppletInfo info) {
			ActionEvent e = new ActionEvent(info.getCurrentSample(), 0, DemoGlobalCommands.load.name());
			getListener().actionPerformed(e);
		}
	}

}
