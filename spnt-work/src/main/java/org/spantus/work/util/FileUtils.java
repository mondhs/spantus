package org.spantus.work.util;

import java.io.File;

public abstract class FileUtils {
	
	public static String getOnlyFileName(File file){
		String fileName = file.getName();
		fileName = fileName.replaceAll("\\.\\w{2,4}$","");
		return fileName;
	}
	
	public static File checkDirs(String dirName){
		File dir = new File(dirName);
		if(!dir.exists()){
			dir.mkdirs();
		}
		return dir;
	}
	
	public static String getPath(File file){
		return file.getParentFile().getAbsolutePath()+"/";
	}

}
