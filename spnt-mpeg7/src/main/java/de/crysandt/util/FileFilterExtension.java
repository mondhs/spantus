/*
  Copyright (c) 2002-2006, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.util;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class FileFilterExtension
    extends FileFilter
{
  public static final String[] AUDIO = {"wav", "au", "aiff", "mp3"};
  public static final String[] MP7   = {"mp7", "xml"};

  private final String[] extension;

  public FileFilterExtension(String[] extension) {
    super();
    this.extension = extension;
  }

  public String getDescription() {
    StringBuffer buffer = new StringBuffer( "*." + extension[0] );
    for (int i=1; i<extension.length; ++i)
      buffer.append( ", *." + extension[i]);
    return buffer.toString();
  }

  public boolean accept(File file) {
    if (file.isDirectory())
      return true;

    String ext = getExtension(file);
    if (ext != null) {
      for (int i=0; i<extension.length; ++i)
        if (ext.equals(extension[i]))
          return true;
    }

    return false;
  }

  String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if(i > 0 &&  i < s.length() - 1)
      ext = s.substring(i+1).toLowerCase();

    return ext;
  }
}
