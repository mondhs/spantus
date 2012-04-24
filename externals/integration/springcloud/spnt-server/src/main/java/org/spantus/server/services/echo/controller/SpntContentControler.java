package org.spantus.server.services.echo.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.handeler.AppletResourceHandler;
import org.spantus.server.servlet.handeler.JsResourceHandler;
import org.spantus.server.servlet.handeler.ResourceHandler;
import org.spantus.server.servlet.handeler.WamiXmlResourceHandler;
import org.spantus.server.servlet.service.SpntServletConfigService;
import org.spantus.server.servlet.service.SpntServletControlService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.mit.csail.sls.wami.util.ServletUtils;

@Controller
@RequestMapping(method = RequestMethod.GET, value = "/content")
public class SpntContentControler {

	private SpntServletConfigService configService;
	private SpntServletControlService controlService;

	@RequestMapping(method = RequestMethod.GET, value = "/applet/"
			+ SpntServletConfig.APPLET_JAR_NAME)
	public void fetchApplet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ResourceHandler handler = new AppletResourceHandler();
		SpntServletConfig config = getConfigService().extractConfig(request);
		InputStream stream = handler.handle(request, null, config);
		ServletUtils.sendStream(stream, response.getOutputStream());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/wami.xml")
	public void fetchConfig(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ResourceHandler handler = new WamiXmlResourceHandler();
		SpntServletConfig config = getConfigService().extractConfig(request);
		InputStream stream = handler.handle(request, null, config);
		ServletUtils.sendStream(stream, response.getOutputStream());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/js/spantus.js")
	public void fetchJs(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		ResourceHandler handler = new JsResourceHandler();
		SpntServletConfig config = getConfigService().extractConfig(request);
		InputStream stream = handler.handle(request, null, config);
		ServletUtils.sendStream(stream, response.getOutputStream());
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
