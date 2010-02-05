/*
  Copyright (c) 2002-2006, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

import java.util.*;

import de.crysandt.math.*;
import de.crysandt.util.Debug;

/**
 * Hidden Markov Model with N Gaussian distributions
 *
 * The algorithm used is described in "A Gentle Tutorial of the EM Algorithm
 * and its Application to Parameter Estimation for Gaussian Mixture and Hidden
 * Markov Models" by Jeff A. Bilmes.
 *
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 *
 * @version 1.0
 */
public class HMM
{
	/** set initial values of state #n to realative time spent in this state */
	public static final int INIT_RELATIVE_TIME = 0;

	/** determine initial values of state #n by begin of sequence(s) */
	public static final int INIT_BEGIN_OF_SEQUENCE = 1;

	/** ignore initial probabilities */
	public static final int INIT_NONE = 2;
	
	static final float FLOAT_MIN_VALUE = 1024 * Float.MIN_VALUE;
//	private static final Random random = new Random();

	/** Number of states */
	public final int N;

	/** Length of feature vectors */
	public final int SIZE;

	private final float[] init;
	private final GaussianDistribution[] dist;
	private final float[][] trans;
	private transient double[][] trans_log = null;

	/**
	 * Hidden Markov Model
	 *
	 * @param N Number of states
	 * @param size Length of feature vectors
	 * @param init Initial values
	 * @param dist Array of Gaussian distribution
	 * @param trans Probability of transitions
	 */
	public HMM(int N,
			   int size,
			   float[] init,
			   GaussianDistribution[] dist,
			   float[][] trans)
	{
		this.N = N;
		this.SIZE = size;
		this.init = init;
		this.dist = dist;
		this.trans = trans;
	}

	HMM(int N, int size) {
		this(N, size, new float[N], new GaussianDistribution[N], new float[N][N]);
	}

	/**
	 * Initializes gaussian distributions ignoring first dimension (power)
	 *
	 * @param sequences Sequence of states (stored row-by-row)
	 */

	static GaussianDistribution[] initGaussianDist(
			int SIZE,
			int N, 
			Collection<float[][]> sequences) 
	{
		double[] mean = new double[SIZE];
		double[] var = new double[SIZE];

		SortedSet<float[]> vectors = new TreeSet<float[]>(new SortByColumn(0));
		for (Iterator<float[][]> i = sequences.iterator(); i.hasNext(); ) {
			float[][] sequence = (float[][]) i.next();
			for (int n = 0, n_max = sequence.length; n < n_max; ++n) {
				assert sequence[n].length == SIZE;
				vectors.add(sequence[n]);
			}
		}

		for (Iterator<float[]> i = vectors.iterator(); i.hasNext(); ) {
			float[] vector = (float[]) i.next();

			for (int c = 0; c < SIZE; ++c) {
				mean[c] += vector[c];
				var[c] += vector[c] * vector[c];
			}
		}

		for (int c = 0; c < SIZE; ++c) {
			mean[c] /= vectors.size();
			var[c] = var[c] / vectors.size() - mean[c] * mean[c];
		}

		// find dimension with highest variance
		int index = 0;
		for (int i = 1; i < var.length; ++i)
			if (var[i] > var[index])
				index = i;

				// resort "vectors" if necessary
		if (index > 0) {
			SortedSet<float[]> vectors_new = new TreeSet<float[]>(new SortByColumn(index));
			vectors_new.addAll(vectors);
			vectors = vectors_new;
		}

		GaussianDistribution[] dist = new GaussianDistribution[N];
		final int GAP = (vectors.size() - 1) / (N + 1) - 1;
		Iterator<float[]> i = vectors.iterator();
		for (int n = 0; n < N; ++n) {
			// fast forward (#GAP steps)
			for (int k = 0; (k < GAP) && i.hasNext(); ++k)
				i.next();

			float[] vector = (float[]) i.next();

			float[] center = new float[SIZE];
			float[][] cov_inv = new float[SIZE][SIZE];

			for (int k = 0; k < SIZE; ++k) {
				center[k] = vector[k]
/*					 + (float) ((Math.random() - 0.5) / 10 * Math.sqrt(var[k])) */ 
					;
//				cov_inv[k][k] = (float) (SIZE / var[k]); // energy is shared between SIZE distributions (better Id
				cov_inv[k][k] = (float) (1.0f / var[k] / SIZE);    // energy of subband k is var[k]
			}

			dist[n] = new GaussianDistributionFull(center, cov_inv);
		}
		
		return dist;
	}

