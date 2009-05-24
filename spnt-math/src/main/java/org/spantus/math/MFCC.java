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
 * Based on Aldebaro Klautau mfcc impl(version 2.0 - March 07, 2001):
 * Calculates the mel-based cepstra coefficients for one frame of speech.
 * Based on the original MFCC implementation described in:
 * [1] Davis & Mermelstein - IEEE Transactions on ASSP, August 1980.
 * Additional references are:
 * [2] Joseph Picone, Proceedings of the IEEE, Sep. 1993.
 * [3] Jankowski et al. IEEE Trans. on Speech and Audio Processing. July, 1995.
 * [4] Cardin et al, ICASSP'93 - pp. II-243
 *
 * Notice that there are several different implementations of the mel filter
 * bank. For example, the log is usually implementated after having the filter
 * outputs calculated, but could be implemented before filtering. Besides, there are
 * differences in the specification of the filter frequencies. [1]
 * suggested linear scale until 1000 Hz and logarithm scale afterwards.
 * This implementation uses the equation (10) in [2]:
 *      mel frequency = 2595 log(1 + (f/700)), where log is base 10
 * to find the filter bank center frequencies.
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.01
 *
 */
public class MFCC {

	static int m_nnumberOfFilters = 24;
	
	static int nlifteringCoefficient = 22;
	
	static int nnumberOfParameters = 12;
	/**
	 * Minimum value of filter output, otherwise the log is not calculated and
	 * m_dlogFilterOutputFloor is adopted. ISIP implementation assumes
	 * m_dminimumFilterOutput = 1 and this value is used here.
	 */
	private static final double m_dminimumFilterOutput = 1.0;
	/**Floor value for filter output in log domain.
	 * ISIP implementation assumes m_dlogFilterOutputFloor = 0 and this value is used
	 * here.
	 */
	private static final float m_dlogFilterOutputFloor = 0.0f;
	/**Coefficient of filtering performing in cepstral domain 
	 * (called 'liftering' operation). It is not used if 
	 * m_oisLifteringEnabled is false. 
	 */ 
	private static boolean oisLifteringEnabled = true;


	/**
	 * Returns the MFCC coefficients for the given speech frame. If calculated,
	 * the 0-th coefficient is added to the end of the vector (for compatibility
	 * with HTK). The order of an output vector x with 3 MFCC's, including the
	 * 0-th, would be: x = {MFCC1, MFCC2, MFCC0}
	 */
	public static List<Float> calculateMFCC(List<Float> fspeechFrame, double dsamplingFrequency) {

		
		List<Float> dfilterOutput = MatrixUtils.zeros(m_nnumberOfFilters);
		double[][] dweights = calculateMelBasedFilterBank(dsamplingFrequency, m_nnumberOfFilters, fspeechFrame.size());
		List<List<Float>> nboundariesDFTBins = new ArrayList<List<Float>>(
				m_nnumberOfFilters);
		for (int i = 0; i < m_nnumberOfFilters; i++) {
			List<Float> lst = new ArrayList<Float>();
			lst.add(new Float(0f));
			lst.add(new Float(0f));
			nboundariesDFTBins.add(lst);
		}

		// use mel filter bank
		for (int i = 0; i < m_nnumberOfFilters; i++) {
			dfilterOutput.set(i, 0.0f);
			// Notice that the FFT samples at 0 (DC) and fs/2 are not considered
			// on this calculation
			List<Float> fmagnitudeSpectrum = TransformUtil
					.calculateFFTMagnitude(fspeechFrame);
			for (int j = nboundariesDFTBins.get(i).get(0).intValue(), k = 0; j <= nboundariesDFTBins
					.get(i).get(1); j++, k++) {
				dfilterOutput.set(i, 
						new Float( dfilterOutput.get(i)
								+ fmagnitudeSpectrum.get(j) * dweights[i][k]
								 )
				);
			}

			// ISIP (Mississipi univ.) implementation
			if (dfilterOutput.get(i) > m_dminimumFilterOutput) {// floor
																	// power to
																	// avoid
																	// log(0)
				dfilterOutput.set(i, new Float(Math.log(dfilterOutput.get(i)))); // using
																			// ln
			} else {
				dfilterOutput.set(i, m_dlogFilterOutputFloor);
			}
		}
		// need to allocate space for output array
		// because it allows the user to call this method
		// many times, without having to do a deep copy
		// of the output vector
		List<Float> dMFCCParameters = MatrixUtils.zeros(nnumberOfParameters);
		// allocate space

		double dscalingFactor = Math.sqrt(2.0 / m_nnumberOfFilters);
		
		double[][] m_ddCTMatrix = calculateDCTMatrix(nnumberOfParameters);
		
		// cosine transform
		for (int i = 0; i < nnumberOfParameters; i++) {
			for (int j = 0; j < m_nnumberOfFilters; j++) {
				dMFCCParameters.set(i, new Float
						( dMFCCParameters.get(i) 
						+ dfilterOutput.get(j) * m_ddCTMatrix[i][j]
				        )
				);
				// the original equations have the first index as 1
			}
			// could potentially incorporate liftering factor and
			// factor below to save multiplications, but will not
			// do it for the sake of clarity
			dMFCCParameters.set(i, new Float(dMFCCParameters.get(i) * dscalingFactor));
		}

		// debugging purposes
		// System.out.println("Windowed speech");
		// IO.DisplayVector(fspeechFrame);
		// System.out.println("FFT spectrum");
		// IO.DisplayVector(fspectrumMagnitude);
		// System.out.println("Filter output in dB");
		// IO.DisplayVector(dfilterOutput);
		// System.out.println("DCT matrix");
		// IO.DisplayMatrix(m_ddCTMatrix);
		// System.out.println("MFCC before liftering");
		// IO.DisplayVector(dMFCCParameters);

		if (oisLifteringEnabled) {
//			 Implements liftering to smooth the cepstral coefficients
//			 according to
//			 [1] Rabiner, Juang, Fundamentals of Speech Recognition, pp. 169,
//			 [2] The HTK Book, pp 68 and
//			 [3] ISIP package - Mississipi Univ. Picone's group.
//			 if 0-th coefficient is included, it is not liftered
			for (int i = 0; i < nnumberOfParameters; i++) {
				dMFCCParameters.set(i, new Float(dMFCCParameters.get(i) * calculateLifteringFactor()[i]));
			}
		}

		return dMFCCParameters;
	}

