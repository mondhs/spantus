package org.spantus.exp.recognition;

import java.io.File;
import java.io.FilenameFilter;

public class WavFileNameFilter implements FilenameFilter {

    public boolean accept(File file, String fileName) {
        return fileName.endsWith(".wav");
    }
}