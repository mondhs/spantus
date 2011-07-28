package org.spantus.exp.recognition;

import java.io.File;
import java.io.FilenameFilter;

public class SpantusNameFilter implements FilenameFilter {

    public boolean accept(File file, String fileName) {
        return fileName.endsWith(".mspnt.xml");
    }
}