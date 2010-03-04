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

import org.spantus.externals.recognition.ui.AdminPanel;
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

	public RecognitionContentPane() {
		this.setLayout(new BorderLayout());
		AdminPanel adminPanel = new AdminPanel();
		this.add(adminPanel,BorderLayout.CENTER);
	}

	public void initialize() {
		
	}

	public void reload() {
		
	}
	
	public JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new RecognitionToolBar();
		}
		return toolBar;
	}
	
}
