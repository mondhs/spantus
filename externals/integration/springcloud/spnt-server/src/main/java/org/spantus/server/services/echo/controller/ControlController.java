package org.spantus.server.services.echo.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.service.SpntServletConfigService;
import org.spantus.server.servlet.service.SpntServletControlService;
import org.spantus.server.wami.SpntRelay;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ControlController {
	private static Logger LOG = LoggerFactory
			.getLogger(ControlController.class);
	private SpntServletControlService controlService;
	private SpntServletConfigService configService;

	@RequestMapping(method = RequestMethod.GET, value = "/control")
	public void getControl(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

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
			LOG.error(error);
			printResponse("<reply type='error' error_type='" + type
					+ "' message='" + error + "' />", request, response);
		} catch (IOException e) {
			e.printStackTrace();
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

}
