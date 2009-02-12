/*
  Copyright (c) 2005-2006, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

import de.crysandt.math.LinAlg;

/**
 * Stores center and inverse covariance matrix of a multidimensional gaussian
 * distribution.
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class GaussianDistributionFull
	extends GaussianDistribution
{
	/**
	 * number of simensions (Set by constructor)
	 */
	private final float[] center;
	private final float[][] cov_inv;
	
	/**
	 * Create a new Gaussian distribution with center and inverse covariance matrix. 
	 * Determinant must match to inverse covariance matrix. If determinant is unknown use other 
	 * constructor
	 *  
	 * @param center Center of distribution (mu)
	 * @param cov_inv Inverse covariance matrix
	 * @param det Determinant of inverse covariance matrix
	 * 
	 * @see GaussianDistributionFull#GaussianDistributionFull(float[], float[][])
	 */
	public GaussianDistributionFull(float[] center, float[][] cov_inv, float det) {
		super(center.length, det);
		
		this.center = new float[this.SIZE];
		this.cov_inv = new float[this.SIZE][this.SIZE];

		// set center
		System.arraycopy(center, 0, this.center, 0, SIZE);		

		// set inverse covariance matrix
		for (int i = 0; i < SIZE; ++i)
			System.arraycopy(cov_inv[i], 0, this.cov_inv[i], 0, SIZE);		
	}

	public GaussianDistributionFull(float[] center, float[][] cov_inv) {
		this(center, cov_inv, (float) LinAlg.det(cov_inv));
	}

//	private static void testDeterminant(float det) {
//		if (Float.isInfinite(det))
//			throw new IllegalArgumentException("Determinant is infinite");
//		else if (det < 0.0f)
//			throw new IllegalArgumentException(
//					"Determinant of inverse covariance matrix must " +
//					"be positive (is " + det + ")");
//		else if (det == 0.0f)
//			throw new IllegalArgumentException("Covariance matrix is singular");
//		else if (Float.isNaN(det))
//			throw new IllegalArgumentException("Determinant is not a number (NaN)");
//	}

	public double getProb(float[] x) {
		double arg = getLogProb(x);
		
		assert ! Double.isInfinite(arg);
		assert ! Double.isNaN(arg);
		
		double value = Math.exp(arg);
		
		assert ! Double.isNaN(value);
		assert ! Double.isInfinite(value);
		
		return value;
	}

	public double getLogProb(float[] x) {
		assert(this.SIZE == x.length);
		
		float[] diff = new float[SIZE];
		for (int i = 0; i < this.SIZE; ++i)
			diff[i] = x[i] - center[i];
		
		double sum = 0.0;
		for (int i = 0; i < SIZE; ++i)
			sum += LinAlg.dot(cov_inv[i], diff) * diff[i];

		// numerical instability (sum must not be negative!)
		if (sum < 0.0)
			sum = 0.0;
		
		assert ! Double.isInfinite(sum);
		assert ! Double.isNaN(sum);
		
		return -sum / 2.0 + scal_log;
	}

	/**
	 * @deprecated use getSize instead
	 */
	public int getLength() {
		return SIZE;
	}

	/**
	 * Returns copy of the center vector
	 *
	 * @return copy of center vector
	 */
	public float[] getCenter() {
		float[] tmp = new float[SIZE];
		System.arraycopy(center, 0, tmp, 0, SIZE);
		return tmp;
	}

	/**
	 * Returns copy of the inverse covariance matrix
	 *
	 * @return copy of inverse covariance matrix
	 */
	public float[][] getCovarianceInverse() {
		float[][] tmp = new float[SIZE][SIZE];
		for (int i = 0; i < SIZE; ++i)
			System.arraycopy(cov_inv[i], 0, tmp[i], 0, SIZE);
		return tmp;
	}
}
