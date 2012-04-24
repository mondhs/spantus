package org.spnt.applet.handler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.spantus.logger.Logger;
import org.spnt.applet.SpntAppletEventListener;
import org.spnt.applet.ctx.SpantusAudioCtx;

public class PlayPollHandler extends AbstractHandler {
	private static Logger LOG = Logger.getLogger(PlayPollHandler.class);
	private SpantusAudioCtx ctx;
	private SpntAppletEventListener pollEventListener;
	private Pattern jsessionPatern = Pattern.compile("JSESSIONID=(.*);");

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

		Thread thread = new Thread() {
			@Override
			public void run() {
				int READ_TIMEOUT = 1000 * 60 * 5; // 5 minutes
				while (!ctx.getDestroyed()) {
					boolean repoll = pollForAudio(READ_TIMEOUT);
					if (!repoll) {
						pollEventListener.setConnectionStatus(false);
						return;
					}
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						LOG.error(e);
					}
				}
			}

		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 
	 * @param connectionTimeout
	 * @return
	 */
	boolean pollForAudio(int connectionTimeout) {
		try {
			HttpURLConnection c;
			c = (HttpURLConnection) ctx.getPlayUrl().openConnection();
			if (ctx.getSessionId() != null) {
				c.setRequestProperty("Cookie",
						"JSESSIONID=" + ctx.getSessionId());
			}
			c.setRequestProperty("LastTimestamp", ctx.getLastTimestamp()
					.toString());
			// Spend some time polling before timing out
			c.setReadTimeout(connectionTimeout);
			c.connect();
			ctx.setSessionId(extractSessionId(c, ctx.getSessionId()));
			ctx.setLastTimestamp(extractLastTimestamp(c, ctx.getLastTimestamp()));

			// LOG.debug("[pollForAudio]Polling for audio on: {0}",
			// ctx.getPlayUrl());

			if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Polling failed.");
			}

			// LOG.debug("[pollForAudio]Connected.");

			if ("audio/wav".equals(c.getContentType())) {
				InputStream stream = c.getInputStream();
				AudioInputStream ais;

				// assume the audio has header information in the stream
				// to tell us what it is
				try {
					// must be a bufferedinputstream b/c mark must be
					// supported to to read the header and determine the audio
					// format
					BufferedInputStream bstream = new BufferedInputStream(
							stream);
//					writeToFile(bstream, true);
					ais = AudioSystem.getAudioInputStream(bstream);
					 LOG.debug("[pollForAudio]Playing");
					 pollEventListener.play(ais);
					LOG.debug("[pollForAudio]Sleeping");
					return true;
				} catch (UnsupportedAudioFileException e) {
					LOG.error(e);
					return true;
				}
			} else {
				// LOG.debug("[pollForAudio]Connection was OK, but there was no audio, polling again.");
			}
		} catch (IOException e) {
			if (e instanceof SocketTimeoutException) {
				LOG.debug("[pollForAudio]Socket Timeout while polling for audio.");
			} else {
				pollEventListener.setConnectionStatus(false);
				LOG.debug(
						"[pollForAudio]WARNING: Failed to poll for audio on: {0}",
						ctx.getPlayUrl());
				LOG.error(e);
				return false;
			}
		}
		return ctx.getRepollOnTimeout();
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private Long extractLastTimestamp(HttpURLConnection c, Long lastTimestamp) {
		List<String> lastTimestampList = c.getHeaderFields().get(
				"LastTimestamp");
		if (lastTimestampList == null) {
			return lastTimestamp;
		}
		for (String lastTimestampI : lastTimestampList) {
			return Long.valueOf(lastTimestampI);
		}
		return lastTimestamp;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private String extractSessionId(HttpURLConnection c, String sessionId) {
		String anSessionid = null;
		List<String> cookies = c.getHeaderFields().get("Set-Cookie");
		if (cookies == null) {
			return sessionId;
		}
		for (String cookieIter : cookies) {
			Matcher anSessionidMatcher = jsessionPatern.matcher(cookieIter);
			if (anSessionidMatcher.find()) {
				MatchResult matchResult = anSessionidMatcher.toMatchResult();
				anSessionid = matchResult.group(1);
				break;
			}
		}
		return anSessionid;
	}


	/**
	 * Closes InputStream and/or OutputStream. It makes sure that both streams
	 * tried to be closed, even first throws an exception.
	 * 
	 * @throw IOException if stream (is not null and) cannot be closed.
	 * 
	 */
	protected static void close(InputStream iStream, OutputStream oStream)
			throws IOException {
		try {
			if (iStream != null) {
				iStream.close();
			}
		} finally {
			if (oStream != null) {
				oStream.close();
			}
		}
	}

}
