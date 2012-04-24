package org.spantus.server.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "CorporaEntyList")
public class CorporaEntryList implements Iterable<CorporaEntry>{
	@XmlElement(name = "CorporaEntry")
	private List<CorporaEntry> corporaEntry = new ArrayList<CorporaEntry>();

	public CorporaEntryList() {
	}
	
	@XmlTransient
	public List<CorporaEntry> getCorporaEntry() {
		return corporaEntry;
	}

	public void setCorporaEntry(List<CorporaEntry> corporaEntry) {
		this.corporaEntry = corporaEntry;
	}

	@Override
	public Iterator<CorporaEntry> iterator() {
		return corporaEntry.iterator();
	}

}
