
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

import org.spantus.work.ui.dto.SelectionDto;
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
public class ZoomOutCmd extends AbsrtactCmd {

	
	public ZoomOutCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.zoomout);
	}
	public String execute(SpantusWorkInfo ctx) {
		SelectionDto selectionDto = new SelectionDto();
		getExecutionFacade().fireEvent(GlobalCommands.sample.selectionChanged, selectionDto);
		//FIXME: hack CommandExecutionFacadeImpl#changedZoom
		((CommandExecutionFacadeImpl)getExecutionFacade()).changedZoom(null, null);
		return null;
	}

}
