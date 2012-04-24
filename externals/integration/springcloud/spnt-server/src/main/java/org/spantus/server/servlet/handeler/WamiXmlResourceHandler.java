package org.spantus.server.servlet.handeler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.spantus.server.dto.SpntServletConfig;
import org.spantus.server.servlet.service.SpntServletConfigService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.mit.csail.sls.wami.util.XmlUtils;

public class WamiXmlResourceHandler implements ResourceHandler {

	private SpntServletConfigService spntServletConfigServer;

	@Override
	public boolean isApplicable(String resource) {
		return "/wami.xml".equals(resource)
				|| "/content/wami.xml".equals(resource);
	}

	@Override
	public InputStream handle(HttpServletRequest request, String resource,
			SpntServletConfig config) {
		return new ByteArrayInputStream(getUrls(request, config).getBytes());
	}

	private String getUrls(HttpServletRequest request, SpntServletConfig config) {
		// WamiConfig wc = WamiConfig.getConfiguration(request.getSession()
		// .getServletContext());

		Document doc = XmlUtils.newXMLDocument();
		Element root = doc.createElement("root");
		doc.appendChild(root);

		Element recordE = doc.createElement("record");
		recordE.setAttribute("url", getSpntServletConfigServer()
				.constructRecordServletURL(config));
		root.appendChild(recordE);

		Element playE = doc.createElement("play");
		playE.setAttribute("url", getSpntServletConfigServer()
				.constructAudioServletURL(config));
		root.appendChild(playE);

		Element controlE = doc.createElement("control");
		controlE.setAttribute("url", getSpntServletConfigServer()
				.constructControlServlet(config));
		root.appendChild(controlE);

		return XmlUtils.toXMLString(doc);
	}

	public SpntServletConfigService getSpntServletConfigServer() {
		if (spntServletConfigServer == null) {
			spntServletConfigServer = new SpntServletConfigService();
		}
		return spntServletConfigServer;
	}
}
