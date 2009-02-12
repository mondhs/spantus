package org.spantus.demo.dto;

import java.net.URL;

public class SampleDto {
	String title;
	URL url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	public boolean isSamplePlayable() {
		return !getUrl().getFile().endsWith("xml");
	}
	public boolean isSampleReaderConfigurable() {
		return isSamplePlayable();
	}

}
