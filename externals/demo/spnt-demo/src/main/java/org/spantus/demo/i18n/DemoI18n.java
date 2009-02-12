/*
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
package org.spantus.demo.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class DemoI18n implements I18n {

	public static final String PROPERTIES_FILE_NAME = "org.spantus.demo.res.messages";
	public static final Locale LITHUANIAN = new Locale("lt", "LT");
	private Locale locale;
	private ResourceBundle bundle;
	private Logger log = Logger.getLogger(getClass());

	public Locale getLocale() {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	public ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = ResourceBundle
					.getBundle(PROPERTIES_FILE_NAME, getLocale());
		}
		return bundle;
	}

	public String getMessage(String key) {
		String rtnStr = key;
		if (key.equals(I18nResourcesEnum.appletAboutHtml.getCode())) {
			return getMessageHtml(key);
		}
		try {
			rtnStr = getBundle().getString(key);
		} catch (MissingResourceException e) {
//			System.out.println(key + "=" + key);
			log.debug("Resource not fount: " + key);
//			e.printStackTrace();
		}
		return rtnStr;
	}

	public String getMessageHtml(String key) {
		InputStream in = getClass().getResourceAsStream(key);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