	/**
	 * Creates a hidden markov model with N states from one sequence of vectors
	 * using the Baum-Welch algorithm.
	 *
	 * @param N Number of states
	 * @param size Length of feature vector
	 * @param sequence Obsertion sequence stored row-by-row
	 * @param init_type INIT_RELATIVE_TIME, INIT_BEGIN_OF_SEQUENCE or INIT_NONE
	 * @param cov_format COVARIANCE_FULL or COVARIANCE_DIAGONAL 
	 *
	 * @return Returns hidden markov model
	 *
	 * @see HMM#INIT_RELATIVE_TIME
	 *      INIT_RELATIVE_TIME
	 * @see HMM#INIT_BEGIN_OF_SEQUENCE
	 *      INIT_BEGIN_OF_SEQUENCE
	 * @see HMM#INIT_NONE INIT_NONE
	 */
	public static HMM createModel(
			int N,
			int size,
			float[][] sequence,
			int init_type)
	{
		Collection<float[][]> list = new ArrayList<float[][]>(1);
		list.add(sequence);
		return createModel(N, size, list, init_type);
	}

	/**
	 * Creates a hidden markov model with N states of a collection of
	 * sequences of vectors using the Baum-Welch algorithm.
	 *
	 * @param N Number of states
	 * @param size Length of feature vector
	 * @param sequences Collection of observation sequences. Elements must be
	 * of type double[][]
	 *
	 * @param init_type INIT_RELATIVE_TIME, INIT_BEGIN_OF_SEQUENCE or INIT_NONE
	 *
	 * @return Returns hidden markov model
	 *
	 * @see HMM#INIT_RELATIVE_TIME
	 *      INIT_RELATIVE_TIME
	 * @see HMM#INIT_BEGIN_OF_SEQUENCE
	 *      INIT_BEGIN_OF_SEQUENCE
	 * @see HMM#INIT_NONE INIT_NONE
	 */
	public static HMM createModel(
			int N,
			int size,
			Collection<float[][]> sequences,
			int init_type)
	{
		HMM hmm = new HMM(N, size);
		
		Arrays.fill(hmm.init, 1.0f / N);

		System.arraycopy(initGaussianDist(size, N, sequences), 0, hmm.dist, 0, N);

		for (int n = 0; n < N; ++n)
			Arrays.fill(hmm.trans[n], 1.0f / N);

		try {
			double opt_path = hmm.getLogProbBestPath(sequences);
			for (int num_iter=0; true; ++num_iter) {
/*				
				for (int n=0; n<hmm.N; ++n)
					hmm.dist[n].toMatlab(System.out);
*/
				HMM hmm_new = hmm.optimizeModel(sequences, init_type);
				double opt_path_new = hmm_new.getLogProbBestPath(sequences);

				assert Debug.println(System.err,
									 num_iter + ", " + opt_path_new + " (" +
									 (opt_path_new - opt_path) + ")");

				// check if model does not change much anymore.
				if (Math.abs(1.0 - opt_path_new / opt_path) < 1e-5) {
					hmm = hmm_new;
					opt_path = opt_path_new;
					break;
				}

				hmm = hmm_new;
				opt_path = opt_path_new;
			}
		} catch (Exception e) {
			assert printStackTrace(e);
		}

		return hmm;
	}

