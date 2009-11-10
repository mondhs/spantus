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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.spantus.logger.Logger;
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
public class DefaultI18n implements I18n {

	public static final String PROPERTIES_FILE_NAME = "org.spantus.work.ui.res.messages";
	public static final String HTML_PROPERTIES_FILE_NAME = "org.spantus.work.ui.res.html_resources";
	public static final Locale LITHUANIAN = new Locale("lt", "LT");
	private Locale locale;
	private ResourceBundle bundle;
	private ResourceBundle htmlBundle;
	private Logger log = Logger.getLogger(DefaultI18n.class);

	public Locale getLocale() {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
	/**
	 * 
	 * @return
	 */
	public ResourceBundle getBundle() {
		if (bundle == null) {
//			log.debug("Creating new bundle on locale: " + getLocale());
			bundle = ResourceBundle
					.getBundle(PROPERTIES_FILE_NAME, getLocale());
		}
		return bundle;
	}
	public ResourceBundle getHtmlBundle() {
		if (htmlBundle == null) {
			log.debug("Creating new bundle on locale: " + getLocale());
			htmlBundle = ResourceBundle
					.getBundle(HTML_PROPERTIES_FILE_NAME, getLocale());
		}
		return htmlBundle;
	}
	/**
	 * 
	 */
	public String getMessage(String key) {
		String rtnStr = key;
		String htmlMsg = getMessageHtml(key);
		if(htmlMsg != null){
			return htmlMsg;
		}
		
		try {
			rtnStr = getBundle().getString(key);
		} catch (MissingResourceException e) {
			log.debug("Resource not fount: " + key);
//			log.error(e);
		}
//		log.debug("{0}->{1}", key, rtnStr);
		return rtnStr;
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getMessageHtml(String key) {
		HtmlResourcesEnum htmlEnum = null;
		try{
			htmlEnum = HtmlResourcesEnum.valueOf(key);
		}catch (IllegalArgumentException e) {
			return null;
		}
		String htmlPath = getHtmlBundle().getString(htmlEnum.name());
		
		InputStream in = getClass().getResourceAsStream(htmlPath);
		BufferedReader br;
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
		} catch (UnsupportedEncodingException e1) {
			log.error(e1);
		} catch (IOException e) {
			log.error(e);
		}
		
		return sb.toString();
	}

	
	public DecimalFormat getDecimalFormat() {
		return new DecimalFormat("###.###");
	}
	public DecimalFormat getPercentFormat() {
		return new DecimalFormat("# %");
	}

	public DecimalFormat getMillisecondFormat() {
		return new DecimalFormat("# ms");
	}

	protected void setLocale(Locale locale) {
			log.debug("Locale set: {0}", locale.toString());
		this.locale = locale;
		setBundle(null);
	}

	protected void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}


}
