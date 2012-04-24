package org.spantus.server.servlet.handeler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.service.SpntServletHtmlService;

public class JsResourceHandler implements ResourceHandler {
	// private Pattern conentPatern = Pattern.compile("\\/content\\/(\\w*.js)");
	private SpntServletHtmlService spntServletHtmlService;

	@Override
	public boolean isApplicable(String resource) {
		// Matcher anContentMatcher = conentPatern.matcher(resource);
		// return anContentMatcher.find();
		return resource.endsWith("spantus.js");
	}

	@Override
	public InputStream handle(HttpServletRequest request, String resource,
			SpntServletConfig config) throws IOException {
//		Matcher anContentMatcher = conentPatern.matcher(resource);
//		if (!anContentMatcher.find()) {
//			return null;
//		}
		StringBuilder result = new StringBuilder();;
//		MatchResult matchResult = anContentMatcher.toMatchResult();
//		String matched = matchResult.group(1);
		result.append(getSpntServletHtmlService().getUtils(config));

//		if (matched.endsWith(".js")) {
//			result += getSpntServletHtmlService().getUtils(config);
//		}
		result.append(getSpntServletHtmlService().getConfigurationJSON(config));
		result.append(getSpntServletHtmlService().getContentResourceAsString(
				"js/app.js"));
		return new ByteArrayInputStream(result.toString().getBytes());
	}

//	private String getConfigurationJSON(HttpServletRequest request,
//			SpntServletConfig config) throws IOException {
//
//		String result = "var _wamiParams = {\n";
//		result += "\t\"wsessionid\":\"" + config.getClientSessionId() + "\",\n";
//		result += "\t\"controlUrl\":\""
//				+ config.getControlServletURL()+ "\",\n";
//		result += "\t\"playUrl\":\"" +config.getAudioServletURL()
//				+ "\",\n";
//		result += "\t\"recordUrl\":\""
//				+ config.getRecordServletURL()) + "\",\n";
//		result += getAppletJSON(request, config);
//		result += "}\n\n";
//
//		// System.out.println("JSON FOR CONFIGURATION: \n" + result);
//		return result;
//	}

	public SpntServletHtmlService getSpntServletHtmlService() {
		if (spntServletHtmlService == null) {
			spntServletHtmlService = new SpntServletHtmlService();
		}
		return spntServletHtmlService;
	}

}
