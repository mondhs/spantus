/*
 	Copyright (c) 2009, 2010 Mindaugas Greibus (spantus@gmail.com)
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
import java.util.Set;

import org.spantus.work.ui.container.panel.SignalInfoDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class SignalInfoCmd extends AbsrtactCmd {


	private SignalInfoDialog info;
	private Frame frame;
	
	public SignalInfoCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.help.signalInfo);
	}
	
	public String execute(SpantusWorkInfo ctx){
		getInfoPnl().setCtx(ctx);
		getInfoPnl().setVisible(true);
		return null;
	}

	private SignalInfoDialog getInfoPnl(){
		if(info == null){
			info = new SignalInfoDialog(frame);
			
			info.setModal(true);
		}
		return info;
	}
}
