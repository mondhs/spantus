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

import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18n;
import org.spantus.work.ui.i18n.I18nFactory;

public abstract class AbsrtactCmd implements SpantusWorkCommand {
	
	private Logger log = Logger.getLogger(AbsrtactCmd.class);
	
	private CommandExecutionFacade executionFacade;
	SpantusWorkUIEvent currentEvent;
	
	public AbsrtactCmd(CommandExecutionFacade executionFacade) {
		this.executionFacade = executionFacade;
	}
	
	public void execute(SpantusWorkUIEvent event){
		log.debug("[execute][{0}] cmd:{1};[{2}]",getClass().getName(),event.getCmd(), event.getValue());
		this.currentEvent = event;
		String newCmd = execute(event.getCtx());
		if(newCmd != null){
			getExecutionFacade().fireEvent(newCmd);
		}
	}
	
	public abstract String execute(SpantusWorkInfo ctx);
	
	
	protected Set<String> createExpectedActions(Enum<?> enumVal){
		return createExpectedActions(enumVal.name());
	}
	
	protected Set<String> createExpectedActions(String... actions){
		Set<String> actionSet = new HashSet<String>();
		for (String action : actions) {
			actionSet.add(action);
		}
		return actionSet;
	}
	
	protected void error(String message, SpantusWorkInfo ctx){
		String messageBody = getMessage(message);
		log.error(messageBody);
		
//		if(Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())){
			JOptionPane.showMessageDialog(null,messageBody,
					getMessage("Error"),
					JOptionPane.ERROR_MESSAGE);	
//		}		
	}
	
	protected String getMessage(String key){
		return getI18n().getMessage(key);
	}
	protected I18n getI18n(){
		return I18nFactory.createI18n();
	}

	public CommandExecutionFacade getExecutionFacade() {
		return executionFacade;
	}

	public SpantusWorkUIEvent getCurrentEvent() {
		return currentEvent;
	}
}
