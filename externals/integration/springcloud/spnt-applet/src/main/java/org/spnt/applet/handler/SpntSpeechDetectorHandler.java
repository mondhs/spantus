package org.spnt.applet.handler;

import javax.swing.SwingUtilities;

import org.spnt.applet.SpntAppletEventListener;
import org.spnt.applet.SpntSpeechDetectorListener;
import org.spnt.applet.ctx.SpantusAudioCtx;

import edu.mit.csail.sls.wami.applet.sound.AutocorrSpeechDetector;
import edu.mit.csail.sls.wami.applet.sound.SpeechDetector;
/**
 * 
 * @author mgreibus
 *
 */
public class SpntSpeechDetectorHandler implements SpntSpeechDetectorListener {

	private SpantusAudioCtx ctx;
	private SpntAppletEventListener appletEventListener;
	private RecordHandler recordHandler;
	private SpeechDetector detector = new AutocorrSpeechDetector();

	public SpntSpeechDetectorHandler(SpantusAudioCtx ctx,
			SpntAppletEventListener appletEventListener) {
		this.ctx = ctx;
		this.appletEventListener = appletEventListener;
		this.recordHandler = new RecordHandler(detector, ctx, appletEventListener);
		detector.addListener(this);
	}
	/**
	 * Samples are ready for capture
	 */
	public void speechStart(long offsetSample) {
		ctx.setIsRecording(true);
		recordHandler.recordAudio();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				appletEventListener.showStatus("Speech start");
			}
		});
	}

	/**
	 * End of samples to be captured
	 */
	public void speechEnd(long offsetSample) {
		appletEventListener.finish();
	}

	/**
	 * Speech detection is not sensing speech
	 */
	public void noSpeech(long offsetSample) {
	}
	/**
	 * @return the detector
	 */
	public SpeechDetector getDetector() {
		return detector;
	}
	/**
	 * @param detector the detector to set
	 */
	public void setDetector(SpeechDetector detector) {
		this.detector = detector;
	}

}
