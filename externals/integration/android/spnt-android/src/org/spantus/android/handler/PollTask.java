package org.spantus.android.handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spantus.android.SpntAppletEventListener;
import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.logger.Logger;

import android.os.AsyncTask;

public class PollTask extends AsyncTask<Void, Void, Void> {

	private static Logger LOG = Logger.getLogger(PollTask.class);
	private SpantusAudioCtx ctx;
	private SpntAppletEventListener pollEventListener;
	private Pattern jsessionPatern = Pattern.compile("JSESSIONID=(.*);");

	public PollTask(SpantusAudioCtx ctx,
			SpntAppletEventListener pollEventListener) {
		super();
		this.ctx = ctx;
		this.pollEventListener = pollEventListener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		int READ_TIMEOUT = 1000 * 60 * 5; // 5 minutes
		while (!ctx.getDestroyed()) {
			boolean repoll = pollForAudio(READ_TIMEOUT);
			if (!repoll) {
				pollEventListener.setConnectionStatus(false);
				return null;
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}

		return null;
	}

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
				DataInputStream inputStream = new DataInputStream(stream);
				pollEventListener.play(inputStream);
			} else {
				//
				LOG.debug("[pollForAudio]Connection was OK, but there was no audio, polling again.");
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

}
