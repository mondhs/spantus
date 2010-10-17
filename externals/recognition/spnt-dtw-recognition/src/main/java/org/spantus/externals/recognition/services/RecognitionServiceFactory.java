/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.externals.recognition.services;

/**
 *
 * @author mondhs
 */
public abstract class RecognitionServiceFactory {

    private static CorpusService corpusService;

     public static CorpusService createCorpusService(){
        if(corpusService == null){
            corpusService = new CorpusServiceBaseImpl();
        }
        return corpusService;
     }
}
