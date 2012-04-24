package org.spnt.applet.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.spantus.logger.Logger;
import org.spnt.applet.SpntAppletEventListener;
import org.spnt.applet.SpntAudoListener;
import org.spnt.applet.ctx.SpantusAudioCtx;

public class SpntAudioListenerHandler implements SpntAudoListener {
	private static Logger LOG = Logger.getLogger(SpntAudioListenerHandler.class);
	private SpantusAudioCtx ctx;
	private SpntAppletEventListener appletEventListener;
	private Timer levelTimer;

	public SpntAudioListenerHandler(SpantusAudioCtx ctx,
			final SpntAppletEventListener anAppletEventListener) {
		this.ctx = ctx;
		this.appletEventListener = anAppletEventListener;
		// javax.swing.timer runs events on swing event thread, so this is safe
		levelTimer = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				appletEventListener.updateMeterChangedValue();
			}
		});
	}

	/**
	 * audio device is playing
	 */
	public void playingHasStarted() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LOG.debug("playingHasStarted");
				ctx.setIsPlaying(true);
				appletEventListener.showStatus("Playing has started");
			}
		});
	}

	/**
	 * audio device has finished playing
	 */
	public void playingHasEnded() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ctx.setIsPlaying(false);
				LOG.debug("playingHasEnded");
				appletEventListener.showStatus("Playing has ended");
			}
		});
	}

	/**
	 * Audio device is listening
	 */
	public void listeningHasStarted() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LOG.debug("listeningHasStarted");
				levelTimer.start();
				appletEventListener.showStatus("Listening has started");
			}
		});
	}


	/**
	 * Audio device has stopped listening
	 */
	public void listeningHasEnded() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LOG.debug("listeningHasEnded");
				ctx.setIsRecording(false);
				ctx.setIsListening(false);
				appletEventListener.showStatus("Listening has ended");
				levelTimer.stop();
				appletEventListener.resetMeterChangedValue();
			}
		});
	}

}
