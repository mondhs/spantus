package org.spantus.core.beans;

import java.text.DecimalFormat;
import java.util.Locale;

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
public interface I18n {

	public static final Locale LITHUANIAN = new Locale("lt", "LT");
	public static final Locale[] LOCALES = new Locale[]{LITHUANIAN, Locale.ENGLISH};

	public String getMessage(String key);
	public Locale getLocale();
	public DecimalFormat getDecimalFormat();
	public DecimalFormat getPercentFormat();
	public DecimalFormat getMillisecondFormat();
}
