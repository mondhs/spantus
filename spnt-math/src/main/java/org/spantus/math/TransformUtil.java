/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.math;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created 2008.02.20
 * 
 */
public class TransformUtil {
	static final int MAXLOGM = 20; /* max FFT length 2^MAXLOGM */
	static final double SQHALF = 0.707106781186547524401;
	static final  double TWOPI=6.28318530717958647692;


	/** Calculates the power (magnitude squared) spectrum of a real signal.
	  * The returned vector contains only the positive frequencies.
	  */
	  public List<Float> calculateFFTPower(List<Float> x) {
			int i,n;
			int logm = (int) (Math.log(x.size()) / Math.log(2));
			n=1<<logm;

			

			rsfft(new ArrayList<Float>(x),logm);

			//System.out.println("FFT before magnitude");
			//IO.DisplayVector(x);

			List<Float> mag = new ArrayList<Float>(n/2 + 1);
			mag.add( x.get(0)); //DC frequency must be positive always

			if (n==1) {
				return mag;
			}
			mag.set(n/2, Math.abs(x.get(n/2))); //pi (meaning: fs / 2)

			for (i=1;i<n/2;i++) {
				//mag[i] = x[i]^2+x[n-i]^2
				mag.set(i, new Float(Math.pow(x.get(i), 2) +Math.pow(x.get((n-i)), 2)));
			}

			//IO.DisplayVector(mag);
			return mag;
	  }
	
	
	public static List<Float> calculateFFTMagnitude(List<Float> x) {
		int i, n;
		int logm = (int) (Math.log(x.size()) / Math.log(2));
		n = 1 << logm;

		if (x.size() > n) {
			throw new IllegalArgumentException("Tried to use a " + n
					+ "-points FFT for a vector with " + x.size() + " samples!");
		}

		rsfft(x, logm);

		List<Float> mag = MatrixUtils.zeros(n / 2 + 1);// new float[n / 2 + 1];

		mag.add(0, x.get(0)); // DC frequency must be positive always

		if (n == 1) {
			return mag;
		}
		mag.set(n / 2, (float) Math.abs(x.get(n / 2))); // pi (meaning: fs / 2)

		// System.out.println("FFT before magnitude");
		// IO.DisplayVector(x);

		for (i = 1; i < n / 2; i++) {
			mag.set(i, (float) Math.sqrt(x.get(i) * x.get(i) + x.get(n - i)
					* x.get(n - i)));
			// System.out.println(mag[i] + " " + x[i] + " " + x[n-i]);
		}

		// IO.DisplayVector(mag);
		return mag;

	}

	private static void rsfft(List<Float> x, int logm) {

		float tab[][] = creattab(logm);
		rsrec(x, tab, logm);

		/* Output array unshuffling using bit-reversed indices */
		if (logm > 1) {
			BR_permute(x, logm);
			return;
		}
	}