	static double[] nlifteringMultiplicationFactor = null;
	
	public static double[] calculateLifteringFactor() {
		if(nlifteringMultiplicationFactor != null){
			return nlifteringMultiplicationFactor;
		}
		
		// for liftering method
		if (oisLifteringEnabled) {
			// note that:
//			int nnumberOfCoefficientsToLift = nnumberOfParameters;
			// even when m_oisZeroThCepstralCoefficientCalculated is true
			// because if 0-th cepstral coefficient is included,
			// it is not liftered
			nlifteringMultiplicationFactor = new double[nlifteringCoefficient];
			double dfactor = nlifteringCoefficient / 2.0;
			double dfactor2 = Math.PI / nlifteringCoefficient;
			for (int i = 0; i < nlifteringCoefficient; i++) {
				nlifteringMultiplicationFactor[i] = 1.0 + dfactor
						* Math.sin(dfactor2 * (i + 1));
			}
			if (nnumberOfParameters > nlifteringCoefficient) {
				new Error(
						"Liftering is enabled and the number "
								+ "of parameters = "
								+ nnumberOfParameters
								+ ", while "
								+ "the liftering coefficient is "
								+ nlifteringCoefficient
								+ ". In this case some cepstrum coefficients would be made "
								+ "equal to zero due to liftering, what does not make much "
								+ "sense in a speech recognition system. You may want to "
								+ "increase the liftering coefficient or decrease the number "
								+ "of MFCC parameters.");
			}
		} else {
			nlifteringMultiplicationFactor = new double[]{};
		}
		return nlifteringMultiplicationFactor;
	}

	
	/**
	 * Converts frequencies in Hz to mel scale according to mel frequency = 2595
	 * log(1 + (f/700)), where log is base 10 and f is the frequency in Hz.
	 */
	public static double[] convertHzToMel(double[] dhzFrequencies,
			double dsamplingFrequency) {
		double[] dmelFrequencies = new double[dhzFrequencies.length];
		for (int k = 0; k < dhzFrequencies.length; k++) {
			dmelFrequencies[k] = 2595.0 * (Math
					.log(1.0 + (dhzFrequencies[k] / 700.0)) / Math.log(10));
		}
		return dmelFrequencies;
	}

	/**
	 * Calculates triangular filters.
	 */

