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
package org.spantus.work.ui.cmd;

import java.awt.Frame;

import org.spantus.work.ui.container.panel.SpantusAboutDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Aug 26, 2008
 *
 */
public class AboutCmd extends AbsrtactCmd {


	private SpantusAboutDialog about;
//	private Frame frame;
	
//	public AboutCmd(Frame frame){
//		this.frame = frame;
//	}
	

	public AboutCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	
	public String execute(SpantusWorkInfo ctx){
		getAboutPnl().setVisible(true);
		return null;
	}

	private SpantusAboutDialog getAboutPnl(){
		if(about == null){
			about = new SpantusAboutDialog(null);
			about.setModal(true);
		}
		return about;
	}
}
