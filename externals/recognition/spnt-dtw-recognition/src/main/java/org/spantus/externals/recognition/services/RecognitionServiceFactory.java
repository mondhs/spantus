/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.externals.recognition.services;

import java.util.HashSet;

/**
 *
 * @author mondhs
 */
public abstract class RecognitionServiceFactory {

    private static CorpusService corpusService;

     public static CorpusService createCorpusService(){
        if(corpusService == null){
            CorpusServiceBaseImpl corpusServiceimpl = new CorpusServiceBaseImpl();
            corpusServiceimpl.setIncludeFeatures(new HashSet<String>());
            corpusServiceimpl.getIncludeFeatures().add("MFCC_EXTRACTOR");
             corpusServiceimpl.getIncludeFeatures().add("LPC_EXTRACTOR");
             corpusServiceimpl.getIncludeFeatures().add("FFT_EXTRACTOR");
            corpusService = corpusServiceimpl;
        }
        return corpusService;
     }
}
