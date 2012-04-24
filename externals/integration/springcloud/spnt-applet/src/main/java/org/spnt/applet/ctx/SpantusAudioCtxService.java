package org.spnt.applet.ctx;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import javax.sound.sampled.AudioFormat;
import javax.swing.JApplet;

public class SpantusAudioCtxService {

	public static SpantusAudioCtxService getInstance() {
		return new SpantusAudioCtxService();
	}

	/**
	 * 
	 * @param applet
	 * @return
	 */
	public SpantusAudioCtx createSpantusAudioParams(JApplet applet) {
		SpantusAudioCtx anAudioParams = new SpantusAudioCtx();
		anAudioParams.setUseSpeechDetector(getBooleanParameter(applet,
				SpantusAudioCtx.USE_SPEECH_DETECTOR, true));
		anAudioParams.setAllowStopPlaying(getBooleanParameter(applet,
				SpantusAudioCtx.ALLOW_STOP_PLAYING, false));
		anAudioParams.setHideButton(getBooleanParameter(applet,
				SpantusAudioCtx.HIDE_BUTTON, false));
		anAudioParams.setRecordUrl(urlParameter(applet,
				SpantusAudioCtx.RECORD_URL));
		anAudioParams
				.setPlayUrl(urlParameter(applet, SpantusAudioCtx.PLAY_URL));
		String audioFormatStr = applet
				.getParameter(SpantusAudioCtx.RECORD_AUDIO_FORMAT);
		String sampleRateStr = applet
				.getParameter(SpantusAudioCtx.RECORD_SAMPLE_RATE);
		String isLittleEndianStr = applet
				.getParameter(SpantusAudioCtx.RECORD_IS_LITTLE_ENDIAN);
		anAudioParams.setRecordFormat(getAudioFormatFromParams(applet,
				audioFormatStr, sampleRateStr, isLittleEndianStr));
		anAudioParams.setPlayRecordTone(getBooleanParameter(applet,
				"playRecordTone", false));
		anAudioParams.setLastTimestamp(0L);
		anAudioParams.setLocale(extraceLocale(applet, Locale.getDefault()));
		return anAudioParams;
	}

	private Locale extraceLocale(JApplet applet, Locale aDefaultLocale) {
		String localeCode = applet.getParameter(SpantusAudioCtx.LOCALE_CODE);
		if (localeCode == null) {
			return aDefaultLocale;
		}
		Locale locale = new Locale(localeCode);
		return locale;
	}

	/**
	 * 
	 * @param applet
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	private boolean getBooleanParameter(JApplet applet, String paramName,
			boolean defaultValue) {
		String value = applet.getParameter(paramName);
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}

	/**
	 * 
	 * @param applet
	 * @param paramName
	 * @return
	 */
	private URL urlParameter(JApplet applet, String paramName) {
		String urlString = applet.getParameter(paramName);
		if (urlString != null && !"".equals(urlString)
				&& !"null".equals(urlString)) {
			try {
				URI uri = new URI(urlString);
				return uri.toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.err.println("Invalid url: " + urlString);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param applet
	 * @param audioFormatStr
	 * @param sampleRateStr
	 * @param isLittleEndianStr
	 * @return
	 */
	private AudioFormat getAudioFormatFromParams(JApplet applet,
			String audioFormatStr, String sampleRateStr,
			String isLittleEndianStr) {

		audioFormatStr = audioFormatStr == null ? SpantusAudioCtx.RECORD_AUDIO_FORMAT_DEFAULT
				: audioFormatStr;
		sampleRateStr = sampleRateStr == null ? SpantusAudioCtx.RECORD_SAMPLE_RATE_DEFAULT
				: sampleRateStr;
		isLittleEndianStr = isLittleEndianStr == null ? SpantusAudioCtx.RECORD_IS_LITTLE_ENDIAN_DEFAULT
				: isLittleEndianStr;

		int sampleRate = Integer.parseInt(sampleRateStr);
		boolean isLittleEndian = Boolean.parseBoolean(isLittleEndianStr);

		if ("MULAW".equals(audioFormatStr)) {
			return new AudioFormat(AudioFormat.Encoding.ULAW, sampleRate, 8, 1,
					2, 8000, !isLittleEndian);
		} else if ("LIN16".equals(audioFormatStr)) {
			return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate,
					16, 1, 2, sampleRate, !isLittleEndian);
		}

		throw new UnsupportedOperationException("Unsupported audio format: '"
				+ audioFormatStr + "'");
	}
}
