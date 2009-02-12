/*
 * Copyright (c) 2002-2003, Minnozzi Leonardo, Holger Crysandt
 * 
 * This file is part of MPEG7AudioEnc.
 */
package it.univpm.deit;

/**
 * @author Minnozzi Leonardo, Holger Crysandt
 */
final public class FFT2N {

  private final Butterfly butterfly;
  public final int length;

  public FFT2N(int length) throws IllegalArgumentException {
	if (!isPowerOf2(length))
	  throw new IllegalArgumentException("length must be a power of 2");
	this.length = length;
	butterfly = getButterfly( length );
  }

  private Butterfly getButterfly( int length ) {
	switch( length ) {
	  case 2:
	return new Butterfly2();
	  case 4:
	return new Butterfly4();
	  default:
	return new Butterfly2n(length, getButterfly(length / 2));
	}
  }

  /**
   * Calculates the fft of a complex signal. The complex coefficients
   * are returned in the values re and im. The signal is overwritten
   * during the fft.
   */
  public void fft2(float[] re, float[] im) {
	butterfly.fft( re, im );
  }

  /**
   * Calculates the fft of a real signal. The "half-complex" coefficients
   * are returned in the value re.
   * re = [ r_0, r_1, ..., r_(length/2), i_(length/2-1), ..., i_1 ]
   */
  public void fft2(float[] re) {
	butterfly.fft( re );
  }

  /**
   * returns the power spectrum of a FFT.
   */
  public static float[] PowerSpectrum( float[] re, float[] im ) {
	float[] power = new float[re.length/2+1];
	int i=0;
	for (; i < power.length; ++i)
	  power[i] += re[i] * re[i]  + im[i] * im[i];
	for (; i < re.length; ++i)
	  power[re.length - i] += re[i] * re[i] + im[i] * im[i];
	return power;
  }

  /**
   * returns the power spectrum of a "half-complex" FFT.
   */
  public static float[] PowerSpectrum( float[] re) {
	float[] power = new float[re.length/2+1];
	int i=0;
	for (; i < power.length; ++i)
	  power[i] += re[i] * re[i];
	for (; i < re.length; ++i) {
	  power[re.length - i] += re[i] * re[i];
	  power[re.length - i] *= 2.0;
	}
	return power;
  }

  private boolean isPowerOf2( int x ) {
	while ( (x & 1) == 0)
	  x >>= 1;
	return (x == 1);
  }

  private static float[] wRe( int length ) {
	float[] w_re = new float[length];
	for( int i=0; i<w_re.length; ++i )
	  w_re[i] = (float)Math.cos(2.0 * Math.PI * i / length);
	return w_re;
  }

  private static float[] wIm( int length ) {
	float[] w_im = new float[length];
	for( int i=0; i<w_im.length; ++i )
	  w_im[i] = (float)-Math.sin(2.0 * Math.PI * i / length);
	return w_im;
  }

  private void perfectShuffle( float[] src,
				   float[] dest,
				   int blocksize )
  {
	int i, i_max, j, k;
	for (int b = 0; b < src.length; b += blocksize) {
	  i = k = b;
	  j  = i_max = b + blocksize / 2;
	  for (; i < i_max; ) {
	dest[k++] = src[i++];
	dest[k++] = src[j++];
	  }
	}
  }

   private class Butterfly2n
	  implements Butterfly {

	private int length;
	private Butterfly next;

	float[] w_re;
	float[] w_im;

	Butterfly2n(int length, Butterfly next) {
	  this.length = length;
	  this.next = next;

	  w_re = wRe(length);
	  w_im = wIm(length);
	}

	public void fft(float[] re, float[] im) {
	  float[] tmp_re = new float[re.length];
	  float[] tmp_im = new float[im.length];

	  float real, imag;
	  for (int l = 0; l < re.length; l += length) {
	for (int i = l, j = l + length / 2, imax = j, k = 0;
		 i < imax;
		 ++i, ++j,
		 ++k)
	{
	  tmp_re[i] = ( real = re[i] ) + re[j];
	  real -= re[j];

	  tmp_im[i]   = (imag = im[i]) + im[j];
	  imag -= im[j];

	  tmp_re[j] = real * w_re[k] - imag * w_im[k];
	  tmp_im[j] = real * w_im[k] + imag * w_re[k];
	}
	  }
	  next.fft(tmp_re, tmp_im);
	  perfectShuffle(tmp_re, re, length);
	  perfectShuffle(tmp_im, im, length);
	}

	/**
	 * Calculates the fft of a real signal. The "half-complex" coefficients
	 * are returned with the value re.
	 * re = [ r_0, r_1, ..., r_(length/2), i_(length/2-1), ..., i_1 ]
	 */
	public void fft(float[] re) {
	  float[] tmp_re = new float[re.length / 2];
	  float[] tmp_im = new float[re.length / 2];
	  for (int i = 0, j = 0; i < re.length; ++j) {
	tmp_re[j] = re[i++];
	tmp_im[j] = re[i++];
	  }
	  next.fft( tmp_re, tmp_im );
	  re[0]             = tmp_re[0] + tmp_im[0];
	  re[re.length / 2] = tmp_re[0] - tmp_im[0];
	  float rp, rm, ip, im;
	  int i, ii, j, jj;
	  for (i = 1, ii = 2 + (j = tmp_re.length - 1), jj = re.length - 1;
	   i <= j;
	   ++i, ++ii, --j, --jj) {
	rp = (tmp_re[i] + tmp_re[j]) / 2;
	rm = (tmp_re[i] - tmp_re[j]) / 2;
	ip = (tmp_im[i] + tmp_im[j]) / 2;
	im = (tmp_im[i] - tmp_im[j]) / 2;

	float tmp0 = w_re[i] * ip + w_im[i] * rm;
	re[i] =  rp + tmp0;
	re[j] =  rp - tmp0;

	tmp0 = + w_im[i] * ip - w_re[i] * rm;
	re[ii] = tmp0 - im;
	re[jj] = tmp0 + im;
	  }
	}
  }

