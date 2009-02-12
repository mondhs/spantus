package org.spantus.ui;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class ReflectionUtils {
	/**
	 * Contains a list of all types that are considered primitive; this includes
	 * all primitive and wrapper types as well as strings. These are considered
	 * attributes in XML Elements
	 */
	private static Set<String> primitiveTypes = new HashSet<String>();
	static {
		primitiveTypes.add("byte");
		primitiveTypes.add("short");
		primitiveTypes.add("int");
		primitiveTypes.add("long");
		primitiveTypes.add("float");
		primitiveTypes.add("double");
		primitiveTypes.add("boolean");
		primitiveTypes.add("java.lang.String");
		primitiveTypes.add("java.lang.Byte");
		primitiveTypes.add("java.lang.Short");
		primitiveTypes.add("java.lang.Integer");
		primitiveTypes.add("java.lang.Long");
		primitiveTypes.add("java.lang.Float");
		primitiveTypes.add("java.lang.Double");
		primitiveTypes.add("java.math.BigDecimal");
		primitiveTypes.add("java.lang.Boolean");
	}

	private static Set<String> excludedProperties = new HashSet<String>();
	static {
		excludedProperties.add("class");
	}

	/**
	 * Maps class names to a map of its property names to its Property object
	 */
	private static Map<String, Map<String, Property>> classProperties = new TreeMap<String, Map<String, Property>>();

	/**
	 * Returns true if the specified class name is a primitive type, otherwise
	 * false
	 * 
	 * @param className
	 *            The class name to examine to see if it is a primitive type
	 * @return
	 */
	public static boolean isPrimitiveType(String className) {
		return primitiveTypes.contains(className);
	}

	public static boolean isBooleanType(String className) {
		return className.equals("boolean")
				|| className.equals("java.lang.Boolean");
	}

	/**
	 * Returns true if the specified class is a Collection, otherwise it returns
	 * false
	 * 
	 * @param c
	 *            The class to examine
	 * @return True if the specified class is a Collection, otherwise false
	 * @throws com.javasrc.rtools.rxml.RXMLException
	 */
	public static boolean isCollection(Class<?> c) throws Exception {
		return containsInterface(c, "java.util.Collection") || isSet(c)
				|| isList(c);
	}

	/**
	 * Returns true if the specified class is a Set, otherwise it returns false
	 * 
	 * @param c
	 *            The class to examine to see if it’s a set
	 * @return True if the specified class is a Set, false otherwise
	 * @throws RXMLException
	 */
	public static boolean isSet(Class<?> c) throws Exception {
		return containsInterface(c, "java.util.Set");
	}

	/**
	 * Returns true if the specified class is a List, otherwise it returns false
	 * 
	 * @param c
	 *            The class to examine to see if it’s a list
	 * @return True if the specified class is a List, false otherwise
	 * @throws RXMLException
	 */
	public static boolean isList(Class<?> c) throws Exception {
		return containsInterface(c, "java.util.List");
	}

	/**
	 * Returns true if the specified class is a Map, otherwise it returns false
	 * 
	 * @param c
	 *            The class to examine to see if it’s a map
	 * @return True if the specified class is a Map, false otherwise
	 * @throws RXMLException
	 */
	public static boolean isMap(Class<?> c) throws Exception {
		return containsInterface(c, "java.util.Map");
	}

	/**
	 * Examines a class and searches through all interfaces that it implements.
	 * If it implements the specified "interfaceName" then this method returns
	 * true, otherwise it returns false.
	 * 
	 * @param c
	 *            The class to examine
	 * @param interfaceName
	 *            The name of the interface to look for
	 * @return True if the class implements the specified interface, otherwise
	 *         false
	 * @throws RXMLException
	 */
	public static boolean containsInterface(Class<?> c, String interfaceName)
			throws Exception {
		try {
			// If this class is the interface then it will not implement itself
			if (c.getName().equals(interfaceName)) {
				return true;
			}

			// Extract all of the interfaces from the specified class
			Class<?>[] interfaces = c.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				// Compare the interface against the specified criteria
				String iName = interfaces[i].getName();
				if (iName.equals(interfaceName)) {
					// Found it
					return true;
				}
			}

			// Completed iterating over all interfaces and this class does not
			// implement the
			// specified interface
			return false;
		} catch (Exception e) {
			// Wrap the exception in an RXMLException and throw it to the caller
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public static Class<?> getClassFromName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}
		return null;
	}

	/**
	 * Returns a map of property names to Property instances for the specified
	 * class name
	 * 
	 * @param className
	 * @return
	 */
	public static Map<String, Property> getClassProperties(String className) {
		try {
			Class<?> c = Class.forName(className);
			return getClassProperties(c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}
		return null;
	}

	/**
	 * Returns a map of property names to Property instances for the specified
	 * class
	 * 
	 * @param c
	 * @return
	 */
	public static Map<String, Property> getClassProperties(Class<?> c) {
		String className = c.getName();
		if (classProperties.containsKey(className)) {
			// We already know about this class, so return its properties
			return (Map<String, Property>) classProperties.get(className);
		}

		// Define the property map
		Map<String, Property> propertyMap = new TreeMap<String, Property>();

		// Obtain and iterate over all methds
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();
			if (methodName.startsWith("get") || methodName.startsWith("is")) {
				// Check the number of parameters passed to this method;
				// we only consider a getter an attribute of this class if
				// it does not accept any parameters
				Class<?>[] parameterTypes = methods[i].getParameterTypes();
				if (parameterTypes.length > 0) {
					continue;
				}

				// Found a getter, but exclude any transient fields
				int modifiers = methods[i].getModifiers();
				if (!Modifier.isTransient(modifiers)) {
					// Get the name of this property as well as the type that
					// it returns
					String propertyName = null;
					if (methodName.startsWith("get")) {
						propertyName = Character.toLowerCase(methodName
								.charAt(3))
								+ methodName.substring(4);
					} else {
						propertyName = Character.toLowerCase(methodName
								.charAt(2))
								+ methodName.substring(3);
					}

					// See if we already know about this property
					if (propertyMap.containsKey(propertyName)) {
						// Update its getter method
						Property prop = (Property) propertyMap
								.get(propertyName);
						prop.setGetter(methods[i]);
					} else {
						String returnClass = methods[i].getReturnType()
								.getName();

						// Put this property into the property map
						if (!excludedProperties.contains(propertyName)) {
							// This is a new property for this class so create
							// it and add it to the map
							Property prop = new Property(propertyName,
									returnClass, null, methods[i]);
							propertyMap.put(propertyName, prop);
						}
					}
				}
			} else if (methodName.startsWith("set")) {
				// Obtain the property name for this setter method
				String propertyName = Character.toLowerCase(methodName
						.charAt(3))
						+ methodName.substring(4);

				// Ensure that the return type is void; setters shouldn’t return
				// any values
				String returnClass = methods[i].getReturnType().getName();
				if (returnClass.equals("void")) {
					// Get the list of parameter types; there should only be one
					// if this is a setter
					Class<?>[] parameterTypes = methods[i].getParameterTypes();
					if (parameterTypes.length == 1) {
						// This is truly a setter (starts with set, returns
						// void, and accepts no parameters
						if (propertyMap.containsKey(propertyName)) {
							// We already know about this property so update its
							// setter method
							Property prop = (Property) propertyMap
									.get(propertyName);
							prop.setSetter(methods[i]);
						} else {
							// Get the class name of the parameter that this
							// setter accepts
							String parameterClassName = parameterTypes[0]
									.getName();

							// Create a new Property
							Property prop = new Property(propertyName,
									parameterClassName, methods[i], null);

							// Put this property into the property map
							propertyMap.put(propertyName, prop);
						}
					}
				}
			}

		}

		// Save the class properties
		classProperties.put(className, propertyMap);

		// Return the property map to the caller
		return propertyMap;
	}

	/**
	 * Returns the Property object for the specified class’s property
	 * 
	 * @param c
	 * @param propertyName
	 * @return
	 */
	public static Property getProperty(Class<?> c, String propertyName) {
		Map<String, Property> props = getClassProperties(c);
		if (!props.containsKey(propertyName)) {
			return null;
		}
		return (Property) props.get(propertyName);
	}

	/**
	 * Reads the specified property on the specified object and returns its
	 * value as an object
	 * 
	 * @param obj
	 * @param propertyName
	 * @return
	 */
	public static Object getPropertyValue(Object obj, String propertyName) {
		Property prop = getProperty(obj.getClass(), propertyName);
		if (prop.isReadable()) {
			try {
				return prop.getGetter().invoke(obj, new Object[] {});
			} catch (IllegalAccessException e) {
				e.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			} catch (InvocationTargetException e) {
				e.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			}
		}
		return null;
	}

	/**
	 * Returns the setter method for the specifed class’s property
	 * 
	 * @param className
	 * @param propertyName
	 * @return
	 */
	public static Method getSetterMethod(String className, String propertyName) {
		Class<?> c = getClassFromName(className);
		if (c == null)
			return null;
		return getSetterMethod(c, propertyName);
	}

	/**
	 * Returns the setter method for the specifed class’s property
	 * 
	 * @param c
	 * @param propertyName
	 * @return
	 */
	public static Method getSetterMethod(Class<?> c, String propertyName) {
		Property prop = getProperty(c, propertyName);
		if (prop == null)
			return null;

		return prop.getSetter();
	}

	/**
	 * Returns the setter method for the specifed class’s property
	 * 
	 * @param className
	 * @param propertyName
	 * @return
	 */
	public static Method getGetterMethod(String className, String propertyName) {
		Class<?> c = getClassFromName(className);
		if (c == null)
			return null;
		return getGetterMethod(c, propertyName);
	}

	/**
	 * Returns the getter method for the specified class’s property
	 * 
	 * @param c
	 * @param propertyName
	 * @return
	 */
	public static Method getGetterMethod(Class<?> c, String propertyName) {
		Property prop = getProperty(c, propertyName);
		if (prop == null)
			return null;

		return prop.getGetter();
	}

	/**
	 * Returns an array of Annotation objects implemented by the getter method
	 * for the specified property
	 * 
	 * @param className
	 * @param propertyName
	 * @return
	 */
	public static Annotation[] getGetterAnnotations(String className,
			String propertyName) {
		Class<?> c = getClassFromName(className);
		if (c == null)
			return null;
		return getGetterAnnotations(c, propertyName);
	}

	/**
	 * Returns an array of Annotation objects implemented by the getter method
	 * for the specified property
	 * 
	 * @param c
	 * @param propertyName
	 * @return
	 */
	public static Annotation[] getGetterAnnotations(Class<?> c,
			String propertyName) {
		Method m = getGetterMethod(c, propertyName);
		if (m == null)
			return null;

		return m.getDeclaredAnnotations();
	}

	/**
	 * Returns an array of Annotation objects implemented by the setter method
	 * for the specified property
	 * 
	 * @param className
	 * @param propertyName
	 * @return
	 */
	public static Annotation[] getSetterAnnotations(String className,
			String propertyName) {
		Class<?> c = getClassFromName(className);
		if (c == null)
			return null;
		return getSetterAnnotations(c, propertyName);
	}

	/**
	 * Returns an array of Annotation objects implemented by the setter method
	 * for the specified property
	 * 
	 * @param c
	 * @param propertyName
	 * @return
	 */
	public static Annotation[] getSetterAnnotations(Class<?> c,
			String propertyName) {
		Method m = getSetterMethod(c, propertyName);
		if (m == null)
			return null;

		return m.getAnnotations();
	}

	/**
	 * Returns the value of the specified setter property’s annotation’s
	 * property.
	 * 
	 * @param className
	 * @param propertyName
	 * @param annotationName
	 * @param annotationPropertyName
	 * @return
	 */
	public static Object getSetterAnnotationPropertyValue(String className,
			String propertyName, String annotationName,
			String annotationPropertyName) {
		try {
			Class<?> c = Class.forName(className);
			return getSetterAnnotationPropertyValue(c, propertyName,
					annotationName, annotationPropertyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the value of the specified setter property’s annotation’s
	 * property.
	 * 
	 * @param c
	 * @param propertyName
	 * @param annotationName
	 * @param annotationPropertyName
	 * @return
	 */
	public static Object getSetterAnnotationPropertyValue(Class<?> c,
			String propertyName, String annotationName,
			String annotationPropertyName) {
		Annotation[] annotations = getSetterAnnotations(c, propertyName);
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType().getName()
					.equals(annotationName)) {
				// Get the value of this property
				return getAnnotationPropertyValue(annotations[i],
						annotationPropertyName);
			}
		}
		return null;

	}

	/**
	 * Given an Annotation and a property name, this method finds the property
	 * method, invokes it, and returns the result
	 * 
	 * @param a
	 * @param annotationPropertyName
	 * @return
	 */
	public static Object getAnnotationPropertyValue(Annotation a,
			String annotationPropertyName) {
		try {
			Method[] methods = a.annotationType().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(annotationPropertyName)) {
					// Found the method
					return methods[i].invoke(a, new Object[] {});
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns an array of Annotation objects implemented by this property. Note
	 * that this method simply defaults to (forwards to) getGetterAnnotations()
	 * 
	 * @param className
	 * @param propertyName
	 * @return
	 */
	public static Annotation[] getPropertyAnnotations(String className,
			String propertyName) {
		return getGetterAnnotations(className, propertyName);
	}

	/**
	 * Returns an array of Annotation objects implemented by this property. Note
	 * that this method simply defaults to (forwards to) getGetterAnnotations()
	 * 
	 * @param c
	 * @param propertyName
	 * @return
	 */
	public static Annotation[] getPropertyAnnotations(Class<?> c,
			String propertyName) {
		return getGetterAnnotations(c, propertyName);
	}

	/**
	 * Given a class, this method returns a map of property names to their
	 * types, for all readable properties.
	 * 
	 * @param c
	 * @return
	 */
	public static Map<String, String> getReadableProperties(Class<?> c) {
		// Get all properties for this class
		Map<String, Property> props = getClassProperties(c);

		// Find only the writeable properties
		Map<String, String> propertyMap = new TreeMap<String, String>();
		for (Iterator<String> i = props.keySet().iterator(); i.hasNext();) {
			String name = (String) i.next();
			Property prop = (Property) props.get(name);
			if (prop.isReadable()) {
				propertyMap.put(name, prop.getClassType());
			}

		}

		// Return the writeable properties
		return propertyMap;
	}

	/**
	 * Given a class name, this method returns a map of property names to their
	 * types, for all readable properties.
	 * 
	 * @param className
	 * @return
	 */
	public static Map<String, String> getReadableProperties(String className) {
		try {
			Class<?> c = Class.forName(className);
			return getReadableProperties(c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}
		return null;
	}

	/**
	 * Given a class, this method returns a map of property names to their
	 * types, for all writeable properties.
	 * 
	 * @param c
	 * @return
	 */
	public static Map<String, Property> getWriteableProperties(Class<?> c) {
		// Get all properties for this class
		Map<String, Property> props = getClassProperties(c);

		// Find only the writeable properties
		Map<String, Property> propertyMap = new TreeMap<String, Property>();
		for (Iterator<String> i = props.keySet().iterator(); i.hasNext();) {
			String name = (String) i.next();
			Property prop = (Property) props.get(name);
			if (prop.isWriteable()) {
				propertyMap.put(name, prop);
			}

		}

		// Return the writeable properties
		return propertyMap;
	}

	/**
	 * Given a class name, this method returns a map of property names to their
	 * types, for all writeable properties.
	 * 
	 * @param className
	 * @return
	 */
	public static Map<String, Property> getWriteableProperties(String className) {
		try {
			Class<?> c = Class.forName(className);
			return getWriteableProperties(c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}
		return null;
	}

	public static boolean isSetter(Method method) {
		String methodName = method.getName();
		Class<?> returnClass = method.getReturnType();
		// Ensure that the return type is void; setters shouldn’t
		// return
		// any values
		// Get the list of parameter types; there should only be
		// one
		// if this is a setter

		Class<?>[] parameterTypes = method.getParameterTypes();
		return methodName.startsWith("set") && returnClass.equals(void.class)
				&& parameterTypes.length == 1;
	}

	/**
	 * Builds an object with the specified class name and sets its writable
	 * properties to the values specified in the propertyMap
	 * 
	 * @param className
	 * @param propertyMap
	 * @return
	 */
	public static Object buildObject(Object o,
			Map<String, String> propertyMap) {
		try {
			Class<?> clazz = o.getClass();

			// Obtain and iterate over all methds
			Method[] methods = clazz.getMethods();
			for (int i = 0; i < methods.length; i++) {
				String methodName = methods[i].getName();
				if (isSetter(methods[i])) {
					// Obtain the property name for this setter method
					String propertyName = Character.toLowerCase(methodName
							.charAt(3))
							+ methodName.substring(4);

					Class<?> parameterType = methods[i].getParameterTypes()[0];
					// We have a setter method, let’s see if we have a
					// corresponding property to set
					if (propertyMap.containsKey(propertyName)) {
						// Convert the value...
						if (primitiveTypes.contains(parameterType.getName())) {
							// The value is stored as a String
							String value = (String) propertyMap
									.get(propertyName);
							if (value != null && value.length() > 0) {
								// Build the parameter list (one object)
								Object[] parameters = new Object[1];
								parameters[0] = convertStringToObjectWrapper(
										parameterType.getName(), value);

								// Invoke the setter method
								methods[i].invoke(o, parameters);
							}
						} else {
							// Not support (yet)
						}
					}
				}
			}

			// Return the property map
			return o;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Converts the string value to the specified wrapper class type
	 * 
	 * @param className
	 *            The wrapper type to convert the string to
	 * @param value
	 *            The string value to convert
	 * @return
	 */
	private static Object convertStringToObjectWrapper(String className,
			String value) {
		// Build a parameter class to pass as a parameter to
		Object o = null;
		if (className.equals("java.lang.Integer") || className.equals("int")) {
			o = new Integer(value);
		} else if (className.equals("java.lang.Long")
				|| className.equals("long")) {
			o = Long.valueOf(value);
		} else if (className.equals("java.lang.Short")
				|| className.equals("short")) {
			o = Short.valueOf(value);
		} else if (className.equals("java.lang.Byte")
				|| className.equals("byte")) {
			o = Byte.valueOf(value);
		} else if (className.equals("java.lang.Float")
				|| className.equals("float")) {
			o = Float.valueOf(value);
		} else if (className.equals("java.lang.Double")
				|| className.equals("double")) {
			o = Double.valueOf(value);
		} else if (className.equals(BigDecimal.class.getName())) {
			o = BigDecimal.valueOf(Double.valueOf(value));
		} else if (className.equals("java.lang.String")) {
			o = value;
		} else if (className.equals("java.lang.Boolean")
				|| className.equals("boolean")) {
			o = Boolean.valueOf(value);
		}

		// Return the object we built
		return o;
	}
}
