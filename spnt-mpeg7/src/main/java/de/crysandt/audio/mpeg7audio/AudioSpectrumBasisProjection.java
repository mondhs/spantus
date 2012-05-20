/*
  Copyright (c) 2002-2003, Holger Crysandt
  Contributed by Felix Engel

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.*;

/**
 * <p/>
 * This class implements
 * <ol>
 * <li><i>AudioSpectrumBasisType</i> and</li>
 * <li><i>AudioSpectrumProjectionType</i></li>
 * </ol>
 *
 * These two are complementary and can be used to save
 * a compressed representation of the AudioSpectrumEnvelopeType frames.
 * The default is to calculate the Basis and Projection Matrices every
 * 500ms.<p/>
 *
 * <h3>Reconstruction</h3>
 * An approximation of AudioSpectrumEnvelopeType can be gained by
 * matrix-multiplying
 * <i>AudioSpectrumProjectionType*AudioSpectrumBasisType</i> and afterwards
 * inverting the log-scale norming. The following Matlab Function implements
 * the reconstruction:

 <pre>
 function [ase]=ASPBReconstruction(ASB,ASP)
 [a,b]=size(ASB);
 [c,d]=size(ASP);
 X = ASP(:,2:d)*ASB';
 X = X .* ((ASP(:,1))*ones(1,a));
 X = X ./ 10;
 ase = (10 * ones(c,a)) .^ X;
 </pre>

 with:
 <ul>
  <li>ASB: AudioSpectrumBasis and </li>
  <li>ASP: AudioSpectrumProjection</li>
 </ul>

 *  These operations refert to on block of the AudioSpectrumEnvelopeType. To reconstruct
 *  all ASE-Data the function above has to be called for each pair of blocks.<p/>
 *  This code has been verified against the Matlab-XM reference implementation
 *  dated 08-2003.<p/>
 *
 * @author Felix Engel,
 *         <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a><p/>
 */