  private interface Butterfly {
	public void fft( float[] re, float[] im );
	public void fft( float[] re );
  }

  private class Butterfly4
	  implements Butterfly {

	private Butterfly2 butterfly2 = new Butterfly2();

	public void fft(float[] re, float[] im) {
	  float[] tmp_re = new float[re.length];
	  float[] tmp_im = new float[im.length];

	  for (int l = 0, l2 = 2; l < re.length; ) {
	tmp_re[l]  = re[l] + re[l2];
	tmp_re[l2] = re[l] - re[l2];
	tmp_im[l]  = im[l] + im[l2];
	tmp_im[l2] = im[l] - im[l2];
	++l; ++l2;
	tmp_re[l2] = im[l] - im[l2];
	tmp_im[l]  = im[l] + im[l2];
	tmp_re[l]  = re[l] + re[l2];
	tmp_im[l2] = re[l2] - re[l];
	l2 = 2 + (l += 3);
	  }
	  butterfly2.fft(tmp_re, tmp_im);

	  perfectShuffle(tmp_re, re, 4);
	  perfectShuffle(tmp_im, im, 4);
	}

	public void fft(float[] re ) {
	  for (int l = 0, l1, l2, l3; l < re.length;l += 4)
	  {
	l3 = 1 + (l2 = 1 + (l1 = 1 + l));
	float tmp_0 = re[l] + re[l2];
	float tmp_2 = re[l1] + re[l3];
	re[l3] -= re[l1];
	re[l1] = re[l] - re[l2];
	re[l]  = tmp_0 + tmp_2;
	re[l2] = tmp_0 - tmp_2;
	  }
	}
  }

  private class Butterfly2
	  implements Butterfly {

	public void fft(float[] re, float[] im) {
	  for (int l = 0, l1 = 1; l < re.length; l1 = 1 + (l += 2)) {
	float real = re[l] - re[l1];
	re[l] += re[l1];
	float imag = im[l] - im[l1];
	im[l] += im[l1];
	re[l1] = real;
	im[l1] = imag;
	  }
	}

	public void fft( float[] re ) {
	  for (int l = 0, l1 = 1; l < re.length; l1 = 1 + (l += 2)) {
	float tmp = re[l] - re[l1];
	re[l] += re[l1];
	re[l1] = tmp;
	  }
	}
  }
/*  
  public static void main(String[] args) {

	final int num_tests = 25;
	
	for( int length=1024; length<1024*128; length *=2 ) {
	  FFT2N fft2n = new FFT2N( length );
	  long duration_real = 0;
	  long duration_comp = 0,mia1=0,mia2=0;
	  for (int i = -1; i<num_tests; ++i) {
	float[] re = new float[length*2];
	float[] im = new float[length*2];
	im[1] = 1;re[0]=10;re[5]=1;//data = re;
	System.gc();
	long time1 = System.currentTimeMillis();
	
	//System.out.println("re "+re [10]+" "+re[15]);
	//System.out.println("im "+im [10]+" "+im[15]);
	fft2n.fft2(re);
	long time2 = System.currentTimeMillis();
	fft2n.fft2(re, im);
	long time3 = System.currentTimeMillis();
	
	fft2n.fft(re);
	long time4 =System.currentTimeMillis();
	fft2n.fft(re,im);
	long time5 =System.currentTimeMillis();

	if( i>=0 ) { // skip first run
	  duration_real += time2 - time1;
	  duration_comp += time3 - time2;
	  mia1+=time4-time3;
	  mia2+=time5-time4; 
	}
	  }
	  System.out.println(length + ": \t fftreale " +
			 duration_real/num_tests + " ms \t" +"fftrealeottim "+mia1/num_tests+" ms \t fftcompl "+duration_comp/num_tests+ " ms \t fftcomplottim "+mia2/num_tests+ " ms \t");
	}
  }
*/  
public  void fft(float [] re, float [] im)
{  int i=re.length>>>1,j=i<<1;
float data [] = new float [j+1];

for(;--i>=0;)
 {data[j--]=im[i];   
 data[j--]=re[i];
 }


 i=re.length>>>1;j=3;
 fou(data,i);
 for(;--i>0;)
 {
	re[i]=data[j++];
 im[i]=data[j++];
 
 }
 im[0]=data[2];
 re[0]=data[1];
  }
   private static void fou(float data[], int nn)
      