	private static void rsrec(List<Float> x, float tab[][], int logm) {
		int m, m2, m4, m8, nel, n;
		int x0 = 0;
		int xr1, xr2, xi1;
		int cn = 0;
		int spcn = 0;
		int smcn = 0;
		float tmp1, tmp2;

		/* Check range of logm */
		if ((logm < 0) || (logm > MAXLOGM)) {
			System.err.println("FFT length m is too big: log2(m) = " + logm
					+ "is out of bounds [" + 0 + "," + MAXLOGM + "]");
			throw new IllegalArgumentException("Out of Border Exception: "
					+ logm);
		}

		/* Compute trivial cases */

		if (logm < 2) {
			if (logm == 1) { /* length m = 2 */
				xr2 = x0 + 1;
				tmp1 = x.get(x0) + x.get(xr2);
				x.set(xr2, new Float(x.get(x0) - x.get(xr2)));
				x.set(x0, tmp1);
				return;
			} else if (logm == 0)
				return; /* length m = 1 */
		}

		/* Compute a few constants */
		m = 1 << logm;
		m2 = m / 2;
		m4 = m2 / 2;
		m8 = m4 / 2;

		/* Build tables of butterfly coefficients, if necessary */
		// if ((logm >= 4) && (tab[logm-4][0] == 0)) {
		/* Allocate memory for tables */
		// nel = m4 - 2;
		/*
		 * if ((tab[logm-4] = (float *) calloc(3 * nel, sizeof(float))) == NULL) {
		 * printf("Error : RSFFT : not enough memory for cosine tables.\n");
		 * error_exit(); }
		 */

		/* Initialize pointers */
		// tabi=logm-4;
		// cn =0; spcn = cn + nel; smcn = spcn + nel;
		/* Compute tables */
		/*
		 * for (n = 1; n < m4; n++) { if (n == m8) continue; ang = n *
		 * (float)TWOPI / m; c = Math.cos(ang); s = Math.sin(ang);
		 * tab[tabi][cn++] = (float)c; tab[tabi][spcn++] = (float)(- (s + c));
		 * tab[tabi][smcn++] =(float)( s - c); } } /* Step 1
		 */
		xr1 = x0;
		xr2 = xr1 + m2;
		for (n = 0; n < m2; n++) {
			tmp1 = x.get(xr1) + x.get(xr2);
			x.set(xr2, new Float(x.get(xr1) - x.get(xr2)));
			x.set(xr1, tmp1);
			xr1++;
			xr2++;
		}

		/* Step 2 */
		xr1 = x0 + m2 + m4;
		for (n = 0; n < m4; n++) {
			x.set(xr1, new Float(-x.get(xr1)));
			xr1++;
		}

		/* Steps 3 & 4 */
		xr1 = x0 + m2;
		xi1 = xr1 + m4;
		if (logm >= 4) {
			nel = m4 - 2;
			cn = 0;
			spcn = cn + nel;
			smcn = spcn + nel;
		}

		xr1++;
		xi1++;
		for (n = 1; n < m4; n++) {
			if (n == m8) {
				tmp1 = (float) (SQHALF * (x.get(xr1) + x.get(xi1)));
				x.set(xi1, (float) (SQHALF * (x.get(xi1) - x.get(xr1))));
				x.set(xr1, tmp1);
			} else {// System.out.println ("logm-4="+(logm-4));
				tmp2 = tab[logm - 4][cn++] * (x.get(xr1) + x.get(xi1));
				tmp1 = tab[logm - 4][spcn++] * x.get(xr1) + tmp2;
				x.set(xr1, tab[logm - 4][smcn++] * x.get(xi1) + tmp2);
				x.set(xi1, tmp1);
			}
			// System.out.println ("logm-4="+(logm-4));
			xr1++;
			xi1++;
		}

		/* Call rsrec again with half DFT length */
		rsrec(x, tab, logm - 1);

		/*
		 * Call complex DFT routine, with quarter DFT length. Constants have to
		 * be recomputed, because they are static!
		 */
		m = 1 << logm;
		m2 = m / 2;
		m4 = 3 * (m / 4);
		srrec(x, x0 + m2, x0 + m4, tab, logm - 2);

		/* Step 5: sign change & data reordering */
		m = 1 << logm;
		m2 = m / 2;
		m4 = m2 / 2;
		m8 = m4 / 2;
		xr1 = x0 + m2 + m4;
		xr2 = x0 + m - 1;
		for (n = 0; n < m8; n++) {
			tmp1 = x.get(xr1);
			x.set(xr1++, -x.get(xr2));
			x.set(xr2--, -tmp1);
		}
		xr1 = x0 + m2 + 1;
		xr2 = x0 + m - 2;
		for (n = 0; n < m8; n++) {
			tmp1 = x.get(xr1);
			x.set(xr1++, -x.get(xr2));
			x.set(xr2--, tmp1);
			xr1++;
			xr2--;
		}
		if (logm == 2)
			x.set(3, -x.get(3));
	}

