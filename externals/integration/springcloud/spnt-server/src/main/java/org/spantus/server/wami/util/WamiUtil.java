package org.spantus.server.wami.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.spantus.logger.Logger;
import org.spantus.server.wami.SpntRelay;

public abstract class WamiUtil {
	private static final String RELAY2 = "relay";
	private static Logger LOG = Logger.getLogger(WamiUtil.class);
	/**
	 * The relay is lazily created for each session.
	 * 
	 * @param request
	 * @return
	 */
	public static SpntRelay getRelay(HttpServletRequest request) {
		String wsessionid = request.getParameter("wsessionid");
		return getRelay(request, wsessionid);
	}

	public static SpntRelay getRelay(HttpServletRequest request,
			String wsessionid) {
		if (wsessionid == null || "".equals(wsessionid)) {
			wsessionid = request.getSession().getId();
		}

		HttpSession session = request.getSession();

		SpntRelay relay = null;

		// SpntRelay manager = RelayManager.getManager(session);
		// synchronized (manager) {
		relay = (SpntRelay) request.getSession().getAttribute(RELAY2);
		if(relay == null){
			relay = initializeRelay(request, wsessionid);
			setRelay(request, relay, wsessionid);
		}

		return relay;
	}

	public static String setRelay(HttpServletRequest request, SpntRelay relay,
			String wsessionid){

		LOG.debug("Placing session WAMI session: {0}", wsessionid);
		 request.getSession().setAttribute(RELAY2, relay);
		
//		RelayManager manager = RelayManager.getManager(request.getSession());
//		manager.addRelay(relay, wsessionid);
		return wsessionid;
	}

	public static SpntRelay initializeRelay(HttpServletRequest request,
			String wsessionid) {
		LOG.debug("**********************************************");
		LOG.debug("Initializing Relay " + wsessionid);
		LOG.debug("**********************************************");
		HttpSession session = request.getSession();
		SpntRelay relay;

		relay = newRelay(session.getServletContext());
		wsessionid = setRelay(request, relay, wsessionid);

		relay.initialize(request, wsessionid);
		return relay;
	}
	/**
	 * 
	 * @param sc
	 * @return
	 * @throws InitializationException
	 */
	private static SpntRelay newRelay(ServletContext sc){
//		WamiConfig ac = WamiConfig.getConfiguration(sc);
//		String className = ac.getRelayClass();
		SpntRelay relay = new SpntRelay();

//		if (className == null) {
//			throw new InitializationException("No relay class name specified.");
//		}

//		try {
//			System.out.println("Creating new WamiRelay subclass: " + className);
//			relay = (WamiRelay) Class.forName(className).newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}

		return relay;
	}
}