public class AudioSpectrumBasisProjection
    extends MsgSpeaker
    implements MsgListener
{
    private final int NUM_FRAMES;
    private final int NUM_IC;

    private final ArrayList<float[]> ase_list = new ArrayList<float[]>();

    private MsgAudioSpectrumEnvelope msg_first = null;

    /**
     *
     * @param frames The number of time vectors that add up to
     * a time of 500ms.
     *
     * @param num_ic The number of significant values to retain.
     * This is typically in the range 3-10, accordig to the
     * Standard.
     */
    AudioSpectrumBasisProjection(int frames, int num_ic) {
        this.NUM_FRAMES = frames;
        this.NUM_IC = num_ic;
    }

    /**
     * Receives a message. This method expects to receive messages
     * of type MsgAudioSpectrumEnvelope and saves each received message
     * until it has gathered enough data to start computing.<p/>
     *
     * According to the MPEG7-Standard, blocks with a length of 500ms
     * (PT500N1000F) should be analyzed.
     */
    public void receivedMsg(Msg m) {
        if (m instanceof MsgAudioSpectrumEnvelope)
            receivedMsg((MsgAudioSpectrumEnvelope) m);
    }

    private void receivedMsg(MsgAudioSpectrumEnvelope m) {

        // Save the message for now to get enough samples
        if (msg_first == null)
            msg_first = m;

        ase_list.add(m.getEnvelope());

        if (NUM_FRAMES > 0) {
            if (ase_list.size() == NUM_FRAMES)
                calculate();
        } else {
            /* Process the whole file in one piece!
             * So we just wait for a call to flush() and do nothing here
             */
        }
    }

    private void calculate() {
        if (ase_list.isEmpty())
            return;

        float[] first = (float[]) ase_list.get(0);
        if (first.length > ase_list.size())
            return;

        /* This calculates Basis Functions for a whole chunk of segments
         * of audio data
         */
        Calculate calc = new Calculate();
        
        ase_list.clear();

        // Send the results for this block to whoever is interested
        send(new MsgAudioSpectrumBasisProjection(
            msg_first.time,
            msg_first.hopsize * NUM_FRAMES,
				msg_first.hopsize,
            msg_first.lo_edge,
            msg_first.hi_edge,
            msg_first.resolution,
            calc.getBasis(),
            calc.getProjection()));
    }

    public void flush() {
        if (NUM_FRAMES <= 0)
            calculate();
        ase_list.clear();
        super.flush();
    }

    /**
     * Create the AudioSpectrumProjection-matrix from a
     * AudioSpectrumEnvelope-matrix and the AudioSpectrumBasis-matrix
     * @param asb AudioSpectrumBasis (stored row-by-row)
     * @param ase AudioSpectrumEnvelope (stored row-by-row)
     * @return Return AudioSpectrumProjection-matrix
     */
    public static float[][] getASP(float[][] asb, float[][] ase) {
        float[][] ase_log = new float[ase.length][];
        float[] ase_log_sum = new float[ase.length];
        for (int i = 0; i < ase.length; ++i) {
            float[] row = ase[i];
            float[] row_log = (ase_log[i] = new float[row.length]);
            double sum = 0.0;
            
            for (int j = 0; j < row.length; ++j) {
                float row_log_j = row_log[j] = 10.0f / (float) Function.LOG10 * 
					 		(float) Math.log(row[j] + Float.MIN_VALUE);
                
                sum += row_log_j * row_log_j;
            }
            
            ase_log_sum[i] = (float) (sum = Math.sqrt(sum));
            
            for (int j = 0; j < row_log.length; ++j)
                row_log[j] /= sum;
        }

        asb = LinAlg.transpose(asb);
        int cols = asb.length;
        
        float[][] asp = new float[ase.length][cols + 1];
        for (int i = 0; i < asp.length; ++i) {
            float[] ase_log_row = ase_log[i];
            float[] asp_row = asp[i];
            asp_row[0] = ase_log_sum[i];
            for (int j = 0; j < asb.length; ++j)
                asp_row[j+1] = LinAlg.dot(ase_log_row, asb[j]);
        }

        return asp;
    }

    /**
     * Calculate the basis functions and the projection values.
     *
     * This computes
     * <ul>
     * <li>A set of basis Functions using SVD (basisFunctionsMatrix)</li>
     * <li>A Projection of the Spectrum Envelope using these basis
     * functions (projectionMatrix)</li>
     * </ul>
     *
     * @param basisFunctionMatrix After succesful execution the matrix will
     * contain the basis functions
     *
     * @param projectionMatrix After successful execution the matrix will
     * contain the projection against the basis functions.
     */
    private class Calculate {
        private float[][] basisFunctionMatrix;
        private float[][] projectionMatrix;

        public Calculate() {
            int m = ase_list.size();
            int n = ((float[]) ase_list.get(0)).length;
            
            float[][] X = new float[m][];
            
            float[][] ase = new float[m][];
            ase_list.toArray(ase);

            // Vector to remember the L2-Norm Values
            float[] l2NormValues = new float[m];

            for (int row = 0; row < ase.length; ++row) {
                float[] ase_row = ase[row];
                float[] X_row = (X[row] = new float[ase_row.length]);
                float norm = 0.0f;

                for (int i = 0; i < X_row.length; i++) {
                    //Convert to decibel scale
                    X_row[i] = 10.0f *
                        Function.log10(ase_row[i] + Float.MIN_VALUE);

                    // Calculate the L2-Norm
                    norm += X_row[i] * X_row[i];
                }
                l2NormValues[row] = norm = (float) Math.sqrt(norm);

                // Scale the Vector
                for (int i = 0; i < X_row.length; ++i)
                    X_row[i] /= norm;
            } // Next Vector

            SVD svd = new SVD(X, m, n);

            //U = singularValueDecomposition.getU();
          //commented out by mondhs           float[][] S = svd.getS();
            float[][] V = svd.getV();

            /* Caclculate the error made */
            //		Sum of diagonal elements
//commented out by mondhs
//            float sumFull = 0;
//            float sumReduced = 0;
//            for (int i = 0; i < n; i++) {
//                if (i < NUM_IC)
//                    sumReduced += S[i][i];
//                sumFull += S[i][i];
//            }
//\commented out by mondhs

//            float error = sumReduced / sumFull;

            // Dimension Reduction - keep NUM_IC columns of V
            basisFunctionMatrix = new float[n][NUM_IC];
            for (int i = 0; i < n; ++i)
                System.arraycopy(V[i], 0, basisFunctionMatrix[i], 0, NUM_IC);

                // Generate the projection vectors
//            float[] basisFunction = new float[n];
/*            
            projectionMatrix = new float[m][NUM_IC + 1];
            for (int i = 0; i < m; ++i) {
                float[] projectedVector = projectionMatrix[i];
                float[] timeFrame = X[i];

                // First element is the L2 norm of the frame
                projectedVector[0] = l2NormValues[i];

                for (int j = 0; j < NUM_IC; ) {
                    float sum = 0.0f;
                    for (int k = 0; k < n; ++k)
                        sum += basisFunctionMatrix[k][j] * timeFrame[k];
                    projectedVector[++j] = sum;
                }
            }

*/
            projectionMatrix = AudioSpectrumBasisProjection.getASP(
            		basisFunctionMatrix, ase);
        }

        public float[][] getBasis() {
            return basisFunctionMatrix;
        }

        public float[][] getProjection() {
            return projectionMatrix;
        }
    } // End of class calculate
}
