
/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.work.ui.dto;

import java.io.Serializable;
import java.util.Locale;

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

	private Locale locale;
	
	EnviromentRepresentation env;
	
	private SpantusWorkProjectInfo project;

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
			locale = Locale.getDefault();
		}
		return locale;
	}

	public void setLocale(Locale currentLocale) {
		this.locale = currentLocale;
	}

}