	/**
	 * 
	 * @param x
	 * @param logm
	 */
	private static void BR_permute(List<Float> x, int logm) {
		int i, j, lg2, n;
		int off, fj, gno;
		float tmp;
		int xp, xq, brp;
		int x0 = 0;

		lg2 = logm >> 1;
		n = 1 << lg2;
		if (logm != (logm >> 1) << 1)
			lg2++;

		/* Create seed table if not yet built */
		/*
		 * if (brsflg != logm) { brsflg = logm; brseed[0] = 0; brseed[1] = 1;
		 * for (j=2; j <= lg2; j++) { imax = 1 << (j-1); for (i = 0; i < imax;
		 * i++) { brseed[i] <<= 1; brseed[i + imax] = brseed[i] + 1; } } }
		 */
		int[] brseed = creatbrseed(logm);

		/* Unshuffling loop */
		for (off = 1; off < n; off++) {
			fj = n * brseed[off];
			i = off;
			j = fj;
			tmp = x.get(i);
			x.set(i, x.get(j));
			x.set(j, tmp);
			xp = i;
			brp = 1;

			for (gno = 1; gno < brseed[off]; gno++) {
				xp += n;
				j = fj + brseed[brp++];
				xq = x0 + j;
				tmp = x.get(xp);
				x.set(xp, x.get(xq));
				x.set(xq, tmp);
			}
		}

	}

	private static int[] creatbrseed(int logm) {
		int brseed[] = new int[4048];
		int lg2;
		lg2 = logm >> 1;
		if (logm != (logm >> 1) << 1)
			lg2++;
		brseed[0] = 0;
		brseed[1] = 1;
		for (int j = 2; j <= lg2; j++) {
			int imax = 1 << (j - 1);
			for (int i = 0; i < imax; i++) {
				brseed[i] <<= 1;
				brseed[i + imax] = brseed[i] + 1;
			}
		}
		return brseed;
	}

