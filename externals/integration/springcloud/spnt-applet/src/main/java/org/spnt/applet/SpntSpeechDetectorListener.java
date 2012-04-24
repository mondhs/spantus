package org.spnt.applet;


/**
 * Generic speech detector API
 **/
public interface SpntSpeechDetectorListener {

	/**
	 * Indicates that speech has been detected somewhere after the specified
	 * position.
	 * 
	 * @param offsetSample
	 *            The padded position, where speech has been detected, in
	 *            samples
	 * 
	 **/
	void speechStart(long offsetSample);

	/**
	 * Indicates the end of detected speech.
	 * 
	 * @param offsetSample
	 *            The last byte of detected speech. Note that if the detector is
	 *            reenabled, this offset may be after the next speechStart
	 *            position.
	 * 
	 **/
	void speechEnd(long offsetSample);

	/**
	 * No speech was detected before the end of file marker was read by the
	 * detector.
	 * 
	 * @param offsetSample
	 *            The position of the end of file mark.
	 * 
	 **/
	void noSpeech(long offsetSample);
}
