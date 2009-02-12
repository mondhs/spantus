/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.math;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public final class Function {
	public static final double LOG2  = Math.log(2.0);
	public static final double LOG10 = Math.log(10.0);
	
	/**
	 * To increase the performance use 
	 * y = Math.log(x) / FUNCTION.LOG2 instead.
	 */
	public static float log2(double x) {
		return (float) (Math.log(x) / LOG2);
	}

	/**
	 * To increase the performance use 
	 * y = Math.log(x) / Function.LOG10 instead.
	 */
	public static float log10(double x) {
		return (float) (Math.log(x) / LOG10);
	}
	
	/**
	 * Calclulates square of a float value. Due to performance this function 
	 * should not be used too often.
	 * @param x value to be squared
	 * @return Returns x^2
	 */
	public static float square(float x) {
		return x * x;
	}
	
	/**
	 * Calculates square of a double value. Due to performance this function 
	 * should not be used too often.
	 * @param x value to be squared
	 * @return Returns x^2
	 */
	public static double square(double x) {
		return x * x;
	}
	
	public static float sum(float[] data) {
		double sum = 0.0f;
		for (int i = 0; i < data.length; ++i)
			sum += data[i];
		return (float) sum;
	}
	
	public static double sum(double[] data) {
		double sum = 0.0f;
		for (int i = 0; i < data.length; ++i)
			sum += data[i];
		return sum;
	}
	
	public static float min(float[] data) {
		float min = data[0];
		for (int i = 1; i < data.length; ++i)
			if (data[i] < min)
				min = data[i];
		return min;
	}
	
	public static int min_index(float[] data) {
		float min = data[0];
		int index = 0;
		for (int i = 1; i < data.length; ++i)
			if (data[i] < min)
				min = data[index = i];
		return index;
	}
	
	public static float max(float[] data) {
		float max = data[0];
		for (int i = 1; i < data.length; ++i)
			if (data[i] > max)
				max = data[i];
		return max;
	}
	
	public static double max(double[] data) {
		double max = data[0];
		for (int i = 1; i < data.length; ++i)
			if (data[i] > max)
				max = data[i];
		return max;
	}
	
	public static int max_index(float[] data) {
		float max = data[0];
		int index = 0;
		for (int i = 1; i < data.length; ++i)
			if (data[i] > max)
				max = data[index = i];
		return index;
	}
	
	public static short max(short[] data) {
		short max = data[0];
		for (int i = 1; i < data.length; ++i)
			if (data[i] > max)
				max = data[i];
		return max;
	}
	
	public static float mean_arith(float[] data) {
		return sum(data) / data.length;
	}
	
	public static double mean_arith(double[] data) {
		return sum(data) / data.length;
	}
	
	public static float mean_geom(float[] data) {
		double mean = 1.0f;
		for (int i = 0; i < data.length; ++i)
			mean *= data[i];
		return (float) Math.pow(mean, 1.0 / data.length);
	}
	
	public static double mean_geom(double[] data) {
		double mean = 1.0f;
		for (int i = 0; i < data.length; ++i)
			mean *= data[i];
		return Math.pow(mean, 1.0 / data.length);
	}
	
	public static float variance(float[] data, float mean) {
		double v = 0.0;
		for (int i = 0; i < data.length; ++i)
			v += data[i] * data[i];
		v /= data.length;
		v -= mean * mean;
		return (float) v;
	}
	
	public static double variance(double[] data, double mean) {
		double tmp, v = 0.0;
		for (int i = 0; i < data.length; ++i) {
			tmp = data[i];
			v += tmp * tmp;
		}
		v /= data.length;
		v -= mean * mean;
		return v;
	}
	
	public static float variance(float[] data) {
		return variance(data, mean_arith(data));
	}
	
	public static double variance(double[] data) {
		return variance(data, mean_arith(data));
	}
	
	
	/**
	 * Determines the arithmetic mean value of each column of a
	 * matrix stored row-by-row.
	 *
	 * @param matrix Matrix stored row-by-row
	 *
	 * @return Returns Mean values of each column
	 */
	public static float[] mean_arith(float[][] matrix) {
		int rows = matrix.length;
		int cols = matrix[0].length;
		
		double[] sum = new double[cols];
		for (int r = 0, c; r < rows; ++r) {
			float[] row = matrix[r];
			for (c = 0; c < row.length; ++c)
				sum[c] += row[c];
		}
		
		float[] mean = new float[cols];
		for (int i=0; i<cols; ++i)
			mean[i] = (float) (sum[i] / rows);
		
		return mean;
	}
	
	/**
	 * Determines the variance of each column of a matrix stored row-by-row
	 *
	 * @param matrix Matrix stored row-by-row
	 *
	 * @return Returns variance of each column
	 */
	public static float[] variance(float[][] matrix) {
		return variance(matrix,mean_arith(matrix));	
	}	
	
	/**
	 * Determinex the variance of each column of a matrix stored row-by-row 
	 * if the arithmetic mean of the matrix was calculated before.<p/>
	 * 
	 * Example:<p/>
	 * float[] mean = Function.mean_arith(matrix);<p/>
	 * float[] variance = Fuction.variance(matrix, mean);<p/>
	 * 
	 * @param matrix Matrix stored row-by-row
	 * @param mean Mean value of each column
	 * @return Returns variace of each column
	 */
	public static float[] variance(float[][] matrix, float[] mean) {
		int rows = matrix.length;
		int cols = matrix[0].length;
		
		double[] power = new double[cols];
		for (int r = 0, c; r < rows; ++r) {
			float[] row = matrix[r];
			float row_c; 
			for (c = 0; c < row.length; ++c)
				power[c] += (row_c = row[c]) * row_c;
		}
		
		float[] var = new float[cols];		
		for (int c = 0; c < var.length; ++c)
			var[c] = (float) (power[c] / rows) - mean[c] * mean[c];
		
		return var;
	}
	
	public static int getHighestBit(int x) {
		int highest_bit = 0;
		
		for (int shift=16; shift>=1; shift /= 2) {
			if (x>=1<<shift) {
				highest_bit += shift;
				x >>= shift;
			}	
		}
		
		return highest_bit;
	}	
}
