/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
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