	private static boolean printStackTrace(Exception e) {
		e.printStackTrace();
		return true;
	}

/**
	 * Determines the best path through the model using the Vertibi algorithm.
	 *
	 * @param sequence Sequence of states (stored row-by-row)
	 *
	 * @return logarithmic probablity of best path
	 */
	public double getLogProbBestPath(float[][] sequence) {
		// cache log. transitions
		if (trans_log == null) {
			trans_log = new double[N][N];
			for (int i = 0; i < N; ++i) {
				for (int j = 0; j < N; ++j) {
					trans_log[i][j] = Math.log(FLOAT_MIN_VALUE + trans[i][j]);
					assert trans_log[i][j] < 1e-3;
				}
			}
		}

		double[] max_log_prob = new double[N];

		for (int i = 0, sequence_last = sequence.length - 1; i < N; ++i)
			max_log_prob[i] = dist[i].getLogProb(sequence[sequence_last]);

		for (int r = sequence.length - 2; r >= 0; --r) {
			double[] max_log_prob_new = new double[N];
			float[] vector = sequence[r];

			for (int i = 0; i < N; ++i) {
				max_log_prob_new[i] = dist[i].getLogProb(vector);
			}

			for (int i = 0; i < N; ++i) {
				double best_path = Double.NEGATIVE_INFINITY;
				double[] trans_log_i = trans_log[i];
				for (int j = 0; j < N; ++j)
					best_path = Math.max(
							best_path,
							trans_log_i[j] + max_log_prob[j]);

				assert!Double.isNaN(best_path);
				assert!Double.isInfinite(best_path);

				max_log_prob_new[i] += best_path;
			}

			max_log_prob = max_log_prob_new;
		}

		if (init != null) {
			for (int i = 0; i < N; ++i)
				max_log_prob[i] += Math.log(init[i]);
		}

		// find and return maximium
		return Function.max(max_log_prob);
	}
/*
	public Collection getTypicalRandomSequence(int length) {
		int[][] num_trans = new int[N][N];

		int num_trans_sum = 0;
		for (int i = 0; i < N; ++i) {
			int[] row = num_trans[i];
			float[] trans_i = trans[i];
			for (int j = 0; j < N; ++j) {
				num_trans_sum += row[j] =
					(int) (Math.round(trans_i[j] * length / N));
			}
		}

		float[][] sequence = new float[num_trans_sum][N];
		for (int i = 0; i < num_trans_sum; ++i) {
			float[] vector_random = new float[N];
			synchronized (random) {
				for (int n = 0; n < N; ++n)
					vector_random[i] = (float) random.nextGaussian();
			}
		}

		return null;
	}
*/
	private double getLogProbBestPath(Collection<float[][]> sequences) {
		double sum_log_prob = 0.0;
		for (Iterator<float[][]> i = sequences.iterator(); i.hasNext(); )
			sum_log_prob += getLogProbBestPath((float[][]) i.next());
		return sum_log_prob;
	}

	/**
	 * Tries to optimize HMM model using the Baum-Welch algorithm.
	 *
	 * Use of indices:
	 * <ul>
	 * <li><i>e</i>: index of sequence</li>
	 * <li><i>t</i>: index of time; depends on sequence
	 * <li><i>i,j</i>: indexes of states (0 .. N-1)
	 * </ul>
	 *
	 * @param sequences Collection of sequences of vectors
	 * @param init_type INIT_RELATIVE_TIME or INIT_BEGIN_OF_SEQUENCE
	 *
	 * @return Returns optimized hidden markov model
	 */
	public HMM optimizeModel(Collection<float[][]> sequences, int init_type) {
		// gamma[e][t][i]
		double[][][] gamma = new double[sequences.size()][][];

		// xi[e][t][i][j]
		final double[][][][] xi = new double[sequences.size()][][][];

		{ // calculate gamma and xi
			Iterator<float[][]> iter = sequences.iterator();

			for (int e = 0; iter.hasNext(); ++e) {
				final float[][] sequence = (float[][]) iter.next();
				final double[][] dist_prob = new double[sequence.length][N];

				// calculate probabilities first
				for (int t = 0, t_max = dist_prob.length; t < t_max; ++t) {
					double[] dist_prob_t = dist_prob[t];
					float[] vector = sequence[t];
					for (int i = 0; i < N; ++i)

						// determine probability and clip max. value
						dist_prob_t[i] = Math.min(
							Float.MAX_VALUE,
							dist[i].getProb(vector) + Float.MIN_VALUE);
					
//					assert testDistProbT(dist_prob_t);
				}

				// forward (alpha)
				Alpha a = new Alpha(dist_prob, init, trans);
				double[][] alpha = a.getAlpha();
//        double[] alpha_scal = a.getAlphaScal();

				// backward (betha)
				Betha b = new Betha(dist_prob, trans);
				double[][] betha = b.getBetha();
				double[] betha_scal = b.getBethaScal();

				// gamma
				gamma[e] = calcGammaE(alpha, betha);

				// xi
				xi[e] = calcXiE(dist_prob, trans, betha, betha_scal, gamma[e]);
			}
		}

		/* create new HMM */
		// one cached value
		double[] sum_sum_gamma_et = calcSumSumGammaET(N, gamma);

		// update distribution
		GaussianDistribution[] dist_new = calcGaussianDistributions(
			N, SIZE, dist, gamma, sum_sum_gamma_et, sequences);
		
		// new transitions
		float[][] trans_new = calcTransitions(xi);

		// update init
		float[] init_new = new float[N];
		switch (init_type) {
			case INIT_BEGIN_OF_SEQUENCE:
				for (int i = 0; i < N; ++i) {
					double sum = 0.0;
					for (int e = 0, e_max = gamma.length; e < e_max; ++e)
						sum += gamma[e][0][i];
					init_new[i] = (float) (sum / sequences.size());
				}
				break;

			case INIT_RELATIVE_TIME:
				double sum_sum_gamma_eti = 0.0;
				for (int i = 0; i < N; ++i)
					sum_sum_gamma_eti += sum_sum_gamma_et[i];

				for (int i = 0; i < N; ++i)
					init_new[i] = (float) (sum_sum_gamma_et[i] / sum_sum_gamma_eti);
				break;

			case INIT_NONE:
				init_new = null;
				break;

			default:
				assert(false);
		}

		return new HMM(N, SIZE, init_new, dist_new, trans_new);
	}

