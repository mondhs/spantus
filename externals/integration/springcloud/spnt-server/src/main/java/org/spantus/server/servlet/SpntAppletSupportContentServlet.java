package org.spantus.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.handeler.AppletResourceHandler;
import org.spantus.server.servlet.handeler.JsResourceHandler;
import org.spantus.server.servlet.handeler.ResourceHandler;
import org.spantus.server.servlet.handeler.WamiXmlResourceHandler;
import org.spantus.server.servlet.service.SpntServletConfigService;
import org.spantus.server.servlet.service.SpntServletHtmlService;

import edu.mit.csail.sls.wami.util.Parameter;
import edu.mit.csail.sls.wami.util.ServletUtils;

//import edu.mit.csail.sls.wami.WamiConfig;
//import edu.mit.csail.sls.wami.util.Parameter;
//import edu.mit.csail.sls.wami.util.ServletUtils;
//import edu.mit.csail.sls.wami.util.XmlUtils;

/**
 * This is not standard, but hopefully it will make it easier to distribute
 * WAMI. This servlet proxies resources which would normally be found under
 * WebContent from locations that would be otherwise inaccessible via a URL.
 * 
 * The audio applet, for instance, is proxied from
 * /WEB-INF/lib/wami_audio_applet.jar. The javascript necessary for the JSAPI is
 * also accessible through this servlet.
 * 
 * @author imcgraw
 * 
 */
//WebServlet(asyncSupported = false, name = "contenct", urlPatterns = { "/content/*" }, initParams = {})
public class SpntAppletSupportContentServlet extends HttpServlet {
	SpntServletConfigService spntServletConfigServer;
	SpntServletHtmlService spntServletHtmlService;
	List<ResourceHandler> resouceHandlers;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3231351676966580310L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		SpntServletConfig config = getSpntServletConfigServer().extractConfig(
				request);
		response.setCharacterEncoding("UTF-8");

		response.setContentType("text/html; char-set:UTF-8");
		String requestURL = request.getRequestURL().toString();
		String resource = requestURL.substring(requestURL.indexOf(request
				.getContextPath()) + request.getContextPath().length());
		resource = resource.replace("//", "/"); // Replace any oddities

		try {
			if (Parameter.get(request, "debug", false)) {
				response.setContentType("text/html");
				response.getWriter().write(
						"The requested resource is: " + resource);
			} else {
				InputStream stream = getRequestedResource(request, resource,
						config);
				if (stream == null) {
					throw new RuntimeException(
							"Could not find requested resource: " + resource);
				}

				ServletUtils.sendStream(stream, response.getOutputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream getRequestedResource(HttpServletRequest request,
			String resource, SpntServletConfig config) throws IOException {
		InputStream inputStream = null;
		for (ResourceHandler handler : getResouceHandlers()) {
			if (handler.isApplicable(resource)) {
				inputStream = handler.handle(request, resource, config);
				break;
			}
		}
		return inputStream;
	}

	// public String handleWamiJSAPIRequest(HttpServletRequest request,
	// SpntServletConfig config) {
	// HttpSession session = request.getSession();
	//
	// Enumeration<String> names = request.getHeaderNames();
	// while (names.hasMoreElements()) {
	// String name = (String) names.nextElement();
	// String value = request.getHeader(name);
	// System.out.println("HEADER: " + name + " " + value);
	// }
	//
	// // no browser test
	// session.setAttribute("passedBrowserTest", new Boolean(true));
	//
	// String jsapi = "";
	//
	// String serverAddress = getSpntServletConfigServer()
	// .reconstructRequestURLandParams(
	// request.getRequestURL().toString(),
	// request.getParameterMap());
	// config.setServerAddress(serverAddress);
	//
	//
	// try {
	// jsapi += getSpntServletHtmlService().getJSAPI(config);
	// } catch (IOException e) {
	// jsapi += getAlert("Error setting up WAMI javascript.");
	// }
	//
	// return jsapi;
	// }

	// protected String getAlert(String message) {
	// return "alert('" + message + "');\n\n";
	// }

	public SpntServletConfigService getSpntServletConfigServer() {
		if (spntServletConfigServer == null) {
			spntServletConfigServer = new SpntServletConfigService();
		}
		return spntServletConfigServer;
	}

	public SpntServletHtmlService getSpntServletHtmlService() {
		if (spntServletHtmlService == null) {
			spntServletHtmlService = new SpntServletHtmlService();
		}
		return spntServletHtmlService;
	}

	public List<ResourceHandler> getResouceHandlers() {
		if (resouceHandlers == null) {
			resouceHandlers = new ArrayList<ResourceHandler>();
			resouceHandlers.add(new AppletResourceHandler());
			resouceHandlers.add(new WamiXmlResourceHandler());
			resouceHandlers.add(new JsResourceHandler());
		}
		return resouceHandlers;
	}

}
