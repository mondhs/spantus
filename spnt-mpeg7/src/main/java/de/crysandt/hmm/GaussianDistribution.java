/*
  Copyright (c) 2005-2006, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

import java.io.*;

public abstract class GaussianDistribution
	implements ObservationDistribution
{
	private final float det;
	
	protected final int SIZE;
	protected final float scal_log;
	
	abstract public float[] getCenter();
	abstract public float[][] getCovarianceInverse();

	public final float getDeterminant() {
		return this.det;
	}
	
	public final int getSize() {
		return this.SIZE;
	}
	
	public GaussianDistribution(int size, float det)
		throws IllegalArgumentException
	{
		if (Float.isInfinite(det))
			throw new IllegalArgumentException(
					"Determinant must not be infinite");

		if (Float.isNaN(det))
			throw new IllegalArgumentException("Determinant is NaN");
		
		if (det<=0)
			throw new IllegalArgumentException("Determinant must be positive");
		
		this.SIZE = size;
		this.det = det;
		
		// update scaling factor
		this.scal_log = -0.5f * (float) (Math.log(2.0 * Math.PI) * SIZE - Math.log(this.det));
		
		assert!Double.isNaN(scal_log);
		assert!Double.isInfinite(scal_log);
	}
	
	public void toMatlab(OutputStream ostream) {
		PrintStream out = new PrintStream(ostream);
		int size = this.getLength();
		float[] center = this.getCenter();
		float[][] cov_inv = this.getCovarianceInverse();
		
		out.print("center = [" + center[0]);
		for (int i = 1; i < size; ++i)
			out.print("," + center[i]);
		out.println("];");
		out.print("cov_inv = [");
		for (int i = 0; i < size; ++i) {
			out.print(cov_inv[i][0]);
			for (int j = 1; j < size; ++j)
				out.print("," + cov_inv[i][j]);
			out.print("" + (i < size - 1 ? ";" : "];"));
		}
		out.println();
		out.println("t=[0:0.001:1]' * 2 * pi;");
		out.println("c = chol(cov_inv);");
		out.println("xy = [sin(t) cos(t)] * inv(c');");
		out.println("plot(center(1), center(2), '+r');");
		out.println("plot(xy(:,1)+center(1), xy(:,2)+center(2), 'r', 'LineWidth', 2);");
	}
	
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		toMatlab(out);
		return out.toString();		
	}
	
	/**
	 * Returns maximum ampliture of the Gaussian distribution which is equal 
	 * to the factor 1/(sqrt(2pi)^m * sqrt(det(Covariance))
	 * 
	 * @return Returns maximum amplitde of distribution
	 */
	public double getMaximumAmplitude() {
		return Math.exp(this.scal_log);
	}
}
