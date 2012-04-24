package org.spantus.server.wami;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.wami.dto.AudioElement;


public class SpntRelay {
	private static Logger LOG = LoggerFactory.getLogger(SpntRelay.class);
	
	BlockingQueue<AudioElement> audioQueue = new LinkedBlockingQueue<AudioElement>();
	public boolean playedAudio = false;
	private boolean isCurrentlyPolling;
	private BlockingQueue<String> messageQueue= new LinkedBlockingQueue<String>();
	private long timeLastMessageSent = System.currentTimeMillis();

	/**
	 * Returns an input stream for audio. The stream should have
	 * header-information already encoded in it.
	 */
	public InputStream waitForAudio(int timeInSeconds)
			throws InterruptedException {
		if (playedAudio) {
			// Not the first time waiting...
			// this means that we just finished playing.
			onFinishedPlayingAudio();
			playedAudio = false;
		}

//		LOG.debug("Waiting for audio for {0} seconds.", timeInSeconds);
		AudioElement e = audioQueue.poll(timeInSeconds, TimeUnit.SECONDS);
		InputStream ais = null;
		if (e != null) {
			ais = e.stream;
			playedAudio = true;
		}
		return ais;
	}

	/**
	 * 
	 */
	protected void onFinishedPlayingAudio() {

	}

	public void initialize(SpntServletConfig config) {

	}

	public void initialize(HttpServletRequest request, String wsessionid) {

		// session = request.getSession();
		// this.sc = request.getSession().getServletContext();
		//
		// wc = WamiConfig.getConfiguration(session.getServletContext());
		// pollTimeout = wc.getPollTimeout();

		// eventLogger = createEventLogger(request);

		// logEvent(new RequestHeadersLogEvent(request), System
		// .currentTimeMillis());

		// synthesizer = wc.createSynthesizer(this);
		// try {
		// recognizer = wc.createRecognizer(request.getSession()
		// .getServletContext(), this);
		// } catch (RecognizerException e) {
		// System.err.println("There was an error creating the recognizer");
		// InitializationException ie = new InitializationException(e);
		// ie.setRelay(this);
		// throw ie;
		// }

		// audioRetriever = wc.createAudioRetriever(sc);

		// If you specified a log player, you might want to start it in the app.
		// logplayer = wc.createLogPlayer(request);

		// This will be null if there is no application set in the config file.
		// wamiApp = wc.createWamiApplication(this, session);

	}

	/**
	 * 
	 * @param ais
	 */
	public void play(InputStream audio) {
		if (audio == null) {
			LOG.debug("WARNING: Attempted to play NULL audio");
			return;
		}
		try {
			audioQueue.put(new AudioElement(audio));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isPlayedAudio() {
		return playedAudio;
	}



	public void stopPolling() {
		this.sendMessage("<reply type='stop_polling' />");
	}

	public void sendMessage(String message) {
		try {
			System.out.println("Sending message: " + message);
			long timestampMillis = System.currentTimeMillis();
			setTimeLastMessageSent(timestampMillis) ;
			messageQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public long getTimeLastMessageSent() {
		return timeLastMessageSent;
	}

	public void setTimeLastMessageSent(long timeLastMessageSent) {
		this.timeLastMessageSent = timeLastMessageSent;
	}

	public BlockingQueue<String> getMessageQueue() {
		return messageQueue;
	}

	public boolean isCurrentlyPolling() {
		return isCurrentlyPolling;
	}

	public void setCurrentlyPolling(boolean isCurrentlyPolling) {
		this.isCurrentlyPolling = isCurrentlyPolling;
	}
}
