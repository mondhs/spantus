package org.spantus.work.ui.cmd;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class UIFileFilter extends FileFilter {

	private String[] extension;
	private String type;

	public UIFileFilter(String[] extension, String type) {
		this.extension = extension;
		this.type = type;
		
	}
	
	@Override
	 public boolean accept(File file) {
	    if (file.isDirectory())
	      return true;

	    String ext = getExtensionName(file);
	    if (ext != null) {
	      for (int i=0; i<getExtension().length; ++i)
	        if (file.getName().endsWith(getExtension()[i]))
	          return true;
	    }

	    return false;
	  }

	@Override
	public String getDescription() {
		StringBuilder buffer = new StringBuilder( "*." + getExtension()[0] );
	    for (int i=1; i<getExtension().length; ++i)
	      buffer.append( ", *." + extension[i]);
	    return buffer.toString();
	}
	
	String getExtensionName(File f) {
	    String ext = null;
	    String s = f.getName();
	    int i = s.lastIndexOf('.');

	    if(i > 0 &&  i < s.length() - 1)
	      ext = s.substring(i+1).toLowerCase();

	    return ext;
	  }
	
	public void setExtension(String[] extension) {
		this.extension = extension;
	}
	
	public String[] getExtension() {
		return extension;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