	static double[] calcSumSumGammaET(int N, double[][][] gamma) {
		double[] sum_sum_gamma_et = new double[N];
		for (int e = 0; e < gamma.length; ++e)
			for (int t = 0, t_max = gamma[e].length; t < t_max; ++t)
				for (int i = 0; i < N; ++i) {
					assert!Double.isNaN(gamma[e][t][i]);
					sum_sum_gamma_et[i] += gamma[e][t][i];
				}
		return sum_sum_gamma_et; 
	}
	
	@SuppressWarnings("unused")
	private static boolean testDistProbT(double[] dist_prob_t) {
		for (int i = 0; i < dist_prob_t.length; ++i) {
			assert!Double.isNaN(dist_prob_t[i]);
			assert!Double.isInfinite(dist_prob_t[i]);
			assert dist_prob_t[i] >= 0.0;
		}

		int i = 0;

		// at least one value is bigger than 0
		for (int i_max = dist_prob_t.length; i < i_max; ++i)
			if (dist_prob_t[i] > Double.MIN_VALUE)
				break;

		return i < dist_prob_t.length;
	}

	/**
	 * Calculates gamme for the e-th sequence
	 *
	 * @param alpha double[][] forward coefficients
	 * @param betha double[][] backward coefficents
	 * @return double[][] gamma
	 */
	static double[][] calcGammaE(double[][] alpha, double[][] betha) {
		final int T = alpha.length;
		final int N = alpha[0].length;

		double[][] gamma_e = new double[T][N];
		for (int t = 0; t < T; ++t) {
			double[] alpha_t = alpha[t];
			double[] betha_t = betha[t];

			double sum = Float.MIN_VALUE;
			double[] gamma_et = gamma_e[t];
			for (int i = 0; i < N; ++i) {
				sum += gamma_et[i] = alpha_t[i] * betha_t[i];
				assert!Double.isNaN(gamma_et[i]);
				assert!Double.isInfinite(gamma_et[i]);
			}

			assert!Double.isNaN(sum);
			assert!Double.isInfinite(sum);

			for (int i = 0; i < N; ++i)
				gamma_et[i] = gamma_et[i] / sum;
		}

		// test gamma
		assert testGammaE(gamma_e);

		return gamma_e;
	}

	private static boolean testGammaE(double[][] gamma_e) {
		for (int t = 0, t_max = gamma_e.length; t < t_max; ++t) {
			double[] gamma_et = gamma_e[t];
			for (int i = 0, i_max = gamma_et.length; i < i_max; ++i) {
				assert!Double.isNaN(gamma_et[i]);
				assert!Double.isInfinite(gamma_et[i]);
				assert gamma_et[i] > 0.0;
			}
		}
		return true;
	}

