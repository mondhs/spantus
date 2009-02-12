/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.math;

import java.util.Arrays;

/**  SVD - Singular Value Decomposition.
 * <P>
 * For an m-by-n matrix A with m >= n, the singular value decomposition is
 * an m-by-n orthogonal matrix U, an n-by-n diagonal matrix S, and
 * an n-by-n orthogonal matrix V so that A = U*S*V'.
 * <P>
 * The singular values, sigma[k] = S[k][k], are ordered so that
 * sigma[0] >= sigma[1] >= ... >= sigma[n-1].
 * <P>
 * The singular value decompostion always exists, so the constructor will
 * never fail.  The matrix condition number and the effective numerical
 * rank can be computed from this decomposition.<p/>
 *  Added by Felix Engel - taken from
 *  <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
 *  reference implementation.
 *
 * @author Felix Engel,
 *           <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */

public final class SVD {
	
	/* ------------------------
	 Class variables
	 * ------------------------ */
	
	/** Arrays for internal storage of U and V.
	 @serial internal storage of U.
	 @serial internal storage of V.
	 */
	private float[][] U, V;
	
	/** Array for internal storage of singular values.
	 @serial internal storage of singular values.
	 */
	private float[] s;
	
	/** Row and column dimensions.
	 @serial row dimension.
	 @serial column dimension.
	 */
	private int m, n;
	
	/** Construct the singular value decomposition
	 * @param X    Rectangular matrix
	 * @param rows Number of rows
	 * @param cols Number of columns
	 */
	
	public SVD(float[][] X,int rows, int cols) {
		this(X, rows, cols, true, true);
	}
	
