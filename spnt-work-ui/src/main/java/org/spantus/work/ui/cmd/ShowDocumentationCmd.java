
/**
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

package org.spantus.work.ui.cmd;

import java.util.Set;

import org.spantus.work.ui.container.panel.SpantusDocumentationDialog;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Apr 9, 2009
 *
 */
public class ShowDocumentationCmd extends AbsrtactCmd {


	private SpantusDocumentationDialog docDialog;
	
	public ShowDocumentationCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.help.userGuide);
	}
	
	public String execute(SpantusWorkInfo ctx){
		getDocDialog().setVisible(true);
		return null;
	}

	private SpantusDocumentationDialog getDocDialog(){
		if(docDialog == null){
			docDialog = new SpantusDocumentationDialog(null);
			docDialog.setModal(true);
		}
		return docDialog;
	}
}
