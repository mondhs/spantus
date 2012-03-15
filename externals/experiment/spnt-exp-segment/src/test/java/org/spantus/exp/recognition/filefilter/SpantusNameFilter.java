package org.spantus.exp.recognition.filefilter;

import java.io.File;
import java.io.FilenameFilter;

public class SpantusNameFilter implements FilenameFilter {

	private String ext = ".mspnt.xml";
	
    public boolean accept(File file, String fileName) {
        return fileName.endsWith(ext);
    }

	public String getExt() {
		return ext;
	}
}