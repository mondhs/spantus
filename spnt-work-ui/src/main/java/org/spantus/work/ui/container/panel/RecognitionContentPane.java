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
import org.spantus.externals.recognition.ui.RecognitionPanel;
import org.spantus.externals.recognition.ui.RecognitionToolBar;
/**
 * 
 * Recognition UI.
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 26, 2010
 *
 */
public class RecognitionContentPane extends AbstractSpantusContentPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RecognitionToolBar toolBar;
	private RecognitionPanel recognitionPanel;
	
	public RecognitionContentPane(SpantusEventMulticaster eventMulticaster) {
		super(eventMulticaster);
		this.setLayout(new BorderLayout());
		toolBar = new RecognitionToolBar(eventMulticaster);
		recognitionPanel = new RecognitionPanel(eventMulticaster);
		this.add(recognitionPanel ,BorderLayout.CENTER);
		this.add(toolBar,BorderLayout.NORTH);
	}

	public void initialize() {
                recognitionPanel.setRepositoryPath(getInfo().getProject().getRecognitionConfig().getRepositoryPath());
		toolBar.initialize();
		recognitionPanel.initialize();
		
	}

	public void reload() {
		
	}
	
	public JToolBar getToolBar() {
		return toolBar;
	}
	
}
