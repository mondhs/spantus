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
package org.spantus.work.ui.dto;

import java.io.Serializable;
import java.util.Locale;

import org.spantus.core.beans.I18n;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.26
 *
 */
public class SpantusWorkInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Locale locale = null;
	
	private EnviromentRepresentation env = null;
	
	private SpantusWorkProjectInfo project = null;

	private boolean playing;

	public boolean getPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public SpantusWorkProjectInfo getProject() {
		return project;
	}

	public void setProject(SpantusWorkProjectInfo project) {
		this.project = project;
	}

	public EnviromentRepresentation getEnv() {
		return env;
	}

	public void setEnv(EnviromentRepresentation env) {
		this.env = env;
	}
	public Locale getLocale() {
		if (locale == null) {
			locale = I18n.LOCALES[1];
//			Locale.Default(locale);
		}
		return locale;
	}

	public void setLocale(Locale currentLocale) {
		this.locale = currentLocale;
//		Locale.setDefault(currentLocale);

	}

}
