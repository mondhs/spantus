/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.math;

/**
 * (Incomplete) set of functions from the field of linear algebra.<p/>
 *
 * Matrices can be stored in two ways:
 * <ul>
 * <li><b>row-by-row</b>: matrix[row][column]
 * <li><b>column-by-column</b>: matrix[column][row]
 * </ul>
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public final class LinAlg {
	/**
	 * Determines the determinant of a square matrix
	 *
	 * @param matrix Square matrix stored row-by-row.
	 *
	 * @return Returns determinant of matrix
	 */
	public static double det(float[][] matrix) {
		return det(toDouble(matrix));
	}
	
	public static double det(double[][] matrix) {
		LUDecomposition lud = new LUDecomposition(matrix);
		return lud.det();
	}
	
	/**
	 * Determines the inverse of a square matrix
	 *
	 * @param matrix Square matrix (row-by-row)
	 *
	 * @return Returns inverse of square matrix
	 *
	 * @throws IllegalArgumentException Throws exception if matrix is singular
	 */
	public static float[][] inv(float[][] matrix)
		throws IllegalArgumentException
	{
		int rows = matrix.length;
		
		double[][] matrix_double = new double[rows][];
		for (int r=0; r<rows; ++r)
			matrix_double[r] = toDouble(matrix[r]);
		
		double[][] matrix_double_inv = inv(matrix_double);
		
		float[][] matrix_inv = new float[rows][];
		for (int r=0; r<rows; ++r)
			matrix_inv[r] = toFloat(matrix_double_inv[r]);
		
		return matrix_inv;
	}
	
	public static double[][] inv(double[][] matrix)
		throws IllegalArgumentException
	{
		int rows = matrix.length;
		
		double[][] matrix_copy = new double[rows][rows];
		for (int r = 0; r < rows; ++r)
			System.arraycopy(matrix[r], 0, matrix_copy[r], 0, rows);
		
		double[][] matrix_inv = new double[rows][rows];
		for (int n = 0; n < rows; ++n)
			matrix_inv[n][n] = 1.0;
		
		inv(rows, matrix_copy, matrix_inv);
		
		return matrix_inv;
	}
	
	public static double[] toDouble(float[] vector) {
		double[] tmp = new double[vector.length];
		for (int i=0; i<vector.length; ++i)
			tmp[i] = vector[i];
		return tmp;
	}
	
	public static double[][] toDouble(float [][] matrix) {
		double[][] tmp = new double[matrix.length][];
		for (int i=0; i<tmp.length; ++i)
			tmp[i] = toDouble(matrix[i]);
		return tmp;
	}
	
	public static float[] toFloat(double[] vector) {
		float[] tmp = new float[vector.length];
		for (int i=0; i<vector.length; ++i)
			tmp[i] = (float) vector[i];
		return tmp;
	}
	
	public static float[][] toFloat(double [][] matrix) {
		float[][] tmp = new float[matrix.length][];
		for (int i=0; i<tmp.length; ++i)
			tmp[i] = toFloat(matrix[i]);
		return tmp;
	}
	
	public static void inv(
			final int rows,
			double[][] matrix,
			double[][] matrix_inv) throws IllegalArgumentException
	{
		/*
		 *set lower left triangle to zero and diagonal to one
		 */
		for (int c = 0; c < rows - 1; c++) {			
			double max = Math.abs(matrix[c][c]);
			int max_index = c;
			for (int r = c + 1; r < rows; ++r) {
				assert(matrix[r].length == rows);
				if (Math.abs(matrix[r][c]) > max) {
					max_index = r;
					max = Math.abs(matrix[r][c]);
				}
			}
			
			if (max==0.0)
				throw new IllegalArgumentException("Matrix is singular");
			
			// swap lines
			if (max_index != c) {
				double[] swap = matrix[c];
				matrix[c] = matrix[max_index];
				matrix[max_index] = swap;
				
				swap = matrix_inv[c];
				matrix_inv[c] = matrix_inv[max_index];
				matrix_inv[max_index] = swap;
			}
			
			double scal = 1.0 / matrix[c][c];
			
			mul(matrix[c], c, scal);
			mul(matrix_inv[c], 0, scal);
			
			// scale other lines and substract c-th line
			for (int i = c + 1; i<rows; ++i) {
				double factor = matrix[i][c];
				mulsub(matrix_inv[i], matrix_inv[c], 0, factor);
				mulsub(matrix[i], matrix[c], c, factor);
			}
		}
		
		if (matrix[rows-1][rows-1]==0.0)
			throw new IllegalArgumentException("Matrix is singular");
		
		mul(matrix_inv[rows-1], 0, 1.0/matrix[rows-1][rows-1]);
		matrix[rows-1][rows-1] = 1.0;
		
		for (int r=rows-2; r>=0; --r) {
			for (int index=r+1; index<rows; ++index) {
				mulsub(matrix_inv[r], matrix_inv[index], 0, matrix[r][index]);
				mulsub(matrix[r], matrix[index], r, matrix[r][index]);
			}
		}
	}
	
	private static void mul(double[] x, int index, double alpha) {
		for (; index<x.length; ++index)
			x[index] *= alpha;
	}
	
	private static void mulsub(double[] x, double[] y, int index, double alpha) {
		for (; index<x.length; ++index)
			x[index] -= alpha * y[index];
	}
	
	/**
	 * Determines the covariance matrix from of a set of vectors.
	 *
	 * @param matrix Set of vectors. Vectors are stored row-by-row.
	 *
	 * @return returns covariance matrix
	 */
	public static float[][] cov(float[][] matrix){
		int rows = matrix.length;
		int cols = matrix[0].length;
		
		assert (rows>=cols);
		
		float[] mean = Function.mean_arith(matrix);
		
		double[][] sum = new double[cols][cols];
		{
			float[] diff = new float[cols];
			float diff_i; 
			double[] cov_sum_i; 
			
			for (int r=0; r<rows; ++r) {
				float[] row = matrix[r];
				
				for (int i=0; i<cols; ++i)
					diff[i] = row[i] - mean[i];
				
				for (int i = 0; i < cols; ++i) {
					cov_sum_i  = sum[i];
					diff_i = diff[i];
					for (int j = i; j < cols; ++j)
						cov_sum_i[j] += diff_i * (row[j] - mean[j]);
				}
			}		
		}
		
		float[][] cov = new float[cols][cols];
		for (int i=0; i<cols; ++i) {
			cov[i][i] = (float) sum[i][i] / rows;
			for (int j = i + 1; j < cols; ++j)
				cov[j][i] = cov[i][j] = (float) sum[i][j] / rows;
		}
		
		return cov;
	}
	
	public static float[][] transpose(float[][] matrix) {
		float[][] transposed = new float[matrix[0].length][matrix.length];
		for (int i = 0, i_max = matrix.length; i < i_max; ++i) {
			float[] row = matrix[i];
			for (int j = 0, j_max = row.length; j<j_max; ++j)
				transposed[j][i] = row[j];
		}
		return transposed;
	}
	
	/** Dot Product of two vectors. Vectors must have same length
	 *
	 * @param a first vector
	 * @param b second vector
	 *
	 * @return Dot product of vector a and b.
	 */
	public static float dot(float[] a, float[] b){
		assert a.length == b.length;
		double sum = 0.0;
		for (int i = a.length; i > 0; )
			sum += a[--i] * b[i];
		return (float) sum;
	}
	
	public static double dot(double[] a, double[] b) {
		assert a.length == b.length;    
		double sum = 0.0;
		for (int i = a.length; i > 0; )
			sum += a[--i] * b[i];
		return sum;
	}
	
	/**
	 * Comutes Matrix-Matrix product M1 * M2
	 * 
	 * @param M1 first Matrix (stored row-by-row)
	 * @param M2 second Matrix (stored row-by-row)
	 * @return Returns product M1 * M2
	 */
	public static float[][] mult(float[][] M1, float[][] M2) {
		// store second Matrix column-by-column
		M2 = LinAlg.transpose(M2); 
		
		float[][] prod = new float[M1.length][M2.length]; 
		
		for (int i=0; i<prod.length; ++i) {
			float[] row = prod[i];
			float[] M1i = M1[i]; 
			
			for (int j=0; j<row.length; ++j) {
				assert M1i.length == M2[j].length; 
				row[j] = LinAlg.dot(M1i, M2[j]);
			}
		}
		    
		return prod;
	}
}
