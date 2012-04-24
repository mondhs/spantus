package org.spantus.server.servlet.service;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.spantus.server.dto.SpntServletConfig;

import edu.mit.csail.sls.wami.util.ServletUtils;

public class SpntServletConfigService {
	/**
	 * 
	 * @return
	 */
	public SpntServletConfig newSpntServletConfig(HttpServletRequest request) {
		SpntServletConfig config = new SpntServletConfig();
		String wsessionid = createClientSessionID(request);
		config.setClientSessionId(wsessionid);
		String requestUrl = request.getRequestURL().toString();
		String requestUri = request.getRequestURI().toString();
		String baseUrl = requestUrl.replace(requestUri, "");
		baseUrl = baseUrl + request.getContextPath();
		config.setBaseURL(baseUrl);
		config.setRecordServletURL(baseUrl + "/api/record");
		config.setAudioServletURL(baseUrl + "/api/play?poll=true");
		config.setControlServletURL(baseUrl + "/api/control");
		config.setPollTimeout(10);
		config.setLocaleCode("lt_LT");
		return config;
	}

	/**
	 * 
	 * @param session
	 * @return
	 */
	public SpntServletConfig extractConfig(HttpServletRequest request) {
		SpntServletConfig config = (SpntServletConfig) request.getSession()
				.getAttribute("SpntServletConfig");
		if (config == null) {
			config = newSpntServletConfig(request);
			request.getSession().setAttribute("SpntServletConfig", config);
		}
		return config;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public String createClientSessionID(HttpServletRequest request) {
		return ServletUtils.getClientAddress(request) + ":"
				+ System.currentTimeMillis();
	}

	public String reconstructRequestURLandParams(String url,
			Map<String, String[]> requestParams) {
		// String url = request.getRequestURL().toString();
		String params = "";

		for (Entry<String, String[]> entry : requestParams.entrySet()) {
			String paramName = entry.getKey();
			String paramValue = entry.getValue()[0];
			params += "&" + paramName + "=" + paramValue;
		}

		if (!"".equals(params)) {
			params = params.replaceFirst("&", "?");
			url += params;
		}

		return url;
	}

	/**
	 * 
	 * @param config
	 * @return
	 */
	public String constructRecordServletURL(SpntServletConfig config) {
		return config.getRecordServletURL();
	}

	public String constructAudioServletURL(SpntServletConfig config) {
		return config.getAudioServletURL();
	}

	public String constructControlServlet(SpntServletConfig config) {
		return config.getControlServletURL();
	}

}
