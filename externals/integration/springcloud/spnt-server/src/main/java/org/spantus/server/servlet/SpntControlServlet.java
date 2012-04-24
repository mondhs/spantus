package org.spantus.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.spantus.logger.Logger;
import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.service.SpntServletConfigService;
import org.spantus.server.servlet.service.SpntServletControlService;
import org.spantus.server.wami.SpntRelay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.mit.csail.sls.wami.util.XmlUtils;

public class SpntControlServlet extends HttpServlet {

	private static Logger LOG = Logger.getLogger(SpntControlServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 8156775273374081354L;

	SpntServletControlService controlService;
	SpntServletConfigService configService;

	/**
	 * Get a message from the control servlet (via polling)
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		SpntServletConfig config = getConfigService().extractConfig(request);

		boolean polling = false;

		SpntRelay relay = getControlService().extractRelay(request, config);

		if (relay == null) {
			showError(request, response, "null_relay",
					"The relay to the recognizer was not found");
			return;
		}

		// if no xml is passed in, then we try the request parameters
		String pollingStr = request.getParameter("polling");
		polling = pollingStr != null && !pollingStr.equals("")
				&& Boolean.parseBoolean(pollingStr);

		String m = null;

		try {
			if (polling) {
				// polling happens here:
				m = getControlService().waitForMessage(relay, config);

				if (m != null) {
					printResponse(m, request, response);
				} else {
					response.getWriter().close();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			Document xmlDoc;
			// SpntServletConfig config =
			// getConfigService().extractConfig(request);
			LOG.debug(
					"[doPost] request: {0}",
					getConfigService().reconstructRequestURLandParams(
							request.getRequestURL().toString(),
							request.getParameterMap()));

			// InputStream stream;
			// stream = request.getInputStream();
			String encding = request.getCharacterEncoding();
			encding = encding == null ? "UTF-8" : encding;
			InputSource source = new InputSource(new java.io.StringReader(request.getParameter("spntMessage")));
//					new InputSource(new InputStreamReader(stream,encding));

			xmlDoc = XmlUtils.getBuilder().parse(source);
			Element root = (Element) xmlDoc.getFirstChild();

			if (root == null) {
				return;
			}

			clientUpdateMessage(request, response, root);
		} catch (SAXException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param root
	 * @throws IOException
	 */
	protected void clientUpdateMessage(HttpServletRequest request,
			HttpServletResponse response, Element root) throws IOException {
		SpntServletConfig config = getConfigService().extractConfig(request);
		SpntRelay relay = getControlService().extractRelay(request, config);

		if (relay == null) {
			// Can't really send an error when we reply to a post for x-site
			printResponse("<empty />", request, response);
			return;
		}

		String stoppollingStr = root.getAttribute("stoppolling");
		boolean stopPolling = stoppollingStr != null
				&& !stoppollingStr.equals("")
				&& Boolean.parseBoolean(stoppollingStr);

		if (stopPolling) {
			getControlService().stopPolling(relay);
		} else {
			String clientUpdateXML = XmlUtils.toXMLString(root);
			LOG.debug("[clientUpdateMessage]clientUpdateXML: {0}",
					clientUpdateXML);
			getControlService().handleClientUpdate(clientUpdateXML, relay);
			printResponse("<empty />", request, response);
		}
	}

	/**
	 * 
	 * @return
	 */
	public SpntServletControlService getControlService() {
		if (controlService == null) {
			controlService = new SpntServletControlService();
		}
		return controlService;
	}

	/**
	 * 
	 * @return
	 */
	public SpntServletConfigService getConfigService() {
		if (configService == null) {
			configService = new SpntServletConfigService();
		}
		return configService;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @param error
	 */
	private void showError(HttpServletRequest request,
			HttpServletResponse response, String type, String error) {
		try {
			printResponse("<reply type='error' error_type='" + type
					+ "' message='" + error + "' />", request, response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the response returned by the relay. By default, it will be encoded
	 * as straight xml, however with ?rtype=js it can be returned as javascript
	 * wrapping the xml
	 */
	private void printResponse(String message, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		// note, you must set content type before getting the writer
		response.setContentType("text/xml; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(message);
		out.close();
	}
}
