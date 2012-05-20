package org.spantus.work.services.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;

public class MarkerProxyDao implements MarkerDao {

    public static final String LABA = "laba";
    public static final String MSPNTXML = "mspnt.xml";
    public static final String TEXT_GRID = "TextGrid";
    public static final String TXT = "txt";
    Map<String, MarkerDao> markerDaoRegister;
    private Logger log = Logger.getLogger(MarkerProxyDao.class);

    public MarkerProxyDao() {
        markerDaoRegister = new HashMap<String, MarkerDao>();
        markerDaoRegister.put(MSPNTXML, new MarkerXmlDaoImpl());
        markerDaoRegister.put(TXT, new MarkerAudacityDao());
        markerDaoRegister.put(LABA, new MarkerLabaDao());
        markerDaoRegister.put(TEXT_GRID, new MarkerTextGridDao());

    }

    public MarkerSetHolder read(File file) {
        MarkerDao markerDao = resolveMarkerDao(file.getName());
        if (markerDao != null) {
            return markerDao.read(file);
        }
        return null;
    }

    public MarkerSetHolder read(InputStream inputStream) {
        MarkerDao markerDao = resolveMarkerDao(MSPNTXML);
        if (markerDao != null) {
            return markerDao.read(inputStream);
        }
        return null;
    }

    public void write(MarkerSetHolder holder, File file) {
        MarkerDao markerDao = resolveMarkerDao(file.getName());
        if (markerDao != null) {
            markerDao.write(holder, file);
        } else {
            log.error("dao not saved for file: " + file);
            throw new ProcessingException("dao not saved for file: " + file);
        }
    }

    public void write(MarkerSetHolder holder, OutputStream outputStream) {
        MarkerDao markerDao = resolveMarkerDao("MSPNTXML");
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