	public SVD(float[][] X,int rows, int cols, boolean WANTU, boolean WANTV) {
		this.m = rows; // rows
		this.n = cols; // columns
		
		float[][] A = new float[m][n];
		for (int i = 0; i < m; i++)
			System.arraycopy(X[i], 0, A[i], 0, n);
		
		// Derived from LINPACK code.
		// Initialize.
		final int nu = Math.min(m,n);
		s = new float [Math.min(m+1,n)];
		U = WANTU ? new float [m][nu] : null;
		V = WANTV ? new float [n][n] : null;
		
		// create some local matrices
	
		double[] e = new double [n];
		double[] work = new double [m];
		
		// Reduce A to bidiagonal form, storing the diagonal elements
		// in s and the super-diagonal elements in e.
		
		int nct = Math.min(m-1,n);
		int nrt = Math.max(0,Math.min(n-2,m));
		for (int k = 0, k_max=Math.max(nct,nrt); k < k_max; k++) {
			if (k < nct) {
				
				// Compute the transformation for the k-th column and
				// place the k-th diagonal in s[k].
				// Compute 2-norm of k-th column without under/overflow.
				double sk = 0;
				for (int i = k; i < m; i++) {
					sk = hypot(sk,A[i][k]);
				}
				
				if (sk != 0.0) {
					if (A[k][k] < 0.0f) {
						sk = -sk;
					}
					for (int i = k; i < m; i++) {
						A[i][k] /= sk;
					}
					A[k][k] += 1.0f;
				}
				s[k] = (float) -sk;
			}
			
			for (int j = k+1; j < n; j++) {
				if ((k < nct) & (s[k] != 0.0f))  {
					
					// Apply the transformation.
					
					double t = 0;
					for (int i = k; i < m; i++) {
						t += A[i][k]*A[i][j];
					}
					t /= -A[k][k];
					for (int i = k; i < m; i++) {
						A[i][j] += t*A[i][k];
					}
				}
				
				// Place the k-th row of A into e for the
				// subsequent calculation of the row transformation.
				
				e[j] = A[k][j];
			}
			if (WANTU & (k < nct)) {
				
				// Place the transformation in U for subsequent back
				// multiplication.
				
				for (int i = k; i < m; i++) {
					U[i][k] = A[i][k];
				}
			}
			if (k < nrt) {
				
				// Compute the k-th row transformation and place the
				// k-th super-diagonal in e[k].
				// Compute 2-norm without under/overflow.
				double ek = 0;
				for (int i = k+1; i < n; i++) {
					ek = hypot(ek,e[i]);
				}
				e[k] = ek;
				
				if (e[k] != 0.0f) {
					if (e[k+1] < 0.0f) {
						e[k] = -e[k];
					}
					for (int i = k+1; i < n; i++) {
						e[i] /= e[k];
					}
					e[k+1] += 1.0f;
				}
				
				e[k] = -e[k];
				if ((k+1 < m) & (e[k] != 0.0f)) {
					
					// Apply the transformation.
					
					Arrays.fill(work, k+1, m, 0.0f);
					for (int j = k+1; j < n; j++) {
						double e_j = e[j];
						for (int i = k+1; i < m; i++) {
							work[i] += e_j*A[i][j];
						}
					}
					for (int j = k+1; j < n; j++) {
						double t = -e[j]/e[k+1];
						for (int i = k+1; i < m; i++) {
							A[i][j] += t*work[i];
						}
					}
				}
				if (WANTV) {
					
					// Place the transformation in V for subsequent
					// back multiplication.
					
					for (int i = k+1; i < n; i++) {
						V[i][k] = (float) e[i];
					}
				}
			}
		}
		
		// Set up the final bidiagonal matrix or order p.
		
		int p = Math.min(n,m+1);
		if (nct < n) {
			s[nct] = A[nct][nct];
		}
		if (m < p) {
			s[p-1] = 0.0f;
		}
		if (nrt+1 < p) {
			e[nrt] = A[nrt][p-1];
		}
		e[p-1] = 0.0f;
		
		// If required, generate U.
		
		if (WANTU) {
			for (int j = nct; j < nu; j++) {
				for (int i = 0; i < m; i++) {
					U[i][j] = 0.0f;
				}
				U[j][j] = 1.0f;
			}
			for (int k = nct-1; k >= 0; k--) {
				if (s[k] != 0.0) {
					for (int j = k+1; j < nu; j++) {
						double t = 0;
						for (int i = k; i < m; i++) {
							t += U[i][k]*U[i][j];
						}
						t /= -U[k][k];
						for (int i = k; i < m; i++) {
							U[i][j] += t*U[i][k];
						}
					}
					for (int i = k; i < m; i++ ) {
						U[i][k] = -U[i][k];
					}
					U[k][k] += 1.0f;
					for (int i = 0; i < k-1; i++) {
						U[i][k] = 0.0f;
					}
				} else {
					for (int i = 0; i < m; i++) {
						U[i][k] = 0.0f;
					}
					U[k][k] = 1.0f;
				}
			}
		}
		
		// If required, generate V.
		
		if (WANTV) {
			for (int k = n-1; k >= 0; k--) {
				if ((k < nrt) & (e[k] != 0.0)) {
					for (int j = k+1; j < nu; j++) {
						double t = 0;
						for (int i = k+1; i < n; i++) {
							t += V[i][k]*V[i][j];
						}
						t /= -V[k+1][k];
						for (int i = k+1; i < n; i++) {
							V[i][j] += t*V[i][k];
						}
					}
				}
				for (int i = 0; i < n; i++) {
					V[i][k] = 0.0f;
				}
				V[k][k] = 1.0f;
			}
		}
		
		// Main iteration loop for the singular values.
		
		int pp = p-1;
		int iter = 0;
		double eps = Float.MIN_VALUE;
		while (p > 0) {
			int k,kase;
			
			// Here is where a test for too many iterations would go.
			
			// This section of the program inspects for
			// negligible elements in the s and e arrays.  On
			// completion the variables kase and k are set as follows.
			
			// kase = 1     if s(p) and e[k-1] are negligible and k<p
			// kase = 2     if s(k) is negligible and k<p
			// kase = 3     if e[k-1] is negligible, k<p, and
			//              s(k), ..., s(p) are not negligible (qr step).
			// kase = 4     if e(p-1) is negligible (convergence).
			
			for (k = p-2; k >= -1; k--) {
				if (k == -1) {
					break;
				}
				if (Math.abs(e[k]) <= eps*(Math.abs(s[k]) + Math.abs(s[k+1]))) {
					e[k] = 0.0f;
					break;
				}
			}
			if (k == p-2) {
				kase = 4;
			} else {
				int ks;
				for (ks = p-1; ks >= k; ks--) {
					if (ks == k) {
						break;
					}
					double t = (ks != p ? Math.abs(e[ks]) : 0.) +
					(ks != k+1 ? Math.abs(e[ks-1]) : 0.);
					if (Math.abs(s[ks]) <= eps*t)  {
						s[ks] = 0.0f;
						break;
					}
				}
				if (ks == k) {
					kase = 3;
				} else if (ks == p-1) {
					kase = 1;
				} else {
					kase = 2;
					k = ks;
				}
			}
			k++;
			
			// Perform the task indicated by kase.
			
			switch (kase) {
				
				// Deflate negligible s(p).
				
				case 1: {
					double f = e[p-2];
					e[p-2] = 0.0f;
					for (int j = p-2; j >= k; j--) {
						double t = hypot(s[j],f);
						double cs = s[j]/t;
						double sn = f/t;
						s[j] = (float) t;
						if (j != k) {
							f = -sn*e[j-1];
							e[j-1] = cs*e[j-1];
						}
						if (WANTV) {
							for (int i = 0; i < n; i++) {
								t = cs*V[i][j] + sn*V[i][p-1];
								V[i][p-1] = (float) (-sn*V[i][j] + cs*V[i][p-1]);
								V[i][j] = (float) t;
							}
						}
					}
				}
				break;
				
				// Split at negligible s(k).
				
				case 2: {
					double f = e[k-1];
					e[k-1] = 0.0f;
					for (int j = k; j < p; j++) {
						double t = hypot(s[j],f);
						double cs = s[j]/t;
						double sn = f/t;
						s[j] = (float) t;
						f = -sn*e[j];
						e[j] *= cs;
						if (WANTU) {
							for (int i = 0; i < m; i++) {
								t = cs*U[i][j] + sn*U[i][k-1];
								U[i][k-1] = (float) (-sn*U[i][j] + cs*U[i][k-1]);
								U[i][j] = (float) t;
							}
						}
					}
				}
				break;
				
				// Perform one qr step.
				
				case 3: {
					
					// Calculate the shift.
					
					double scale = Math.max(Math.max(Math.max(Math.max(
							Math.abs(s[p-1]),Math.abs(s[p-2])),Math.abs(e[p-2])),
							Math.abs(s[k])),Math.abs(e[k]));
					double sp = s[p-1]/scale;
					double spm1 = s[p-2]/scale;
					double epm1 = e[p-2]/scale;
					double sk = s[k]/scale;
					double ek = e[k]/scale;
					double b = ((spm1 + sp)*(spm1 - sp) + epm1*epm1)/2.0f;
					double c = (sp*epm1)*(sp*epm1);
					double shift = 0.0f;
					if ((b != 0.0f) | (c != 0.0f)) {
						shift = (float) Math.sqrt(b*b + c);
						if (b < 0.0) {
							shift = -shift;
						}
						shift = c/(b + shift);
					}
					double f = (sk + sp)*(sk - sp) + shift;
					double g = sk*ek;
					
					// Chase zeros.
					
					for (int j = k; j < p-1; j++) {
						double t = hypot(f,g);
						double cs = f/t;
						double sn = g/t;
						if (j != k) {
							e[j-1] = t;
						}
						f = cs*s[j] + sn*e[j];
						e[j] = cs*e[j] - sn*s[j];
						g = sn*s[j+1];
						s[j+1] *= cs;
						if (WANTV) {
							for (int i = 0; i < n; i++) {
								t = cs*V[i][j] + sn*V[i][j+1];
								V[i][j+1] = (float) (-sn*V[i][j] + cs*V[i][j+1]);
								V[i][j] = (float) t;
							}
						}
						t = hypot(f,g);
						cs = f/t;
						sn = g/t;
						s[j] = (float) t;
						f = cs*e[j] + sn*s[j+1];
						s[j+1] = (float) (-sn*e[j] + cs*s[j+1]);
						g = sn*e[j+1];
						e[j+1] *= cs;
						if (WANTU && (j < m-1)) {
							for (int i = 0; i < m; i++) {
								t = cs*U[i][j] + sn*U[i][j+1];
								U[i][j+1] = (float) (-sn*U[i][j] + cs*U[i][j+1]);
								U[i][j] = (float) t;
							}
						}
					}
					e[p-2] = f;
					iter = iter + 1;
				}
				break;
				
				// Convergence.
				
				case 4: {
					
					// Make the singular values positive.
					
					if (s[k] <= 0.0) {
						s[k] = (s[k] < 0.0f ? -s[k] : 0.0f);
						if (WANTV) {
							for (int i = 0; i <= pp; i++) {
								V[i][k] = -V[i][k];
							}
						}
					}
					
					// Order the singular values.
					
					for ( ; k < pp; ++k) {
						if (s[k] >= s[k+1]) {
							break;
						}
						float t = s[k];
						s[k] = s[k+1];
						s[k+1] = t;
						if (WANTV && (k < n-1)) {
							for (int i = 0; i < n; i++) {
								t = V[i][k+1];
								V[i][k+1] = V[i][k];
								V[i][k] = t;
							}
						}
						if (WANTU && (k < m-1)) {
							for (int i = 0; i < m; i++) {
								t = U[i][k+1];
								U[i][k+1] = U[i][k];
								U[i][k] = t;
							}
						}
					}
					iter = 0;
					p--;
				}
				break;
			}
		}
	}
	
