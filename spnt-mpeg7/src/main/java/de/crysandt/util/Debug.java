/*
  Copyright (c) 2002-2006, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.util;

import java.io.*;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public final class Debug
{
	public static boolean print(PrintStream out, String s) {
		out.print(s);
		return true;
	}
	
	public static boolean print(PrintWriter out, String s) {
		out.print(s);
		return true;
	}
	
	public static boolean println(PrintStream out, String s) {
		out.println(s);
		return true;
	}
	
	public static boolean println(PrintWriter out, String s) {
		out.println(s);
		return true;
	}
	
	public static boolean printStackTrace(PrintStream out, Exception e){
		e.printStackTrace(out);
		return true;
	}
	
	public static boolean printStackTrace(PrintWriter out, Exception e){
		e.printStackTrace(out);
		return true;
	}
	
	public static boolean run(Runnable r) {
		r.run();
		return true;
	}

	/**
	 * tests if abs(value - expected) < tolerance
	 * 
	 * @param expected Expected value
	 * @param actual value
	 * @param tolerance size of toleance between exptected and actual value
	 * 
	 * @return Returns whether the absolute differenece of expected and actual 
	 * 	value is smaller than the given tolerance
	 */
	public static boolean equals(float expected, float value, float tolerance) {
		return Math.abs(expected - value) < tolerance; 
	}
}