package org.spnt.applet;

import java.util.Locale;
import java.util.ResourceBundle;

import org.spnt.applet.ctx.SpantusAudioCtx;

public class I18n {

	SpantusAudioCtx ctx;
	Locale currentLocale = null;
	ResourceBundle labels;

	public I18n(SpantusAudioCtx ctx) {
		this.ctx = ctx;
	}

	public String getMessage(String key) {
		if (currentLocale == null) {
			currentLocale = ctx.getLocale();
			labels = ResourceBundle.getBundle("labelsBundle", currentLocale);
		}
		if (!ctx.getLocale().equals(currentLocale)) {
			currentLocale = ctx.getLocale();
			labels = ResourceBundle.getBundle("labelsBundle", currentLocale);
		}
		String label = labels.getString(key);
		return label;
	}

	public String getTooltip(String key) {
		return getMessage(key);
	}
}
