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
package org.spantus.work.ui.cmd;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

import org.spantus.exception.ProcessingException;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * @author mondhs
 * @since 2010 04 11
 */
public class AppendNoiseCmd extends OpenCmd {
	

	public AppendNoiseCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	@Override
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.tool.appendNoise);
	}
	
	@Override
	protected void setSelectedFile(SpantusWorkInfo ctx, File selectedFile){
		try {
			ctx.getProject().getSample().setNoiseFile(
					selectedFile.toURI().toURL());
		} catch (MalformedURLException e1) {
			throw new ProcessingException(e1);
		}
	}
	
	
	

}
