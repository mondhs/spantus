/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.math;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public final class FFT2N {
	
	private final int length; 
	
	public FFT2N(int length) {
		this.length = length;
	}
	/**
	 * Determines the half complex spectrum of a real signal
	 * @param signal real input signal; replaced by half complex signal 
	 */
	public void fft(float[] signal){
		assert length == signal.length;
		realft(signal, signal.length, true);
	}	
	
	/**
	 * Determines the real signal of a half complex signal
	 * @param spectrum half complex spectrum; replaced by real signal
	 */
	public void ifft(float[] spectrum){
		assert length == spectrum.length;
		realft(spectrum, spectrum.length, false);
		int length2 = spectrum.length/2;
		for (int i=0; i<spectrum.length; ++i)
			spectrum[i] /= length2;
	}
	
	/**
	 * @param spectrum Half complex spectrum
	 * @return Returns power of spectrum
	 */
	public static float[] PowerSpectrum(float[] spectrum) {
		float[] power = new float[spectrum.length/2+1];
		power[0] = spectrum[0] * spectrum[0];
		power[power.length-1] = spectrum[1] * spectrum[1];
		for (int i=2, j=1; i<spectrum.length; i+=2, ++j)
			power[j] = spectrum[i] * spectrum[i] + spectrum[i+1]*spectrum[i+1];
		return power;																						  
	}
	
	/**
	 * Determines the conjurgate complex spectrum of a half complex 
	 * spectrum. spectrum is replaced by conjurgate complex spectrum
	 * @param f Half complex spectrum
	 */
	public static void conj(float[] f) {
		for (int i=3; i<f.length; i+=2)
			f[i] = -f[i];
	}
	
	/**
	 * Multiplies two half complex spetra
	 * @param f1 Half complex spectrum
	 * @param f1 Half complex spectrum
	 * @return Returns product of both half complex spectra
	 */
	public static float[] mult(float[] f1, float[] f2) {
		float[] prod = new float[f1.length];
		prod[0] = f1[0] * f2[0];
		prod[1] = f1[1] * f2[1];
		for (int i=2; i<prod.length; i+=2) {
			prod[i]   = f1[i] * f2[i] - f1[i+1]*f2[i+1];
			prod[i+1] = f1[i] * f2[i+1] + f2[i]*f1[i+1];
		}
		return prod;
	}
	
	/**
	 * @param spectrum Half complex spectrum
	 * @return Returns real part of half complex spectrum
	 */
	public static float[] getReal(float[] spectrum) {
		float[] real = new float[spectrum.length];
		real[0] = spectrum[0];
		real[real.length/2] = spectrum[1];
		for (int i=1, j=real.length-1; i<j; ++i, --j)
			real[j] = real[i] = spectrum[2*i];
		return real;
	}
	
	/**
	 * @param spectrum Half complex spectrum
	 * @return Returns imaginary part of half complex spectrum
	 */
	public static float[] getImag(float[] spectrum) {
		float[] imag = new float[spectrum.length];
		for (int i=1, j=imag.length-1; i<j; ++i, --j)
		  imag[i] = -(imag[j] = spectrum[2*i+1]);
		return imag;
	}
	
	private void realft(float[] data, int n, boolean isign){
		float c1 = 0.5f; 
		float c2, h1r, h1i, h2r, h2i;
		double wr, wi, wpr, wpi, wtemp;
		
		double theta = 3.141592653589793/(n>>1);
		if (isign) {
			c2 = -.5f;
			four1(data, n>>1, true);
		} else {
			c2 = .5f;
			theta = -theta;
		}
		wtemp = Math.sin(.5*theta);
		wpr = -2.*wtemp*wtemp;
		wpi = Math.sin(theta);
		wr = 1. + wpr;
		wi = wpi;
		int np3 = n + 3;
		for (int i=2,imax = n >> 2, i1, i2, i3, i4; i <= imax; ++i) {
			/** @todo this can be optimized */
			i4 = 1 + (i3 = np3 - (i2 = 1 + (i1 = i + i - 1)));
			--i4; --i2; --i3; --i1; 
 
			h1i =  c1*(data[i2] - data[i4]);
			h2r = -c2*(data[i2] + data[i4]);
			
			h1r =  c1*(data[i1] + data[i3]);
			h2i =  c2*(data[i1] - data[i3]);
			
			data[i1] = (float) ( h1r + wr*h2r - wi*h2i);
			data[i2] = (float) ( h1i + wr*h2i + wi*h2r);
			data[i3] = (float) ( h1r - wr*h2r + wi*h2i);
			data[i4] = (float) (-h1i + wr*h2i + wi*h2r);
			wr = (wtemp=wr)*wpr - wi*wpi + wr;
			wi = wi*wpr + wtemp*wpi + wi;
		}
		if (isign) {
			float tmp = data[0]; 
			data[0] += data[1];
			data[1] = tmp - data[1];
		} else {
			float tmp = data[0];
			data[0] = c1 * (tmp + data[1]);
			data[1] = c1 * (tmp - data[1]);
			four1(data, n>>1, false);
		}
	}
	
	private void four1(float data[], int nn, boolean isign) {
		int n, mmax, istep;
		double wtemp, wr, wpr, wpi, wi, theta;
		float tempr, tempi;
		
		n = nn << 1;				
		for (int i = 1, j = 1; i < n; i += 2) {
			if (j > i) {
				// SWAP(data[j], data[i]);
				float swap = data[j-1];
				data[j-1] = data[i-1];
				data[i-1] = swap;
				
				// SWAP(data[j+1], data[i+1]);
				swap = data[j];
				data[j] = data[i]; 
				data[i] = swap;
			}			
			int m = n >> 1;
			while (m >= 2 && j > m) {
				j -= m;
				m >>= 1;
			}
			j += m;
		}
		mmax = 2;
		
		while (n > mmax) {
			istep = mmax << 1;
			theta = 6.28318530717959 / mmax;
			if (!isign)
				theta = -theta;
			wtemp = Math.sin(0.5 * theta);
			wpr = -2.0 * wtemp * wtemp;
			wpi = Math.sin(theta);
			wr = 1.0;
			wi = 0.0;
			for (int m = 1; m < mmax; m += 2) {
				for (int i = m; i <= n; i += istep) {
					int j = i + mmax;
					tempr = (float) (wr * data[j-1] - wi * data[j]);	
					tempi = (float) (wr * data[j]   + wi * data[j-1]);	
					data[j-1] = data[i-1] - tempr;
					data[j]   = data[i] - tempi;
					data[i-1] += tempr;
					data[i]   += tempi;
				}
				wr = (wtemp = wr) * wpr - wi * wpi + wr;
				wi = wi * wpr + wtemp * wpi + wi;
			}
			mmax = istep;
		}
	}	
/*	
	public static void main(String[] args) {
		float[] signal1 = {0,1,2,3,4,5,6,7};
		float[] signal2 = {1,1,0,0,0,0,0,0};
		FFT2N fft = new FFT2N(signal1.length);
		fft.fft(signal1);
		fft.fft(signal2);
		FFT2N.conj(signal1);
		float[] corr = FFT2N.mult(signal1, signal2);
		fft.ifft(corr);
		assert signal1 != null;
	}
*/	
}
