/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.math;

/**
 * LU Decomposition.<p/>
 *
 * Taken from <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
 * implementation.
 */

final public class LUDecomposition {
  private final int rows;
  private final int cols;

  private final double[][] LU;

  private final int[] piv;
  private int pivsign = 1;

	/**
	 * @param matrix float[][] matrix stored row-by-row
	 */
  public LUDecomposition(double[][] matrix) {
    rows = matrix.length;
    cols = matrix[0].length;

    LU = new double[rows][cols];

    piv = new int[rows];
    for (int i = 0; i < rows; ++i) {
      System.arraycopy(matrix[i], 0, LU[i], 0, cols);
      piv[i] = i;
    }

    double[] LUcolj = new double[rows];

    // Outer loop.
    for (int j = 0; j < cols; j++) {

      // Make a copy of the j-th column to localize references.
      for (int i = 0; i < rows; i++)
        LUcolj[i] = LU[i][j];

      // Apply previous transformations.
      for (int i = 0; i < rows; i++) {
        double[] LUrowi = LU[i];

        // Most of the time is spent in the following dot product.
        double s = 0.0f;
        for (int k = 0, kmax = Math.min(i,j); k < kmax; ++k)
          s += LUrowi[k]*LUcolj[k];

        LUrowi[j] = (LUcolj[i] -= s);
      }

      // Find pivot and exchange if necessary.
      int p = j;
      for (int i = j+1; i < rows; ++i) {
        if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p]))
          p = i;
      }

      if (p != j) {
      	double t; 
      	for (int k = 0; k < cols; ++k) {
      		t = LU[p][k];
      		LU[p][k] = LU[j][k];
      		LU[j][k] = t;
      	}
      	int k = piv[p];
      	piv[p] = piv[j];
      	piv[j] = k;
      	pivsign = -pivsign;
      }

      // Compute multipliers.
      if (j < rows & LU[j][j] != 0.0f)
        for (int i = j+1; i < rows; i++)
          LU[i][j] /= LU[j][j];
    }
  }

  public double[][] getL () {
    double[][] X = new double[rows][cols];

    for (int i = 0; i < rows; ++i) {
      double[] X_i = X[i];
      double[] LU_i = LU[i];
      for (int j = 0, j_max = Math.min(i, cols); j < j_max; ++j)
        X_i[j] = LU_i[j];
      if (i<cols)
        X_i[i] = 1.0f;
    }

    return X;
	}

  public double[][] getU() {
    double[][] X = new double[cols][cols];

    for (int i = 0; i < cols; i++) {
      double[] X_i = X[i];
      double[] LU_i = LU[i];
      for (int j = i; j < cols; j++)
        X_i[j] = LU_i[j];
    }

    return X;
  }

	public int[] getPiv() {
		int[] p = new int[piv.length];
		System.arraycopy(piv,0,p,0,p.length);
		return p;
	}

	public double det() {
		assert(rows == cols);

		double d = pivsign;
		for (int j = 0; j < cols; j++)
			d *= LU[j][j];

		return d;
	}
}
