package org.spnt.applet.ctx;

import java.io.Serializable;
import java.net.URL;
import java.util.Locale;

import javax.sound.sampled.AudioFormat;

public class SpantusAudioCtx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 838495701360601130L;
	public static final String HIDE_BUTTON = "hideButton";
	public static final String ALLOW_STOP_PLAYING = "allowStopPlaying";
	public static final String USE_SPEECH_DETECTOR = "useSpeechDetector";
	public static final String RECORD_URL = "recordUrl";
	public static final String PLAY_URL = "playUrl";
	public static final String RECORD_IS_LITTLE_ENDIAN = "recordIsLittleEndian";
	public static final String RECORD_IS_LITTLE_ENDIAN_DEFAULT = "true";
	public static final String RECORD_SAMPLE_RATE = "recordSampleRate";
	public static final String RECORD_AUDIO_FORMAT = "recordAudioFormat";
	public static final String RECORD_SAMPLE_RATE_DEFAULT = "8000";
	public static final String RECORD_AUDIO_FORMAT_DEFAULT = "LIN16";
	public static final String LOCALE_CODE = "localeCode";

	private Boolean useSpeechDetector;
	private Boolean allowStopPlaying;
	private Boolean hideButton;
	private URL recordUrl;
	private URL playUrl;
	private AudioFormat recordFormat;
	private Boolean playRecordTone;
	private Boolean destroyed = false;
	private Boolean repollOnTimeout = true;
	private Boolean isPlaying = false;
	private Boolean isRecording = false;
	private Boolean isListening = false;
	private Boolean audioFailure = false;
	private String sessionId;
	private Long lastTimestamp;
	private Locale locale;

	public Boolean getUseSpeechDetector() {
		return useSpeechDetector;
	}

	public void setUseSpeechDetector(boolean useSpeechDetector) {
		this.useSpeechDetector = useSpeechDetector;
	}

	public Boolean getAllowStopPlaying() {
		return allowStopPlaying;
	}

	public void setAllowStopPlaying(boolean allowStopPlaying) {
		this.allowStopPlaying = allowStopPlaying;
	}

	public Boolean getHideButton() {
		return hideButton;
	}

	public void setHideButton(boolean hideButton) {
		this.hideButton = hideButton;
	}

	public URL getRecordUrl() {
		return recordUrl;
	}

	public void setRecordUrl(URL recordUrl) {
		this.recordUrl = recordUrl;
	}

	public URL getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(URL playUrl) {
		this.playUrl = playUrl;
	}

	public AudioFormat getRecordFormat() {
		return recordFormat;
	}

	public void setRecordFormat(AudioFormat recordFormat) {
		this.recordFormat = recordFormat;
	}

	public Boolean getPlayRecordTone() {
		return playRecordTone;
	}

	public void setPlayRecordTone(boolean playRecordTone) {
		this.playRecordTone = playRecordTone;
	}

	public Boolean getDestroyed() {
		return destroyed;
	}

	public void setDestroyed(Boolean destroyed) {
		this.destroyed = destroyed;
	}

	public Boolean getRepollOnTimeout() {
		return repollOnTimeout;
	}

	public void setRepollOnTimeout(Boolean repollOnTimeout) {
		this.repollOnTimeout = repollOnTimeout;
	}

	public Boolean getIsPlaying() {
		return isPlaying;
	}

	public void setIsPlaying(Boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public Boolean getIsRecording() {
		return isRecording;
	}

	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
	}

	public Boolean getIsListening() {
		return isListening;
	}

	public void setIsListening(Boolean isListening) {
		this.isListening = isListening;
	}

	public Boolean getAudioFailure() {
		return audioFailure;
	}

	public void setAudioFailure(Boolean audioFailure) {
		this.audioFailure = audioFailure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("SpantusAudioCtx [useSpeechDetector=%s,\n allowStopPlaying=%s,\n hideButton=%s,\n recordUrl=%s,\n playUrl=%s,\n recordFormat=%s,\n playRecordTone=%s,\n destroyed=%s,\n repollOnTimeout=%s,\n isPlaying=%s,\n isRecording=%s,\n isListening=%s,\n audioFailure=%s]",
						useSpeechDetector, allowStopPlaying, hideButton,
						recordUrl, playUrl, recordFormat, playRecordTone,
						destroyed, repollOnTimeout, isPlaying, isRecording,
						isListening, audioFailure);
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the lastTimestamp
	 */
	public Long getLastTimestamp() {
		return lastTimestamp;
	}

	/**
	 * @param lastTimestamp
	 *            the lastTimestamp to set
	 */
	public void setLastTimestamp(Long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
