package org.spantus.work.util;

import java.io.File;
import java.io.FilenameFilter;

import org.spantus.logger.Logger;

public class FolderSignalDraw {
	
	Logger log = Logger.getLogger(getClass());
	String folder;
	
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public void process(){
		File folder = new File(getFolder());
		for (String file: folder.list(new WavFilter())) {
			log.debug(file);
			MultipleReaderSignalDraw multipleReader = new MultipleReaderSignalDraw();
			multipleReader.setPath(getFolder() + "/" + file);
			multipleReader.process();
		}
	}
	
	public static void main(String[] args) {
		FolderSignalDraw draw = new FolderSignalDraw();
		draw.setFolder(args[0]);
		draw.process();
	}
	class WavFilter implements FilenameFilter{
		
		public boolean accept(File dir, String name) {
			return (name.endsWith(".wav"));
		}
		
	}

}
