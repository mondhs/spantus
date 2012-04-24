package org.spnt.applet.handler;


import javax.sound.sampled.AudioInputStream;

import org.spantus.logger.Logger;
import org.spnt.applet.SpntAppletEventListener;
import org.spnt.applet.ctx.SpantusAudioCtx;

import edu.mit.csail.sls.wami.applet.sound.SpeechDetector;

public class RecordHandler extends AbstractHandler{
	private static Logger LOG = Logger.getLogger(RecordHandler.class);
	private SpeechDetector detector;
	SpantusAudioCtx ctx;
	SpntAppletEventListener listener;
	
	public RecordHandler(SpeechDetector detector, SpantusAudioCtx ctx,
			SpntAppletEventListener listener) {
		super();
		this.detector = detector;
		this.ctx = ctx;
		this.listener = listener;
	}
	/**
	 * records audio by sending it to the server, until the stream is closed
	 */
	public void recordAudio() {
		LOG.debug("[recordAudio]");
		// must do this immediately in the same thread
		final AudioInputStream in = detector.createReader(0);
//		new Thread(new AudioStreamWriterRunnable(ctx, in, listener)).start();
		new Thread(new BuferedAudioWriterRunnable(ctx, in, listener)).start();

	}

}
