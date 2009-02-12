package org.spantus.ui;

import java.lang.reflect.Method;

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
		StringBuffer sb = new StringBuffer("Property(").append(name).append(
				": ");
		sb.append(classType).append(") -> ");
		if (readable)
			sb.append("getter: ").append(getter.getName());
		if (writeable)
			sb.append(", setter: ").append(setter.getName());
		return sb.toString();
	}

}
