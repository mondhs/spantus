/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spnt.recognition.hmm.test;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;

/**
 *
 * @author mondhs
 */
public class LearnTest {

    final static private double DELTA = 5.E-3;
    public static final Logger LOG = Logger.getLogger(LearnTest.class);
//    private Hmm<ObservationVector> hmm;
    private List<List<ObservationVector>> firstSeqs;
    private List<List<ObservationVector>> first2Seqs;
    private List<List<ObservationVector>> secondSeqs;

    @Before
    public void setUp() throws IOException, FileFormatException {

//        Double[] first = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 101D, 102D, 103D, 104D, 105D, 106D, 107D, 108D, 109D};
//        Double[] first2 = new Double[]{1D, 1D, 3D, 4D, 5D, 5D, 7D, 8D, 9D, 9D, 102D, 103D, 104D, 104D, 106D, 107D, 109D, 109D};
//        Double[] second = new Double[]{19D, 18D, 17D, 16D, 15D, 14D, 13D, 12D, 11D,19D, 18D, 17D, 16D, 15D, 14D, 13D, 12D, 11D,  };

        Double[] first = new Double[] {1D, 1D, 1D, 1D, 2D, 2D, 2D, 2D, 3D, 3D, 3D, 2D, 2D, 2D, 1D, 1D, 1D, 1D};
        Double[] first2 = new Double[]{1D, 1D, 1D, 1D, 1D, 2D, 2D, 2D, 2D, 3D, 3D, 3D, 3D, 2D, 2D, 1D, 1D, 1D};
        Double[] second = new Double[]{3D, 3D, 3D, 3D, 3D, 2D, 2D, 2D, 1D, 1D, 1D, 1D, 2D, 2D, 2D, 2D, 2D, 2D};
        
        firstSeqs = convertToSequence(newFrameVectorValues(first));
        addToSequence(firstSeqs, newFrameVectorValues(first));
        first2Seqs = convertToSequence(newFrameVectorValues(first2));
        this.secondSeqs = convertToSequence(newFrameVectorValues(second));
    }

    private FrameVectorValues newFrameVectorValues(Double[] doubleArray) {
        FrameVectorValues fvv = new FrameVectorValues(1000D);
        for (Double double1 : doubleArray) {
            FrameValues fv = new FrameValues(1D);
            fv.add(double1 + (.1*Math.random()));
            fv.add(double1 + (.1*Math.random()));
//            fv.add(1.0 + double1 * Math.random());
//            fv.add(2.0*double1* Math.random());
            fvv.add(fv);
        }
        return fvv;
    }

    @Test
    public void testLearn() throws IOException {
        int dimension = firstSeqs.get(0).get(0).dimension();
        int numStates = 2;

        KMeansLearner<ObservationVector> kMeansLearner =
                new KMeansLearner<ObservationVector>(numStates,
                new OpdfMultiGaussianFactory(dimension), firstSeqs);
        Hmm<ObservationVector> kMeansHmm = kMeansLearner.iterate();

        BaumWelchScaledLearner learner = new BaumWelchScaledLearner();
//        learner.setNbIterations(150);
        Hmm<ObservationVector> learned = learner.learn(kMeansHmm, firstSeqs);

        LOG.debug("learned: {0}", learned);
        for (List<ObservationVector> seq : firstSeqs) {
            LOG.debug("probability {1} for seq: {0} ", seq, ""+learned.probability(seq));
            LOG.debug("mostLikelyStateSequence {1} for seq: {0} ", seq, 
                    Arrays.toString(learned.mostLikelyStateSequence(seq)));
            LOG.debug("lnProbability {1} for seq: {0}; ", seq, ""+learned.lnProbability(seq));
        }
        LOG.debug("First2");
        for (List<ObservationVector> seq : first2Seqs) {
            LOG.debug("probability {1} for seq: {0} ", seq, ""+learned.probability(seq));
            LOG.debug("mostLikelyStateSequence {1} for seq: {0} ", seq, 
                    Arrays.toString(learned.mostLikelyStateSequence(seq)));
            LOG.debug("lnProbability {1} for seq: {0}; ", seq, ""+learned.lnProbability(seq));
        }
        
        LOG.debug("Second");
        for (List<ObservationVector> seq : secondSeqs) {
            LOG.debug("probability {1} for seq: {0} ", seq, ""+learned.probability(seq));
            LOG.debug("mostLikelyStateSequence {1} for seq: {0} ", seq, 
                    Arrays.toString(learned.mostLikelyStateSequence(seq)));
            LOG.debug("lnProbability {1} for seq: {0}; ", seq, learned.lnProbability(seq));
        }

    }
    
     @Test
    public void testRecognise() throws IOException {
         
     }

    private List<List<ObservationVector>> convertToSequence(FrameVectorValues fvv) {
        List<List<ObservationVector>> result = new ArrayList<List<ObservationVector>>(fvv.size());
        List<ObservationVector> result2 = new ArrayList<ObservationVector>(1);
        for (List<Double> fv : fvv) {
            ObservationVector currSequence = new ObservationVector(VectorUtils.todoubleArray(fv));
            result2.add(currSequence);
        }
        result.add(result2);
        return result;
    }
    private List<List<ObservationVector>> addToSequence(List<List<ObservationVector>> vectors, FrameVectorValues fvv) {
        List<List<ObservationVector>> result =  vectors;
        List<ObservationVector> result2 = new ArrayList<ObservationVector>(1);
        for (List<Double> fv : fvv) {
            ObservationVector currSequence = new ObservationVector(VectorUtils.todoubleArray(fv));
            result2.add(currSequence);
        }
        result.add(result2);
        return result;
    }

}