	static double[][][] calcXiE(
			double[][] dist_prob,
			float[][] trans,
			double[][] betha,
			double[] betha_scal,
			double[][] gamma_e)
	{
		final int T = betha.length;
		final int N = betha[0].length;

		final double[][][] xi_e = new double[T - 1][N][N];
		/*
			double[][] xi_et;
			double[] gamma_et, betha_t, betha_t1, xi_eti, dist_prob_t;
			double betha_scal_t, factor;
			float[] trans_i;
		 */
		for (int t = 0; t < xi_e.length; ++t) {
			double[][] xi_et = xi_e[t];
			double[] gamma_et = gamma_e[t];
			double[] betha_t = betha[t];
			double[] betha_t1 = betha[t + 1];
			double betha_scal_t = betha_scal[t];
			double[] dist_prob_t = dist_prob[t + 1];
			for (int i = 0; i < N; ++i) {
				double[] xi_eti = xi_et[i];
				float[] trans_i = trans[i];
				double factor = gamma_et[i] / betha_t[i] / betha_scal_t;
				for (int j = 0; j < N; ++j) {
					xi_eti[j] = HMM.FLOAT_MIN_VALUE +
						factor * trans_i[j] * dist_prob_t[j] * betha_t1[j];
				}
			}
		}

		return xi_e;
	}

	static GaussianDistribution[] calcGaussianDistributions(
		   int N,
		   int SIZE,
		   GaussianDistribution[] dist,
		   double[][][] gamma,
		   double[] sum_sum_gamma_et, 
		   Collection<float[][]> sequences)
	{
		GaussianDistribution[] dist_new = new GaussianDistribution[N];
		
		for (int i = 0; i < N; ++i) {
			double sum_sum_gamma_eti = sum_sum_gamma_et[i];
			double[] mean_new_sum = new double[SIZE];
			Iterator<float[][]> iter = sequences.iterator();

			for (int e = 0; e < sequences.size(); ++e) {
				float[][] sequence = (float[][]) iter.next();
				double gamma_eti;
				float[] vector; 
				for (int t = 0, t_max = sequence.length; t < t_max; ++t) {
					gamma_eti = gamma[e][t][i];
					vector = sequence[t];
					for (int l = 0, l_max = mean_new_sum.length; l < l_max; ++l)
						mean_new_sum[l] += gamma_eti * vector[l];
				}
			}

			assert(!iter.hasNext());

			float[] mean_new = new float[SIZE];
			for (int l = 0, l_max = mean_new.length; l < l_max; ++l)
				mean_new[l] = (float) (mean_new_sum[l] / sum_sum_gamma_eti);

				// update inverse covariance matrix
			iter = sequences.iterator();

			double[][] cov_new_sum = new double[SIZE][SIZE];
			float[] center_i = dist[i].getCenter();
			for (int e = 0, e_max = sequences.size(); e < e_max; ++e) {
				float[][] sequence = (float[][]) iter.next();
				double[] diff = new double[SIZE];

				for (int t = 0; t < sequence.length; ++t) {
					float[] vector = sequence[t];

					double gamma_eti = gamma[e][t][i];

					for (int k = 0; k < SIZE; ++k)
						// TODO: Old or new center? 
						/*
						 * holger: 
						 * old center leads to positive improvements 
						 * new center somtimes lead to negative "improvements"
						 */
//						diff[k] = vector[k] - mean_new[k];
						diff[k] = vector[k] - center_i[k];

					for (int m = 0; m < SIZE; ++m) {
						double diff_m = diff[m];
						cov_new_sum[m][m] += gamma_eti * diff_m * diff_m;
						double[] cov_new_sum_m = cov_new_sum[m];
						for (int n = 0; n < m; ++n) {
							cov_new_sum_m[n] += (gamma_eti * diff_m * diff[n]);
							assert !Double.isNaN(cov_new_sum_m[n]);
							assert !Double.isInfinite(cov_new_sum_m[n]);
						}
					}
				}
			}
			
			assert ! iter.hasNext();

			float[][] cov_new = new float[SIZE][SIZE];
			for (int m = 0; m < SIZE; ++m)
				for (int n = 0; n <= m; ++n)
					cov_new[m][n] = cov_new[n][m] = (float) (cov_new_sum[m][n] / sum_sum_gamma_eti);

			try {
				double det = 1.0 / LinAlg.det(cov_new);
				
				if (Double.isNaN(det))
					throw new IllegalArgumentException(
						"determinant of inverse covariance matrix is NaN");

				if (det>Float.MAX_VALUE)
					throw new IllegalArgumentException(
							"determinant of inverse covariance matrix is too large");
				
				if (det < 0)
					throw new IllegalArgumentException(
						"determinant of inverse covariance matrix must be positive");
					
				float[][] cov_new_inv = LinAlg.inv(cov_new);
				
				GaussianDistribution gdf = 
					new GaussianDistributionFull(mean_new, cov_new_inv, (float) det);
				
				dist_new[i] = gdf;
			} catch (IllegalArgumentException e) {
				assert Debug.println(System.err, 
						"Oops: " + e.getMessage() + ". " + 
						"Taking old covariance matrix instead.");

				// take new center and old covariance matrix
				if (dist[i] instanceof GaussianDistributionDiagonal) {
					GaussianDistributionDiagonal gdg = 
						(GaussianDistributionDiagonal) dist[i];
					
					dist_new[i] = new GaussianDistributionDiagonal(
							mean_new, gdg.getVarianceInverse());
				} else {
					dist_new[i] = new GaussianDistributionFull(
							mean_new, 
							dist[i].getCovarianceInverse(), 
							dist[i].getDeterminant());
				}
			}
		}

		return dist_new;
	}