	{
		int i,n,mmax,m,j,istep;
		float wtemp,wr,wpr,wpi,wi,theta; 
		float tempr,tempi;
		n = nn << 1;
		j = 1;
		for (i = 1;i < n;i+= 2) {
			if (j > i) { float tmp;
				tmp = data[j];data[j] = data[i];data[i] = tmp; 
				tmp = data[j+1];data[j+1] = data[i+1];data[i+1] = tmp;           }
			m = nn;
			while (m >= 2 && j > m) {
				j -= m;
				m >>= 1;
			}
			j += m;
		}
		
		mmax = 2;
		while (n > mmax) { 
			istep = mmax << 1;
			theta = (float)6.28318530717959/mmax;
			wtemp = (float)Math.sin(0.5*theta);
			wpr = -2*wtemp*wtemp;
			
			wpi = (float)Math.sin(theta);
			wr = 1;
			wi = 0;
			for (m = 1;m < mmax;m+= 2) { 
				for (i = m;i <= n;i+= istep) {
					j = i+mmax;
					tempr = wr*data[j]-wi*data[j+1];
					tempi = wr*data[j+1]+wi*data[j];
					data[j] = data[i]-tempr;
					data[j+1] = data[i+1]-tempi;
					data[i] += tempr;
					data[i+1] += tempi;
				}
				wr = (wtemp = wr)*wpr-wi*wpi+wr;
				wi = wi*wpr+wtemp*wpi+wi;
			}
			mmax = istep;
		}
	}
	
	 public void fft(float re[])
	 {
	 int n=re.length;   
	 float[] data=new float[n+1]; 
     
	 //System.arraycopy(re,0,data,1,n);
	 //init
		int i,i1,i2,i3,i4,np3;
		float c1 = (float)0.5,c2=(float)-0.5,h1r,h1i,h2r,h2i;
		float wr,wi,wpr,wpi,wtemp,theta; 
		for( i=n;--i>=0;)data[i+1]=re[i];
		theta = (float)3.141592653589793/(float) (n >>> 1);
		
		fou(data,n>>>1);

		wtemp = (float)Math.sin(0.5*theta);
		wpr = -2*wtemp*wtemp;
		wpi = (float)Math.sin(theta);
		wr = 1+wpr;
		wi = wpi;
		np3 = n+3;
		for (i = 2;i <= (n >>> 2);i++) { 
			 i4 = 1+(i3 = np3-(i2 = 1+(i1 = i+i-1)));
			h1r = c1*(data[i1]+data[i3]); 
			h1i = c1*(data[i2]-data[i4]);
			h2r = -c2*(data[i2]+data[i4]);
			h2i = c2*(data[i1]-data[i3]);
			data[i1] = h1r+wr*h2r-wi*h2i; 
			data[i2] = h1i+wr*h2i+wi*h2r;
			data[i3] = h1r-wr*h2r+wi*h2i;
			data[i4] = -h1i+wr*h2i+wi*h2r;
			wr = (wtemp = wr)*wpr-wi*wpi+wr; 
			wi = wi*wpr+wtemp*wpi+wi;
		}
		   data[1]=(h1r=data[1])+data[2];
		   data[2]=h1r-data[2];

	 //fine
	 int j=1,k=n-1;  
	  for(i=n>>>1;--i>0;)
	  {
	  re[k--]=-data[j+j+2];
	  re[j]=data[j+j+1];
	  j++;
	  }
	  re[j]=data[2];
	  re[0]=data[1];
	 }

  
 

  public static void main(String[] args) {
	float[] signal = {0,1,0,0,0,0,0,0};
	FFT2N fft2n = new FFT2N( signal.length );
	fft2n.fft(signal);
	assert signal != null;
  }

}
