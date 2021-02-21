package org.spantus.exp.sylcount.filefilter;

import java.io.File;
import java.io.FilenameFilter;

public class ExtNameFilter implements FilenameFilter {
	private String ext;

	public ExtNameFilter(String ext) {
		this.ext = ext;
	}
	
    public boolean accept(File file, String fileName) {
        return fileName.endsWith(ext);
    }
}