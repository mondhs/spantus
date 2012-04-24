package org.spnt.applet.handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.spantus.logger.Logger;
import org.spnt.applet.SpntAppletEventListener;

public class PingHandler extends AbstractHandler{
	private static Logger LOG = Logger.getLogger(PingHandler.class);
	SpntAppletEventListener pollEventListener;

	public PingHandler(SpntAppletEventListener pollEventListener) {
		this.pollEventListener=pollEventListener;
	}
	
	public void pingURL(final URL recordUrl) {
		if (recordUrl == null)
			return;
		new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 5; i++) {
					HttpURLConnection c;
					try {
						c = (HttpURLConnection) recordUrl.openConnection();
						c.setConnectTimeout(5000);
						c.connect();
						if (c.getResponseCode() != 200) {
							LOG.error("[pingURL]WARNING: Ping failed for URL: "+ recordUrl);
							LOG.debug("[pingURL]WARNING: response code: {0}",c.getResponseCode());
							pollEventListener.setConnectionStatus(false);
							Thread.sleep(1000);
						} else {
							pollEventListener.setConnectionStatus(true);
							break;
						}
					} catch (IOException e) {
						LOG.error("[pingURL]WARNING: Ping failed for URL: "+ recordUrl);
						LOG.error(e);
						pollEventListener.setConnectionStatus(false);
						break;
					} catch (InterruptedException e) {
						LOG.error("[pingURL]WARNING: Ping failed for URL: "+ recordUrl);
						LOG.error(e);
					}
				}
			}
		}).start();
	}


}
