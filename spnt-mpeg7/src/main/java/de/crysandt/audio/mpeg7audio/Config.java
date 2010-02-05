/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import java.util.*;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class Config
	implements Cloneable
{
	protected Map<Object, Object> config = new HashMap<Object, Object>();
	
	public Object clone()
		throws CloneNotSupportedException
	{
		Config cloned = (Config) super.clone();
		
		cloned.config = new HashMap<Object, Object>();
		
		for (Iterator<Object> i = config.keySet().iterator(); i.hasNext(); ) {
			Object key = i.next();
			cloned.config.put(key, config.get(key));
		}
		
		return cloned;
	}
	
	public Object getValue(String module, String name) {
		return config.get(key(module, name));
	}
	
	public String getString(String module, String name) {
		return getValue(module, name).toString();
	}
	
	public boolean getBoolean(String module, String name) {
		return getString(module, name).equals(Boolean.TRUE.toString());
	}
	
	public int getInt(String module, String name) {
		return Integer.parseInt(getValue(module, name).toString());
	}
	
	public long getLong(String module, String name) {
		return Long.parseLong(getString(module, name));
	}
	
	public float getFloat(String module, String name) {
		return Float.parseFloat(getString(module, name));
	}
	
	public double getDouble(String module, String name) {
		return Double.parseDouble(getString(module, name));
	}
	
	public void setValue(String module, String name, Object value) {
		config.put(key(module, name), value);
	}
	
	public void setValue(String module, String name, boolean value) {
		setValue(module, name, "" + value);
	}
	
	public void setValue(String module, String name, int value) {
		setValue(module, name, "" + value);
	}
	
	public void setValue(String module, String name, long value) {
		setValue(module, name, "" + value);
	}
	
	public void setValue(String module, String name, float value) {
		setValue(module, name, "" + value);
	}
	
	public void setValue(String module, String name, double value) {
		setValue(module, name, "" + value);
	}
	
	/**
	 * Turns all modules on/off
	 *
	 * @param enable true: on; false: off
	 */
	
	public void enableAll(boolean enable) {
		Set<String> keys = new TreeSet<String>();
		for (Iterator<Object> i=config.keySet().iterator(); i.hasNext(); ){
			String key = i.next().toString();
			if (key.endsWith("enable"))
				keys.add(key);
		}
		
		for (Iterator<String> i=keys.iterator(); i.hasNext(); )
			config.put(i.next(), "" + enable);
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Iterator<Object> i = new TreeSet<Object>(config.keySet()).iterator(); i.hasNext(); ) {
			Object key = (String) i.next();
			s.append(key + ": " + config.get(key) + "\n");
		}
		return s.toString();
	}
	
	private static String key(String module, String name) {
		return module + "_" + name;
	}
}
