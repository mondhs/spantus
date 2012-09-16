/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.work.services.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;

import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;

/**
 * Audacity labels format support
 *
 * <br>format:<br> &lt;start time in sec&gt;\t&lt;end time in
 * sec&gt;\t&lt;label&gt;\n<br>
 *
 * @author Mindaugas Greibus
 * @since 0.0.1
 */
public class MarkerAudacityDao implements MarkerDao {

    Logger log = Logger.getLogger(MarkerAudacityDao.class);

    public MarkerSetHolder read(File file) {
        MarkerSetHolder markerSetHolder = null;
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(file);
            markerSetHolder = read(fstream);
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }
       
        return markerSetHolder;

    }
    /**
     * 
     * @param inputStream
     * @return 
     */
    public MarkerSetHolder read(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        MarkerSetHolder markerSetHolder = new MarkerSetHolder();
        MarkerSet markerSet = new MarkerSet();
        markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), markerSet);
        markerSetHolder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), markerSet);
        String strLine;
        try {
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                String[] entries = strLine.split("\t");
                if (entries.length == 3) {
                    Marker m = new Marker();
                    Double start = Double.valueOf(entries[0]) * 1E3;
                    Double end = Double.valueOf(entries[1]) * 1E3;
                    Double length = end - start;
                    m.setStart(start.longValue());
                    m.setLength(length.longValue());
                    m.setLabel(entries[2]);
                    markerSet.getMarkers().add(m);
                    log.debug("read " + m);
                }
            }
            br.close();
        } catch (IOException ex) {
            log.error(ex);
        }

        // Close the input stream
        return markerSetHolder;
    }

    public void write(MarkerSetHolder holder, File file) {
    }

    public void write(MarkerSetHolder holder, OutputStream outputStream) {
    }
}
