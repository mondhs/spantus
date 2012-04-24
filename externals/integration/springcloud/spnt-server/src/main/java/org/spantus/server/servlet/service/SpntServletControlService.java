package org.spantus.server.servlet.service;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.spantus.logger.Logger;
import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.wami.SpntRelay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.mit.csail.sls.wami.util.XmlUtils;

public class SpntServletControlService {
	private static Logger LOG = Logger.getLogger(SpntServletControlService.class);

	private static final String RELAY2 = "relay";

	public SpntRelay extractRelay(HttpServletRequest request,
			SpntServletConfig config) {
		SpntRelay relay = (SpntRelay) request.getSession().getAttribute(RELAY2);
		if (relay == null) {
			relay = newSpntRelay(request, config);
			request.getSession().setAttribute(RELAY2, relay);
		}
		return relay;
	}

	public SpntRelay newSpntRelay(HttpServletRequest request,
			SpntServletConfig config) {
		LOG.debug("**********************************************");
		LOG.debug("Initializing Relay " + config.getClientSessionId());
		LOG.debug("**********************************************");
		SpntRelay relay = new SpntRelay();
		relay.initialize(config);
		return relay;
	}
	/**
	 * Wait for a message to be sent from server to client via sendMessage().
	 * Will block indefinitely if pollTimeout=0 (set via config.xml), or it will
	 * wait only as long as pollTimeout() and return null if nothing found
	 */
	public String waitForMessage(SpntRelay relay, SpntServletConfig config) throws InterruptedException {
		try {
			relay.setCurrentlyPolling(true);
			LOG.debug("Waiting for message: {0}", config.getPollTimeout());
			return (config.getPollTimeout() > 0) ? relay.getMessageQueue().poll(config.getPollTimeout(),
					TimeUnit.MILLISECONDS) : relay.getMessageQueue().take();
		} finally {
			relay.setCurrentlyPolling(false);
			relay.setTimeLastMessageSent(System.currentTimeMillis());
		}
	}

	
	public void stopPolling(SpntRelay relay) {
		sendMessage(relay, "<reply type='stop_polling' />");
	}
	
	public void sendMessage(SpntRelay relay, String message) {
		try {
			LOG.debug("[sendMessage]Sending message: {0}" + message);
			long timestampMillis = System.currentTimeMillis();
			relay.setTimeLastMessageSent(timestampMillis) ;
			relay.getMessageQueue().put(message);
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}
	/**
	 * 
	 * @param clientUpdateXML
	 * @param relay
	 */
	public void handleClientUpdate(String clientUpdateXML, SpntRelay relay) {
		Document doc = XmlUtils.toXMLDocument(clientUpdateXML);
		Element root = (Element) doc.getFirstChild();
		logClientEvent(clientUpdateXML, root);

		String type = root.getAttribute("type");
		LOG.debug("[handleClientUpdate] do nothing with: {0}", type);
		
//		if ("hotswap".equals(type)) {
//			hotswapComponent(WamiConfig.getConfiguration(session
//					.getServletContext()), root);
//		} else if (wamiApp != null) {
//			wamiApp.onClientMessage(root);
//		} else {
//			System.err
//					.println("warming: handleClientUpdate called with null wami app");
//		}		
	}
	/**
	 * 
	 * @param clientUpdateXML
	 * @param root
	 */
	private void logClientEvent(String clientUpdateXML, Element root) {
			String type = root.getAttribute("type");
			if ("logevents".equals(type)) {
				NodeList eventNodes = root.getElementsByTagName("event");
				LOG.debug("unpacking events");
				for (int i = 0; i < eventNodes.getLength(); i++) {
					Element event = (Element) eventNodes.item(i);
					long timeInMillis = System.currentTimeMillis();
					LOG.debug("[logClientEvent] {0} Element: {1}", timeInMillis, event); 
				}
			} else {
				LOG.debug("logging client event");
				long timestampMillis = System.currentTimeMillis();
				LOG.debug("[logClientEvent] {0} Element: {1}", timestampMillis, clientUpdateXML); 
			}
	}

}
