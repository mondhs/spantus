package org.spantus.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FileUtils {
	
	public static String getOnlyFileName(File file){
		String fileName = file.getName();
		fileName = fileName.replaceAll("\\.\\w{2,4}$","");
		return fileName;
	}
	public static String getOnlyFileName(String file){
		file = file.replaceAll("\\.\\w{2,5}$","");
		return file;
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
	
	public static File findNextAvaibleFile(String fileName){
		File file = new File(fileName);
		File dir = new File(file.getParent());
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(file.exists()){
			Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
			Matcher matcher = pattern.matcher(fileName);
			if(matcher.matches()){
				for (int i = 2; i < 9999; i++) {
					String newFileName = matcher.replaceAll("$1_"+i+".$3");
					File newFile = new File(newFileName);
					if(!newFile.exists()){
						file = newFile;
						break;
					}
				}
				
			}
		}
		return file;
	}

}
