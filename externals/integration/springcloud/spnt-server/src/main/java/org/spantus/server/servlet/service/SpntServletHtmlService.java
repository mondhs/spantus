package org.spantus.server.servlet.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.SpntAppletSupportContentServlet;

import edu.mit.csail.sls.wami.util.ServletUtils;

public class SpntServletHtmlService {
	/**
	 * 
	 * @param request
	 * @param wsessionid
	 * @return
	 * @throws IOException
	 */
	public String getJSAPI(SpntServletConfig config) throws IOException {
		String result = getUtils(config);

		result += getConfigurationJSON(config);
		result += getContentResourceAsString("js/app.js");

		return result;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public String getUtils(SpntServletConfig config) {
		String baseURL = config.getBaseURL();

		StringBuilder result = new StringBuilder();
		result.append(getContentResourceAsString("js/utils.js"));
		result.append("\n\n");
		result.append("Spantus.getBaseURL = function () { return '")
				.append(baseURL).append("'}");
		result.append("\n\n");
		return result.toString();
	}

	public StringBuilder getConfigurationJSON(SpntServletConfig config)
			throws IOException {
		StringBuilder result = new StringBuilder("var _spntParams = {\n");
		result.append("\t\"clientSessionId\":\"" + config.getClientSessionId());
		result.append("\",\n");
		result.append("\t\"controlUrl\":\"" + config.getControlServletURL()
				+ "\",\n");
		result.append("\t\"localeCode\":\"" + config.getLocaleCode()
				+ "\",\n");
		result.append("\t\"playUrl\":\"" + config.getAudioServletURL()
				+ "\",\n");
		result.append("\t\"recordUrl\":\"" + config.getRecordServletURL()
				+ "\",\n");
		result.append(getAppletJSON(config));
		result.append("}\n\n");

		// System.out.println("JSON FOR CONFIGURATION: \n" + result);
		return result;
	}

	private StringBuilder getAppletJSON(SpntServletConfig config) {
		// WamiConfig wc = WamiConfig.getConfiguration(getServletContext());
		Map<String, String> params = getAppletParams(config);
		StringBuilder result = new StringBuilder();
		config.setLocaleCode(params.get("localeCode"));
		String localeCode = config.getLocaleCode() == null?"lt_LT":config.getLocaleCode();
		// TODO: add back in playurl and recordurl for iphone
		result.append("\t\"applet\" : {\n");
		result.append("\t\t\"code\" : \"" + params.get("CODE") + "\",\n");
		result.append("\t\t\"archive\" : \"" + params.get("ARCHIVE") + "\",\n");
		result.append("\t\t\"name\" : \"" + params.get("NAME") + "\",\n");
		result.append("\t\t\"localeCode\" : \"" + localeCode+ "\",\n");
		result.append("\t\t\"width\" : \""
				+ Integer.toString(config.getAppletWidth()) + "\",\n");
		result.append("\t\t\"localeCode\" : \""
				+ Integer.toString(config.getAppletWidth()) + "\",\n");
		result.append("\t\t\"height\" : \""
				+ Integer.toString(config.getAppletHeight()) + "\",\n");
				result.append( "\t\t\"params\" : ");
				result.append( getParamsJSON(params));
				result.append("\t}\n");

		return result;
	}

	private String getParamsJSON(Map<String, String> params) {
		String result = "[\n";

		Object[] paramNames = params.keySet().toArray();

		for (int i = 0; i < paramNames.length; i++) {
			String name = (String) paramNames[i];
			String value = params.get(name);

			result += "\t\t\t{ \"name\" : \"" + name + "\" , \"value\" : \""
					+ value + "\" }";

			if (i < paramNames.length - 1) {
				result += ",\n";
			}
		}
		return result += "]\n";
	}

	public String getContentResourceAsString(String resource) {
		return "\n"
				+ ServletUtils
						.convertStreamToString(SpntAppletSupportContentServlet.class
								.getClassLoader().getResourceAsStream(
										"/content/" + resource)) + "\n";
	}

	public Map<String, String> getAppletParams(SpntServletConfig config) {
		Map<String, String> params = new HashMap<String, String>();

		params.put("CODE", config.getAudioAppletClass());
		params.put("ARCHIVE", config.getAppletArchives());
		params.put("NAME", "AudioApplet");

		params.put("type", "application/x-java-applet;version=1.6");
		params.put("scriptable", "true");
		params.put("mayscript", "true");
		params.put("location", config.getHubLocationString());
		params.put("vision", "false");
		params.put("layout", "stacked");
		params.put("httpOnly", "true");
		params.put("recordUrl", config.getRecordServletURL());
		params.put("recordAudioFormat", config.getRecordAudioFormat());
		params.put("recordSampleRate",
				Integer.toString(config.getRecordSampleRate()));
		params.put("recordIsLittleEndian",
				Boolean.toString(config.getRecordIsLittleEndian()));
		params.put("greenOnEnableInput",
				Boolean.toString(config.getGreenOnEnableInput()));
		params.put("allowStopPlaying",
				Boolean.toString(config.getAllowStopPlaying()));
		params.put("hideButton", Boolean.toString(config.getHideAudioButton()));
		params.put("playRecordTone",
				Boolean.toString(config.getPlayRecordTone()));
		params.put("useSpeechDetector",
				Boolean.toString(config.getUseSpeechDetector()));
		params.put("playUrl", config.getAudioServletURL());
		params.put("localeCode", config.getLocaleCode());
		return params;
	}

}
