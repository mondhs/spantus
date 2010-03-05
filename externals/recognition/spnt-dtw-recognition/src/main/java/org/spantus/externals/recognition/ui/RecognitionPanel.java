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
package org.spantus.externals.recognition.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventListener;
import org.spantus.event.SpantusEventMulticaster;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Mar 3, 2010
 * 
 */
public class RecognitionPanel extends JPanel implements SpantusEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RecognitionMonitorPlot recognitionPlot;
	
	private AdminPanel adminPanel;
	
	private SpantusEventMulticaster eventMulticaster;
	
	
	public RecognitionPanel(SpantusEventMulticaster eventMulticaster) {
		setLayout(new BorderLayout());
		this.eventMulticaster = eventMulticaster;
        RecognitionUIActionListenerImpl actionListener = new RecognitionUIActionListenerImpl(this);
        eventMulticaster.addListener(actionListener);

		eventMulticaster.addListener(this);
	}
	public void initialize(){
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
				getRecognitionPlot(), getAdminPanel());
		splitPane.setOneTouchExpandable(true);
		add(splitPane);
	}
	
	public RecognitionMonitorPlot getRecognitionPlot(){
		if (recognitionPlot == null) {
			recognitionPlot = new RecognitionMonitorPlot();
//			recognitionPlot.setLearnMode(isLearnMode());
		}
		return recognitionPlot;
	}
	

	
	public AdminPanel getAdminPanel() {
		if(adminPanel == null){
			adminPanel = new AdminPanel();			
		}
		return adminPanel;
	}
	public void setRecognitionPlot(RecognitionMonitorPlot recognitionPlot) {
		this.recognitionPlot = recognitionPlot;
	}
	
	
	public void onEvent(SpantusEvent event) {
		String cmd = event.getCmd();
		RecognitionCmdEnum recCmd = RecognitionCmdEnum.valueOf(cmd);
		switch (recCmd) {
		case learn:
			getRecognitionPlot().setLearnMode(true);
			break;
		case stopLearn:
			getRecognitionPlot().setLearnMode(false);
			break;
		default:
			break;
		}	
		
	}
	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}
	public void setEventMulticaster(SpantusEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
	}
	


}