	/**
	 * 
	 * @param x
	 * @param xr
	 * @param xi
	 * @param logm
	 */
	private static void srrec(List<Float> x, int xr, int xi, float tab[][],
			int logm) {
		int m, m2, m4, m8, nel, n;
		// int x0=0;
		int xr1, xr2, xi1, xi2;
		int cn, spcn, smcn, c3n, spc3n, smc3n;
		float tmp1, tmp2;
		cn = 0;
		spcn = 0;
		smcn = 0;
		c3n = 0;
		spc3n = 0;
		smc3n = 0;

		/* Check range of logm */

		if ((logm < 0) || (logm > MAXLOGM)) {
			System.err.println("FFT length m is too big: log2(m) = " + logm
					+ "is out of bounds [" + 0 + "," + MAXLOGM + "]");
			throw new IllegalArgumentException("Out of Border Exception: "
					+ logm);
		}

		/* Compute trivial cases */
		if (logm < 3) {
			if (logm == 2) { /* length m = 4 */
				xr2 = xr + 2;
				xi2 = xi + 2;
				tmp1 = x.get(xr) + x.get(xr2);
				x.set(xr2, x.get(xr) - x.get(xr2));
				x.set(xr, tmp1);
				tmp1 = x.get(xi) + x.get(xi2);
				x.set(xi2, x.get(xi) - x.get(xi2));
				x.set(xi, tmp1);
				xr1 = xr + 1;
				xi1 = xi + 1;
				xr2++;
				xi2++;
				tmp1 = x.get(xr1) + x.get(xr2);
				x.set(xr2, x.get(xr1) - x.get(xr2));
				x.set(xr1, tmp1);
				tmp1 = x.get(xi1) + x.get(xi2);
				x.set(xi2, x.get(xi1) - x.get(xi2));
				x.set(xi1, tmp1);
				xr2 = xr + 1;
				xi2 = xi + 1;
				tmp1 = x.get(xr) + x.get(xr2);
				x.set(xr2, x.get(xr) - x.get(xr2));
				x.set(xr, tmp1);
				tmp1 = x.get(xi) + x.get(xi2);
				x.set(xi2, x.get(xi) - x.get(xi2));
				x.set(xi, tmp1);
				xr1 = xr + 2;
				xi1 = xi + 2;
				xr2 = xr + 3;
				xi2 = xi + 3;
				tmp1 = x.get(xr1) + x.get(xi2);
				tmp2 = x.get(xi1) + x.get(xr2);
				x.set(xi1, x.get(xi1) - x.get(xr2));
				x.set(xr2, x.get(xr1) - x.get(xi2));
				x.set(xr1, tmp1);
				x.set(xi2, tmp2);
				return;
			}

			else if (logm == 1) { /* length m = 2 */
				xr2 = xr + 1;
				xi2 = xi + 1;
				tmp1 = x.get(xr) + x.get(xr2);
				x.set(xr2, x.get(xr) - x.get(xr2));
				x.set(xr, tmp1);
				tmp1 = x.get(xi) + x.get(xi2);
				x.set(xi2, x.get(xi) - x.get(xi2));
				x.set(xi, tmp1);
				return;
			} else if (logm == 0)
				return; /* length m = 1 */
		}

		/* Compute a few constants */
		m = 1 << logm;
		m2 = m / 2;
		m4 = m2 / 2;
		m8 = m4 / 2;

		/* Build tables of butterfly coefficients, if necessary */
		// if ((logm >= 4) && (tab[logm-4] == NULL)) {
		/* Allocate memory for tables */
		/*
		 * nel = m4 - 2; if ((tab[logm-4] = (float *) calloc(6 * nel,
		 * sizeof(float))) == NULL) { exit(1); } /* Initialize pointers
		 */

		/*
		 * cn = tab[logm-4]; spcn = cn + nel; smcn = spcn + nel; c3n = smcn +
		 * nel; spc3n = c3n + nel; smc3n = spc3n + nel;
		 * 
		 *  /* Compute tables
		 */
		/*
		 * for (n = 1; n < m4; n++) { if (n == m8) continue; ang = n * TWOPI /
		 * m; c = cos(ang); s = sin(ang); cn++ = c; *spcn++ = - (s + c); *smcn++ =
		 * s - c; ang = 3 * n * TWOPI / m; c = cos(ang); s = sin(ang); c3n++ =
		 * c; *spc3n++ = - (s + c); *smc3n++ = s - c; } }
		 * 
		 *  /* Step 1
		 */
		xr1 = xr;
		xr2 = xr1 + m2;
		xi1 = xi;
		xi2 = xi1 + m2;

		for (n = 0; n < m2; n++) {
			tmp1 = x.get(xr1) + x.get(xr2);
			x.set(xr2, x.get(xr1) - x.get(xr2));
			x.set(xr1, tmp1);
			tmp2 = x.get(xi1) + x.get(xi2);
			x.set(xi2, x.get(xi1) - x.get(xi2));
			x.set(xi1, tmp2);
			xr1++;
			xr2++;
			xi1++;
			xi2++;
		}
		/* Step 2 */
		xr1 = xr + m2;
		xr2 = xr1 + m4;
		xi1 = xi + m2;
		xi2 = xi1 + m4;
		for (n = 0; n < m4; n++) {
			tmp1 = x.get(xr1) + x.get(xi2);
			tmp2 = x.get(xi1) + x.get(xr2);
			x.set(xi1, x.get(xi1) - x.get(xr2));
			x.set(xr2, x.get(xr1) - x.get(xi2));
			x.set(xr1, tmp1);
			x.set(xi2, tmp2);
			xr1++;
			xr2++;
			xi1++;
			xi2++;
		}

		/* Steps 3 & 4 */
		xr1 = xr + m2;
		xr2 = xr1 + m4;
		xi1 = xi + m2;
		xi2 = xi1 + m4;
		if (logm >= 4) {
			nel = m4 - 2;
			cn = 0;
			spcn = cn + nel;
			smcn = spcn + nel;
			c3n = smcn + nel;
			spc3n = c3n + nel;
			smc3n = spc3n + nel;
		}
		xr1++;
		xr2++;
		xi1++;
		xi2++;
		for (n = 1; n < m4; n++) {
			if (n == m8) {
				tmp1 = (float) (SQHALF * (x.get(xr1) + x.get(xi1)));
				x.set(xi1, (float) (SQHALF * (x.get(xi1) - x.get(xr1))));
				x.set(xr1, tmp1);
				tmp2 = (float) (SQHALF * (x.get(xi2) - x.get(xr2)));
				x.set(xi2, (float) (-SQHALF * (x.get(xr2) + x.get(xi2))));
				x.set(xr2, tmp2);
			} else {
				tmp2 = tab[logm - 4][cn++] * (x.get(xr1) + x.get(xi1));
				tmp1 = tab[logm - 4][spcn++] * x.get(xr1) + tmp2;
				x.set(xr1, tab[logm - 4][smcn++] * x.get(xi1) + tmp2);
				x.set(xi1, tmp1);
				tmp2 = tab[logm - 4][c3n++] * (x.get(xr2) + x.get(xi2));
				tmp1 = tab[logm - 4][spc3n++] * x.get(xr2) + tmp2;
				x.set(xr2, tab[logm - 4][smc3n++] * x.get(xi2) + tmp2);
				x.set(xi2, tmp1);
			}
			// System.out.println ("logm-4="+(logm-4));
			xr1++;
			xr2++;
			xi1++;
			xi2++;
		}
		/* Call ssrec again with half DFT length */
		srrec(x, xr, xi, tab, logm - 1);

		/*
		 * Call ssrec again twice with one quarter DFT length. Constants have to
		 * be recomputed, because they are static!
		 */
		m = 1 << logm;
		m2 = m / 2;
		srrec(x, xr + m2, xi + m2, tab, logm - 2);
		m = 1 << logm;
		m4 = 3 * (m / 4);
		srrec(x, xr + m4, xi + m4, tab, logm - 2);
	}
	