	static float[][] calcTransitions(double[][][][] xi) {
		final int N = xi[0][0].length;

		float[][] trans_new = new float[N][N];

		double[][] sum_xi_et = new double[N][N];
		for (int e = 0, e_max = xi.length; e < e_max; ++e) {
			for (int t = 0, t_max = xi[e].length; t < t_max; ++t) {
				double[][] xi_et = xi[e][t];
				for (int i = 0; i < N; ++i) {
					double[] sum_xi_eti = sum_xi_et[i];
					double[] xi_eti = xi_et[i];
					for (int j = 0; j < N; ++j)
						sum_xi_eti[j] += xi_eti[j];
				}
			}
		}

		for (int i = 0; i < N; ++i)
			for (int j = 0; j < N; ++j)
				trans_new[i][j] = (float) sum_xi_et[i][j];

		for (int i = 0; i < N; ++i) {
			float[] trans_new_i = trans_new[i];

			double trans_new_i_sum = 0.0;
			for (int j = 0; j < N; ++j)
				trans_new_i_sum += trans_new_i[j];

			if (trans_new_i_sum > 0.0) {
				for (int j = 0; j < N; ++j)
					trans_new_i[j] /= trans_new_i_sum;
			} else {
				assert Debug.println(
						System.err, "Sum of transitions equals zero");
				Arrays.fill(trans_new_i, 1.0f / N);
			}
		}

		return trans_new;
	}

	public float[] getInit() {
		if (init == null)
			return null;

		float[] tmp = new float[N];
		System.arraycopy(this.init, 0, tmp, 0, this.N);
		return tmp;
	}

	/**
	 * @return Returns copy of transition matrix
	 */
	public float[][] getTransitions() {
		float[][] tmp = new float[N][N];
		for (int i = 0; i < N; ++i)
			System.arraycopy(this.trans[i], 0, tmp[i], 0, this.N);
		return tmp;
	}
	
	/**
	 * get all Gaussian distributions
	 * @return Returns Gaussian distributions of this model
	 */
	public GaussianDistribution[] getDist() {
		GaussianDistribution[] gd = new GaussianDistribution[dist.length];
		System.arraycopy(this.dist, 0, gd, 0, this.dist.length);
		return gd;
	}
	
	/**
	 * Get Gaussian distribution with a given index.
	 * @param index index of Gaussian distribution
	 * @return Returns distribution with a given "index"
	 */

	public GaussianDistribution getDist(int index) {
		return dist[index];
	}
}

class Alpha {
	private final double[][] alpha; // alpha[t][i]
	private final double[] alpha_scal;