	/** sqrt(a^2 + b^2) without under/overflow.<p/>
	 *  Added by Felix Engel - taken from
	 *  <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
	 *  reference implementation.
	 */
	private static double hypot(double a, double b) {
		if (Math.abs(a) > Math.abs(b)) {
			double r = b / a;
			return Math.abs(a) * Math.sqrt(1 + r * r);
		} else if (b != 0) {
			double r = a / b;
			return Math.abs(b) * Math.sqrt(1 + r * r);
		} else {
			return 0.0;
		}
	}
	
	public float[][] getS(){
		float[][] S = new float[n][n];
		for(int i=0;i<n;i++){
			S[i][i] = s[i];
		}
		return S;
	}
	
	public float[][] getV(){
		if (this.V == null)
			throw new IllegalArgumentException("Matrix V not computed.");
			
		float[][] temp = new float[n][n];
		for(int i=0;i<n;i++){
			System.arraycopy(V[i], 0, temp[i], 0, n);
		}
		return temp;
	}
	
	public float[][] getU() {
		if (this.U == null)
			throw new IllegalArgumentException("Matrix U not computed.");
			
		float[][] temp = new float[m][n];
		for (int i = 0; i < m; i++)
			System.arraycopy(U[i], 0, temp[i], 0, n);
		return temp;
	}
}