	/**
	 * 
	 * @param logm
	 */
	private static float[][] creattab(int logm) {
		int m, m2, m4, m8, nel, n, rlogm;
		int cn, spcn, smcn, c3n, spc3n, smc3n;
		double ang, s, c;
		float[][] tab = new float[logm - 4 + 1][6 * ((1 << logm) / 4 - 2)];
		for (rlogm = logm; rlogm >= 4; rlogm--)

		{
			m = 1 << rlogm;
			m2 = m / 2;
			m4 = m2 / 2;
			m8 = m4 / 2;
			nel = m4 - 2;
			/* Initialize pointers */

			cn = 0;
			spcn = cn + nel;
			smcn = spcn + nel;
			c3n = smcn + nel;
			spc3n = c3n + nel;
			smc3n = spc3n + nel;

			/* Compute tables */
			for (n = 1; n < m4; n++) {
				if (n == m8)
					continue;
				ang = n * TWOPI / m;
				c = Math.cos(ang);
				s = Math.sin(ang);
				tab[rlogm - 4][cn++] = (float) c;
				tab[rlogm - 4][spcn++] = (float) (-(s + c));
				tab[rlogm - 4][smcn++] = (float) (s - c);

				ang = 3 * n * TWOPI / m;
				c = Math.cos(ang);
				s = Math.sin(ang);
				tab[rlogm - 4][c3n++] = (float) c;
				tab[rlogm - 4][spc3n++] = (float) (-(s + c));
				tab[rlogm - 4][smc3n++] = (float) (s - c);
			}
		}
		return tab;
	}

}
