package org.spantus.android.handler;

import org.spantus.android.SpntAppletEventListener;
import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.logger.Logger;

public class PlayPollHandler extends AbstractHandler {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(PlayPollHandler.class);
	private SpantusAudioCtx ctx;
	private SpntAppletEventListener pollEventListener;

	
//	AudioManager audioManager;

	/**
	 * 
	 * @param ctx
	 * @param pollEventListener
	 */
	public PlayPollHandler(SpantusAudioCtx ctx,
			SpntAppletEventListener pollEventListener) {
		this.ctx = ctx;
		this.pollEventListener = pollEventListener;
	}

	/**
	 * Polls a url for audio, plays it when it is returned
	 * 
	 * @param playUrl
	 *            The url to poll
	 */
	public void startPollingForAudio() {
		if (ctx.getPlayUrl() == null)
			return;
		
		PollTask audioOut = new PollTask(ctx,pollEventListener);
		audioOut.execute();
	}


}
