package org.spantus.work.services.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.work.services.MarkerDao;

public class MarkerProxyDao implements MarkerDao {

	Map<String, MarkerDao> markerDaoRegister;
	private Logger log = Logger.getLogger(MarkerProxyDao.class);

	public MarkerProxyDao() {
		markerDaoRegister = new HashMap<String, MarkerDao>();
		markerDaoRegister.put("spnt.xml", new MarkerXmlDaoImpl());
		markerDaoRegister.put("txt", new MarkerAudacityDao());
		markerDaoRegister.put("laba", new MarkerLabaDao());
		markerDaoRegister.put("TextGrid", new MarkerTextGridDao());
		
	}

	public MarkerSetHolder read(File file) {
		MarkerDao markerDao = resolveMarkerDao(file.getName());
		if (markerDao != null) {
			return markerDao.read(file);
		}
		return null;
	}

	public MarkerSetHolder read(InputStream inputStream) {
		MarkerDao markerDao = resolveMarkerDao(".spnt.xml");
		if (markerDao != null) {
			return markerDao.read(inputStream);
		}
		return null;
	}

	public void write(MarkerSetHolder holder, File file) {
		MarkerDao markerDao = resolveMarkerDao(file.getName());
		if (markerDao != null) {
			markerDao.write(holder, file);
		}else{
			log.error("dao not saved for file: " + file);
			throw new ProcessingException("dao not saved for file: " + file);
		}
	}

	public void write(MarkerSetHolder holder, OutputStream outputStream) {
		MarkerDao markerDao = resolveMarkerDao(".spnt.xml");
		if (markerDao != null) {
			markerDao.write(holder, outputStream);
		}

	}

	protected MarkerDao resolveMarkerDao(String name) {
		for (Entry<String, MarkerDao> markerDao : markerDaoRegister.entrySet()) {
			if (name.endsWith(markerDao.getKey())) {
				return markerDao.getValue();
			}
		}
		return null;
	}
}
