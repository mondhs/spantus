package org.spantus.server.servlet.handeler;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.spantus.server.dto.SpntServletConfig;

public interface  ResourceHandler {
	public boolean isApplicable(String resource);
	public InputStream handle(HttpServletRequest request, String resource, SpntServletConfig config) throws IOException;
}
