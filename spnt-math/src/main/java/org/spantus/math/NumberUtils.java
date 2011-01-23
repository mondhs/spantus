package org.spantus.math;


/**
 * Based on google guava
 * @author mondhs
 *
 */
public class NumberUtils {
	  /**
	   * Returns the greatest value present in {@code array}.
	   *
	   * @param array a <i>nonempty</i> array of {@code int} values
	   * @return the value present in {@code array} that is greater than or equal to
	   *     every other value in the array
	   * @throws IllegalArgumentException if {@code array} is empty
	   */
	  public static int max(int... array) {
//	    checkArgument(array.length > 0);
	    int max = array[0];
	    for (int i = 1; i < array.length; i++) {
	      if (array[i] > max) {
	        max = array[i];
	      }
	    }
	    return max;
	  }
	  /**
	   * Returns the least value present in {@code array}.
	   *
	   * @param array a <i>nonempty</i> array of {@code int} values
	   * @return the value present in {@code array} that is less than or equal to
	   *     every other value in the array
	   * @throws IllegalArgumentException if {@code array} is empty
	   */
	  public static int min(int... array) {
//	    checkArgument(array.length > 0);
	    int min = array[0];
	    for (int i = 1; i < array.length; i++) {
	      if (array[i] < min) {
	        min = array[i];
	      }
	    }
	    return min;
	  }
	  
	  /**
	   * 
	   * @param a
	   * @param b
	   * @return
	   */
	  public static int compare(float a, float b) {
		    return Float.compare(a, b);
	  }
}
