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
package org.spantus.ui;

import java.lang.reflect.Method;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class Property {
	/**
	 * The name of the property
	 */
	private String name;

	/**
	 * The setter method
	 */
	private Method setter;

	/**
	 * The getter method
	 */
	private Method getter;

	/**
	 * Is this property readable
	 */
	private boolean readable;

	/**
	 * Is this property writeable
	 */
	private boolean writeable;

	/**
	 * The type of class of this property
	 */
	private String classType;

	public Property() {
	}

	public Property(String name, String classType, Method setter, Method getter) {
		this.name = name;
		this.classType = classType;
		this.setter = setter;
		this.getter = getter;

		if (setter != null)
			this.writeable = true;
		else
			this.writeable = false;

		if (getter != null)
			this.readable = true;
		else
			this.readable = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Method getSetter() {
		return setter;
	}

	public void setSetter(Method setter) {
		this.setter = setter;
		if (setter != null)
			this.writeable = true;
	}

	public Method getGetter() {
		return getter;
	}

	public void setGetter(Method getter) {
		this.getter = getter;
		if (getter != null)
			this.readable = true;
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

	public boolean isWriteable() {
		return writeable;
	}

	public void setWriteable(boolean writeable) {
		this.writeable = writeable;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("Property(").append(name).append(
				": ");
		sb.append(classType).append(") -> ");
		if (readable)
			sb.append("getter: ").append(getter.getName());
		if (writeable)
			sb.append(", setter: ").append(setter.getName());
		return sb.toString();
	}

}