	Alpha(
			double[][] dist_prob,
			float[] init,
			float[][] trans) 
	{
		final int T = dist_prob.length;
		final int N = dist_prob[0].length;

		alpha = new double[T][N];
		alpha_scal = new double[T];

		Arrays.fill(alpha_scal, 0.0);
		final double[] alpha_0 = alpha[0];
		for (int i = 0; i < N; ++i)
			alpha_0[i] = dist_prob[0][i];

		if (init != null) {
			for (int i = 0; i < N; ++i)
				alpha_0[i] *= init[i];
		}

		for (int i = 0; i < N; ++i)
			alpha_scal[0] +=
				(alpha_0[i] = Math.max(HMM.FLOAT_MIN_VALUE, alpha_0[i]));

		for (int i = 0; i < N; ++i)
			alpha_0[i] /= alpha_scal[0];

		for (int t = 1; t < T; ++t) {
			double[] alpha_t = alpha[t];
			double[] alpha_t1 = alpha[t - 1];
			double[] dist_prob_t = dist_prob[t];
			double alpha_scal_t = 0.0;
			for (int j = 0; j < N; ++j) {
				double sum = 0.0;
				for (int i = 0; i < N; ++i)
					sum += alpha_t1[i] * trans[i][j];

				alpha_scal_t += (alpha_t[j] = Math.max(HMM.FLOAT_MIN_VALUE,
					sum * dist_prob_t[j]));

				assert!Double.isNaN(alpha_t[j]);
			}

			for (int j = 0; j < N; ++j)
				alpha_t[j] /= (alpha_scal[t] = alpha_scal_t);
		}

		// test values for alpha
		assert testAlpha(alpha, alpha_scal);
	}

	private static boolean testAlpha(double[][] alpha, double[] alpha_scal) {
		for (int t = 0, t_max = alpha.length; t < t_max; ++t) {
			assert!Double.isNaN(alpha_scal[t]);
			assert!Double.isInfinite(alpha_scal[t]);
			assert alpha_scal[t] >= Float.MIN_VALUE;

			double[] alpha_t = alpha[t];
			for (int i = 0, i_max = alpha_t.length; i < i_max; ++i) {
				assert!Double.isNaN(alpha_t[i]);
				assert!Double.isInfinite(alpha_t[i]);
			}
		}
		return true;
	}

	public double[][] getAlpha() {
		return alpha;
	}

	public double[] getAlphaScal() {
		return alpha_scal;
	}
}

class Betha {
	private final double[][] betha; // betha[t][i]
	private final double[] betha_scal;

	Betha(double[][] dist_prob, float[][] trans) {
		final int T = dist_prob.length;
		final int N = dist_prob[0].length;

		betha = new double[T][N];
		betha_scal = new double[T];

		Arrays.fill(betha[T - 1], 1.0 / N);
		betha_scal[T - 1] = N;

		for (int t = T - 1; t > 0; --t) {
			double[] betha_t = betha[t];
			double[] betha_t1 = betha[t - 1];
			double[] dist_prob_t = dist_prob[t];
			double betha_scal_t1 = 0.0;

			for (int i = 0; i < N; ++i) {
				float[] trans_i = trans[i];
				double sum = HMM.FLOAT_MIN_VALUE;
				for (int j = 0; j < N; ++j)
					sum += trans_i[j] * dist_prob_t[j] * betha_t[j];
				betha_scal_t1 += (betha_t1[i] = sum);
			}

			for (int i = 0; i < N; ++i)
				betha_t1[i] /= (betha_scal[t - 1] = betha_scal_t1);
		}

		assert testBetha(betha, betha_scal);
	}

	private static boolean testBetha(
			double[][] betha,
			double[] betha_scal) 
	{
		for (int t = 0, t_max = betha.length; t < t_max; ++t) {
			assert(!Double.isNaN(betha_scal[t]));
			assert(!Double.isInfinite(betha_scal[t]));
			assert betha_scal[t] >= Float.MIN_VALUE;

			double[] betha_t = betha[t];
			for (int i = 0; i < betha_t.length; ++i) {
				assert(!Double.isNaN(betha_t[i]));
				assert(!Double.isInfinite(betha_t[i]));
				assert betha_t[i] >= Float.MIN_VALUE;
			}
		}

		return true;
	}

	public double[][] getBetha() {
		return betha;
	}

	public double[] getBethaScal() {
		return betha_scal;
	}
}

