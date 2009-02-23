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

import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18n;
import org.spantus.work.ui.i18n.I18nFactory;

public abstract class AbsrtactCmd implements SpantusWorkCommand {
	
	private Logger log = Logger.getLogger(AbsrtactCmd.class);
	
	public String execute(String cmdName, SpantusWorkInfo ctx){
		log.debug("[execute][{0}] cmd:{1};",getClass().getName(),cmdName);
		return execute(ctx);
	}
	public abstract String execute(SpantusWorkInfo ctx);
	
	protected String getMessage(String key){
		return getI18n().getMessage(key);
	}
	protected I18n getI18n(){
		return I18nFactory.createI18n();
	}
}
