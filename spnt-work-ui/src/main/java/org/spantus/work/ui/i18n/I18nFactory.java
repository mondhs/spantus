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

package org.spantus.work.ui.i18n;

import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.06.10
 *
 */
public abstract class I18nFactory {
	static I18n i18n ;
	public static I18n createI18n(){
		if(i18n == null){
			i18n = new DefaultI18n();
		}
		return i18n;
	}
	
	public static I18n createI18n(SpantusWorkInfo info){
		ConfigedI18n configedI18n = new ConfigedI18n();
		configedI18n.setInfo(info);
		i18n = configedI18n;
		return i18n;
	}	
}
