/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;

import org.junit.Test;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

/**
 * 
 * @author mondhs
 */
public class ClassifyHMMSegmentDirTest extends AbstractSegmentDirTest {

	private static final Logger log = Logger
			.getLogger(ClassifyHMMSegmentDirTest.class);

	
	@Test
	public void testClassify() {
		Dataset data = new DefaultDataset();
		List<List<ObservationVector>> seqOfSeq = Lists.newArrayList();
		ListMultimap<String, ObservationVector> mapVector = ArrayListMultimap.create();
		for (CorpusFileEntry entry : getCorpusRepository().getRepository()
				.values()) {
			IValues values = entry.getFeatureMap().get(
					ExtractorEnum.PLP_EXTRACTOR.name()).getValues();
			FrameVectorValues vectors = (FrameVectorValues) values;
			
			for (List<Float> vector : vectors) {
				double[] doubleVector = new double[vector.size()]; 
				int i = 0;
				for (Float float1 : vector) {
					doubleVector[i++]=float1.doubleValue();
				}
				ObservationVector observationVector = new ObservationVector(doubleVector);
				mapVector.put(entry.getName(), observationVector);
			}
			log.debug(entry.getName());			
		}

		for (Entry<String, Collection<ObservationVector>> entires : mapVector.asMap().entrySet()) {
			List<ObservationVector>hmmVector = Lists.newArrayList();
			hmmVector.addAll(entires.getValue());
			seqOfSeq.add(hmmVector);
		}
		
		
		log.debug("seqOfSeq: {0}",seqOfSeq);
		Hmm<ObservationVector> learntHmm = learnHMM(seqOfSeq);
		try {
			(new GenericHmmDrawerDot()).write(learntHmm, "learntHmm.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("test:{0}", learntHmm);

	}
	
	public Hmm<ObservationVector> learnHMM(List<List<ObservationVector>> sequences) {
        int numberOfHiddenStates = 2;
        Hmm<ObservationVector> trainedHmm = null;
//        do {
        	log.debug("[learnHMM]numberOfHiddenStates: {0}", numberOfHiddenStates);
            KMeansLearner<ObservationVector> kml = new KMeansLearner<ObservationVector>(numberOfHiddenStates,
                    new OpdfMultiGaussianFactory(13), sequences);
            trainedHmm = kml.learn();
            BaumWelchLearner bwl = new BaumWelchLearner();
            bwl.setNbIterations(10);
            trainedHmm = bwl.learn(trainedHmm, sequences);
//            numberOfHiddenStates++;
//        } while (Double.isNaN(trainedHmm.getPi(0)) && numberOfHiddenStates< 2);

        return trainedHmm;
    }

}

