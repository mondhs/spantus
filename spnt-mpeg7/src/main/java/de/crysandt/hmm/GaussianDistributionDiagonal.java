/*
  Copyright (c) 2005-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

public class GaussianDistributionDiagonal
	extends GaussianDistribution
{
	private final float[] mean; 
	private final float[] var_inv;
	
	public GaussianDistributionDiagonal(float[] mean, float[] var_inv) {
		super(mean.length, prod(var_inv));

		this.mean= new float[this.SIZE];
		this.var_inv = new float[this.SIZE]; 

		// copy center
		System.arraycopy(mean, 0, this.mean, 0, SIZE);
		
		// copy diagonal of inverse covariance matrix
		assert(var_inv.length == this.SIZE);
		System.arraycopy(var_inv, 0, this.var_inv, 0, this.SIZE);
	}
	
	private static float prod(float[] vector) {
		float prod = 1.0f;
		for (int i=0; i<vector.length; ++i)
			prod *= vector[i]; 
		return prod;
	}

	/**
	 * @deprecated use getSize instead
	 */
	@SuppressWarnings("dep-ann")
	public int getLength() {
		return SIZE;
	}

	public double getProb(float[] vector) {
		return Math.exp(getLogProb(vector));
	}

	public double getLogProb(float[] vector) {
		double sum = 0.0;
		float diff; 
		for (int i=0; i<vector.length; ++i) {
			diff = vector[i] - mean[i]; 
			sum += diff * diff * var_inv[i];
		}
			
		return -sum/2.0 + this.scal_log; 
	}

	
	public float[] getCenter() {
		return this.mean;
	}
	
	public float[] getVarianceInverse() {
		float[] vi = new float[this.SIZE];
		System.arraycopy(this.var_inv, 0, vi, 0, this.SIZE);
		return vi;
	}

	
	public float[][] getCovarianceInverse() {
		float[][] cov_inv = new float[this.SIZE][this.SIZE];
		
		for (int i=0; i<this.SIZE; ++i)
			cov_inv[i][i] = this.var_inv[i]; 
		
		return cov_inv;
	}
}
