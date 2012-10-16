/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.externals.recognition.services;

import java.util.HashSet;
import java.util.Map;

import org.spantus.core.IValues;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.math.dtw.JavaMLLocalConstraint;
import org.spantus.math.dtw.JavaMLSearchWindow;

/**
 *
 * @author mondhs
 */
public final class RecognitionServiceFactory {

    private static CorpusService corpusService;
    
    private RecognitionServiceFactory(){}

     public static CorpusService createCorpusService(){
        if(corpusService == null){
            CorpusServiceBaseImpl corpusServiceimpl = new CorpusServiceBaseImpl();
            corpusServiceimpl.setIncludeFeatures(new HashSet<String>());
//            corpusServiceimpl.getIncludeFeatures().add("MFCC_EXTRACTOR");
//             corpusServiceimpl.getIncludeFeatures().add("LPC_EXTRACTOR");
//             corpusServiceimpl.getIncludeFeatures().add("FFT_EXTRACTOR");
            corpusService = corpusServiceimpl;
        }
        return corpusService;
     }
     
     public static CorpusService createCorpusService(String repositoryPath){
//         if(corpusService == null){
             CorpusServiceBaseImpl corpusServiceimpl = new CorpusServiceBaseImpl();
             corpusServiceimpl.setIncludeFeatures(new HashSet<String>());
//             corpusServiceimpl.getIncludeFeatures().add("MFCC_EXTRACTOR");
//              corpusServiceimpl.getIncludeFeatures().add("LPC_EXTRACTOR");
//              corpusServiceimpl.getIncludeFeatures().add("FFT_EXTRACTOR");
         	  CorpusRepositoryFileImpl corpus = new CorpusRepositoryFileImpl();
         	 corpus.setRepositoryPath(repositoryPath);
             corpusServiceimpl.setCorpus(corpus);
             corpusService = corpusServiceimpl;
//         }
         return corpusService;
      }
     
          public static CorpusService createCorpusServicePartialSearch(String repositoryPath, Float radius, 
                  String... includeExtractors){
//         if(corpusService == null){
             CorpusServiceBaseImpl corpusServiceimpl = new CorpusServiceBaseImpl();
             
             corpusServiceimpl.setSearchRadius(radius);
             corpusServiceimpl.setJavaMLSearchWindow(JavaMLSearchWindow.ParallelogramWindow);
             corpusServiceimpl.setJavaMLLocalConstraint(JavaMLLocalConstraint.Angle);

             corpusServiceimpl.setIncludeFeatures(new HashSet<String>());
              for (String extractor : includeExtractors) {
                  corpusServiceimpl.getIncludeFeatures().add(extractor);
              }
                CorpusRepositoryFileImpl corpus = new CorpusRepositoryFileImpl();
         	corpus.setRepositoryPath(repositoryPath);
             corpusServiceimpl.setCorpus(corpus);
             corpusService = corpusServiceimpl;
//         }
         return corpusService;
      }
     
     public static SignalSegment createSignalSegment(String label,
 			Map<String, IValues> featureDataMap){
 		CorpusFileEntry corpusEntry = new CorpusFileEntry();
 		corpusEntry.setName(label);
 		corpusEntry.putAll(featureDataMap);
 		return corpusEntry;
     }
  
}
