package org.spantus.extr.wordspot.service.impl;

import java.util.*;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.core.service.CorpusService;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.extr.wordspot.service.WordSpottingListener;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.utils.StringUtils;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.3
 * Created: May 7, 2012
 *
 */
public class WordSpottingListenerLogImpl implements WordSpottingListener {

	private static final Logger LOG = Logger
			.getLogger(WordSpottingListenerLogImpl.class);
	
        private List<RecognitionResult> wordMatches = new ArrayList<RecognitionResult>();
         private Map<RecognitionResult, SignalSegment> wordSegments = new HashMap<RecognitionResult, SignalSegment>();


	private List<SignalSegment> signalSegmentsSyllable = new ArrayList<SignalSegment>();
	
	private CorpusService corpusServiceWord;
	private String repositoryPathWord;

	private String target;

	public WordSpottingListenerLogImpl(String target, String repositoryPathSyllable) {
		this.repositoryPathWord = repositoryPathSyllable;
		this.target = target;
	}
	
	@Override
	public String foundSegment(String sourceId, SignalSegment newSyllable,  List<RecognitionResult> recognitionResults){
		
		RecognitionResult rtnRecognitionResult = null;
		
		int index = 0; 
		for (RecognitionResult recognitionResult : recognitionResults) {
			//take first one as default
			if(rtnRecognitionResult == null){
				rtnRecognitionResult = recognitionResult;
			}
                        Set<String> acceptableSyllableSet = new HashSet<String>();
                        acceptableSyllableSet.add("liet");
                         acceptableSyllableSet.add("uvos");
			if(acceptableSyllableSet.contains(recognitionResult.getInfo().getName().toLowerCase())){
				RecognitionResult wordMatch = matchWord(signalSegmentsSyllable, newSyllable);
				if(wordMatch != null){
					getWordMatches().add(wordMatch);
					rtnRecognitionResult= wordMatch;
					signalSegmentsSyllable.clear();
					break;
				}
				signalSegmentsSyllable.add(newSyllable);
			}
			
			//check for first 3 matches
			if(++index>3){
				break;
			}
		}
		String rtnName = null;
		if(rtnRecognitionResult!=null){
			rtnName = rtnRecognitionResult.getInfo().getName();
		}
	
		
		LOG.debug("[processEndedSegment] spotted: {0} in time [{1}:{2}] ",
				rtnName, newSyllable.getMarker().getStart(),
				newSyllable.getMarker().getEnd());
		return rtnName;
	}

	private RecognitionResult matchWord(List<SignalSegment> existingSyllableSegments, SignalSegment newSyllable){
		if(existingSyllableSegments.isEmpty()){
			return null;
		}
		SignalSegment lastSyllable = existingSyllableSegments.get(existingSyllableSegments.size()-1);
		SignalSegment segmentWord = new SignalSegment();
                LOG.debug("[matchWord] merge {0}[{2}] to {1}[{3}]", 
                         lastSyllable.getMarker(), 
                         newSyllable.getMarker(),
                         lastSyllable.getMarker().getStart(),
                         newSyllable.getMarker().getEnd());
                if(lastSyllable.getMarker().getStart()>newSyllable.getMarker().getEnd()){
                    LOG.debug("[matchWord] existing syllable +++++");
                    for (SignalSegment signalSegment : existingSyllableSegments) {
                        LOG.debug("[matchWord] existing syllable: {0}", 
                             signalSegment.getMarker() 
                        );
                    }
                    LOG.debug("[matchWord] existing syllable -----");
                }
               
                 
                Assert.isTrue(lastSyllable.getMarker().getStart()<newSyllable.getMarker().getEnd(), "last syllable should start befor new is finished");
		segmentWord.setMarker(new Marker());
		segmentWord.getMarker().setStart(lastSyllable.getMarker().getStart());
		segmentWord.getMarker().setEnd(newSyllable.getMarker().getEnd());

		Map<String, FrameVectorValuesHolder> syllableFeatureMap = segmentWord.getFeatureFrameVectorValuesMap();
		FrameVectorValuesHolder lastSyllableMFCC = lastSyllable.getFeatureFrameVectorValuesMap().get(ExtractorEnum.MFCC_EXTRACTOR.name());
		FrameVectorValuesHolder newSyllableMFCC = newSyllable.getFeatureFrameVectorValuesMap().get(ExtractorEnum.MFCC_EXTRACTOR.name());
		FrameVectorValuesHolder matchHolder = new FrameVectorValuesHolder(new FrameVectorValues(lastSyllableMFCC.getValues()));
		matchHolder.getValues().addAll(newSyllableMFCC.getValues());
		syllableFeatureMap.put(ExtractorEnum.MFCC_EXTRACTOR.name(), matchHolder);
                
                List<RecognitionResult> result1 = getCorpusServiceWord().findMultipleMatchFull(segmentWord);
                for (RecognitionResult recognitionResult : result1) {
                    LOG.debug("[matchWord] result: {0} [{1}]", recognitionResult.getInfo().getName(), 
                            recognitionResult.getDetails().getDistances());
                }
                
                RecognitionResult result = null;
//		RecognitionResult result = getCorpusServiceWord().matchByCorpusEntry(segmentWord);
//                getWordSegments().put(result, segmentWord);
//		if(result.getDistance().compareTo(80D)>0){
//			return null;
//		}
		return result;
	}
	
	public CorpusService getCorpusServiceWord() {
		if(corpusServiceWord == null){
			Assert.isTrue(StringUtils.hasText(repositoryPathWord), "Repository path not set");
			corpusServiceWord = RecognitionServiceFactory.createCorpusService(repositoryPathWord);
		}
		return corpusServiceWord;
	}
	public List<RecognitionResult> getWordMatches() {
            return wordMatches;
        }

        public Map<RecognitionResult, SignalSegment> getWordSegments() {
            return wordSegments;
        }

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
