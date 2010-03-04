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

import javax.swing.JToolBar;

import org.spantus.event.SpantusEventMulticaster;
import org.spantus.work.ui.container.SpantusWorkToolbar;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * Pane for segmentation, feture and record projects. has its own toolbar and panel
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 26, 2010
 *
 */
public class SegmentationContentPane extends AbstractSpantusContentPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SampleRepresentationPanel sampleRepresentationPanel;
	private SpantusWorkToolbar toolBar = null;
	private SpantusWorkInfo info;
	private SpantusEventMulticaster eventMulticaster;
	
	public SegmentationContentPane() {
		setLayout(new BorderLayout());
		toolBar = new SpantusWorkToolbar();
		add(getToolBar(), BorderLayout.NORTH);
		this.add(getSampleRepresentationPanel(),BorderLayout.CENTER);	
	}
	
	public void initialize() {
		toolBar.initialize();
		getSampleRepresentationPanel().initialize();
	}
	public void reload() {
		toolBar.reload();
		getSampleRepresentationPanel().reload();
	}
	
	public SampleRepresentationPanel getSampleRepresentationPanel() {
		if (sampleRepresentationPanel == null) {
			sampleRepresentationPanel = new SampleRepresentationPanel();
		}
		return sampleRepresentationPanel;
	}
	public JToolBar getToolBar() {
		return toolBar;
	}
	
	public SpantusWorkInfo getInfo() {
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		getSampleRepresentationPanel().setInfo(info);
		this.toolBar.setInfo(info);
		this.info = info;
	}

//	public CommandExecutionFacade getExecutionFacade() {
//		return executionFacade;
//	}
//
//	public void setExecutionFacade(CommandExecutionFacade executionFacade) {
//		this.executionFacade = executionFacade;
//	}

	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}

	public void setEventMulticaster(SpantusEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
		toolBar.setEventMulticaster(eventMulticaster);
	}

	
	
	
	
}
