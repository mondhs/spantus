package org.spantus.work.util;

import java.io.File;

public abstract class FileUtil {
	public static String getOnlyFileName(File file){
		String[] path = file.getAbsoluteFile().getPath().split("[\\\\]");
		if(path.length == 1){
			path = file.getAbsoluteFile().getPath().split("[/]");
		}
		return path[path.length-1].split("[.]")[0];
	}
	public static String getPath(File file){
		return file.getParentFile().getAbsolutePath()+"/";
	}

}
