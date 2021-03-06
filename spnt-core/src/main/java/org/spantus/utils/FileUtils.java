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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Mindaugas Greibus
 * 
 *         Created Feb 22, 2010
 * 
 */
public final class FileUtils {

	public static String stripExtention(File file) {
		String fileName = file.getName();
		return stripExtention(fileName);
	}

	public static String replaceExtention(File file, String newExtention) {
		String fileName = file.getName();
		return stripExtention(fileName) + newExtention;
	}

	public static String stripExtention(String file) {
		file = file.replaceAll("\\.\\w{2,8}$", "");
		file = file.replaceAll("\\.\\w{2,8}$", "");
		return file;
	}

	/**
	 * Fix this
	 * 
	 * @param file
	 * @return
	 */
	public static String truncateDir(String fileName) {
		if (fileName == null) {
			return fileName;
		}
		File file = new File(fileName);
		return file.getName();
	}

	/**
	 * Check if dir exists. if not create all of them
	 * 
	 * @param dirName
	 * @return
	 */
	public static File checkDirs(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static String getPath(File file) {
		return file.getParentFile().getAbsolutePath() + "/";
	}

	public static File findNextAvaibleFile(String fileName) {
		File file = new File(fileName);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (file.exists()) {
			Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
			Matcher matcher = pattern.matcher(fileName);
			if (matcher.matches()) {
				for (int i = 2; i < 9999; i++) {
					String newFileName = matcher.replaceAll("$1_" + i + ".$3");
					File newFile = new File(newFileName);
					if (!newFile.exists()) {
						file = newFile;
						break;
					}
				}

			}
		}
		return file;
	}

	/**
	 * 
	 * @param root
	 * @return
	 */
	public static String findFirstMatchFullPath(String root, String sufix) {
		File rootDir = new File(root);
		if (rootDir.isDirectory()) {
			for (String file : rootDir.list()) {
				if (file.endsWith(sufix)) {
					return root + file;
				}

			}
		}
		return null;
	}

	public static File findNewestMatchFullPath(String root, String sufix) {
		File rootDir = new File(root);
		File newestFile = null;
		if (rootDir.isDirectory()) {
			for (String file : rootDir.list()) {
				
				if (!file.endsWith(sufix)) {
					continue;
				}
				File aFile = new File(rootDir, file);
				if (newestFile == null) {
					newestFile = aFile;
					continue;
				}
				if (newestFile.lastModified() < aFile.lastModified()) {
					newestFile = aFile;
				}

			}
		}
		return newestFile;
	}

	/**
	 * 
	 */
	public static List<String> findAllMatchFullPath(String root, String sufix) {
		File rootDir = new File(root);

		List<String> results = new ArrayList<String>();

		if (rootDir.isDirectory()) {
			for (String file : rootDir.list()) {
				if (file.endsWith(sufix)) {
					results.add(root + "/" + file);
				}

			}
		}

		Collections.sort(results);
		return results;
	}
}