	private static double[][] calculateMelBasedFilterBank(double dsamplingFrequency,
			int nnumberofFilters, int nfftLength) {

		// frequencies for each triangular filter
//		double[][] dfrequenciesInMelScale = new double[nnumberofFilters][3];
		// the +1 below is due to the sample of frequency pi (or fs/2)
		double[] dfftFrequenciesInHz = new double[nfftLength / 2 + 1];
		// compute the frequency of each FFT sample (in Hz):
		double ddeltaFrequency = dsamplingFrequency / nfftLength;
		for (int i = 0; i < dfftFrequenciesInHz.length; i++) {
			dfftFrequenciesInHz[i] = i * ddeltaFrequency;
		}
		// convert Hz to Mel
		double[] dfftFrequenciesInMel = convertHzToMel(
				dfftFrequenciesInHz, dsamplingFrequency);

		// compute the center frequencies. Notice that 2 filters are
		// "artificially" created in the endpoints of the frequency
		// scale, correspondent to 0 and fs/2 Hz.
		double[] dfilterCenterFrequencies = new double[nnumberofFilters + 2];
		// implicitly: dfilterCenterFrequencies[0] = 0.0;
		ddeltaFrequency = dfftFrequenciesInMel[dfftFrequenciesInMel.length - 1]
				/ (nnumberofFilters + 1);
		for (int i = 1; i < dfilterCenterFrequencies.length; i++) {
			dfilterCenterFrequencies[i] = i * ddeltaFrequency;
		}

		// initialize member variables
		int[][] m_nboundariesDFTBins = new int[m_nnumberOfFilters][2];
		double[][] m_dweights = new double[m_nnumberOfFilters][];

		// notice the loop starts from the filter i=1 because i=0 is the one
		// centered at DC
		for (int i = 1; i <= nnumberofFilters; i++) {
			m_nboundariesDFTBins[i - 1][0] = Integer.MAX_VALUE;
			// notice the loop below doesn't include the first and last FFT
			// samples
			for (int j = 1; j < dfftFrequenciesInMel.length - 1; j++) {
				// see if frequency j is inside the bandwidth of filter i
				if ((dfftFrequenciesInMel[j] >= dfilterCenterFrequencies[i - 1])
						& (dfftFrequenciesInMel[j] <= dfilterCenterFrequencies[i + 1])) {
					// the i-1 below is due to the fact that we discard the
					// first filter i=0
					// look for the first DFT sample for this filter
					if (j < m_nboundariesDFTBins[i - 1][0]) {
						m_nboundariesDFTBins[i - 1][0] = j;
					}
					// look for the last DFT sample for this filter
					if (j > m_nboundariesDFTBins[i - 1][1]) {
						m_nboundariesDFTBins[i - 1][1] = j;
					}
				}
			}
		}
		// check for consistency. The problem below would happen just
		// in case of a big number of MFCC parameters for a small DFT length.
		for (int i = 0; i < nnumberofFilters; i++) {
			if (m_nboundariesDFTBins[i][0] == m_nboundariesDFTBins[i][1]) {
				new Error(
						"Error in MFCC filter bank. In filter "
								+ i
								+ " the first sample is equal to the last sample !"
								+ " Try changing some parameters, for example, decreasing the number of filters.");
			}
		}

		// allocate space
		for (int i = 0; i < nnumberofFilters; i++) {
			m_dweights[i] = new double[m_nboundariesDFTBins[i][1]
					- m_nboundariesDFTBins[i][0] + 1];
		}

		// calculate the weights
		for (int i = 1; i <= nnumberofFilters; i++) {
			for (int j = m_nboundariesDFTBins[i - 1][0], k = 0; j <= m_nboundariesDFTBins[i - 1][1]; j++, k++) {
				if (dfftFrequenciesInMel[j] < dfilterCenterFrequencies[i]) {
					m_dweights[i - 1][k] = (dfftFrequenciesInMel[j] - dfilterCenterFrequencies[i - 1])
							/ (dfilterCenterFrequencies[i] - dfilterCenterFrequencies[i - 1]);
				} else {
					m_dweights[i - 1][k] = 1.0 - ((dfftFrequenciesInMel[j] - dfilterCenterFrequencies[i]) / (dfilterCenterFrequencies[i + 1] - dfilterCenterFrequencies[i]));
				}
			}
		}
		return m_dweights;
	}
	/**Initializes the DCT matrix.*/
	private static double[][] calculateDCTMatrix(int nnumberOfParameters) {
		double[][] ddCTMatrix = new double[nnumberOfParameters][m_nnumberOfFilters];
		for(int i=0;i<nnumberOfParameters;i++) {
			for(int j=0;j<m_nnumberOfFilters;j++) {
				ddCTMatrix[i][j] = Math.cos((i+1.0)*(j+1.0-0.5)*(Math.PI/m_nnumberOfFilters));
			}
		}
		return ddCTMatrix;
	}

}
