package org.spantus.extr.wordspot.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.extr.wordspot.service.SegmentRecognitionThresholdService;
import org.spantus.extractor.impl.ExtractorEnum;

public class SegmentRecognitionThresholdServiceImpl implements
		SegmentRecognitionThresholdService {


	private static final Logger LOG = LoggerFactory
			.getLogger(SegmentRecognitionThresholdServiceImpl.class);
	private Map<String, Double> acceptableSyllableThresholdMap;
	private AcceptableSyllableThresholdDaoImpl acceptableSyllableThresholdDaoImpl;
	private String extractorEnum = ExtractorEnum.MFCC_EXTRACTOR.name();
	
	
	public SegmentRecognitionThresholdServiceImpl(String repositoryPathWord) {
		this(repositoryPathWord, null);
	}
	
	/**
	 * 
	 * @param repositoryPathWord CORPORA/word
	 * @param type - phone/word
	 */
	public SegmentRecognitionThresholdServiceImpl(String repositoryPathWord, String type) {
//		this.repositoryPathWord = repositoryPathWord;
		acceptableSyllableThresholdMap = new HashMap<>();
		acceptableSyllableThresholdMap.put("liet", 6E9);
		acceptableSyllableThresholdMap.put("tuvos", 8E10);
		getAcceptableSyllableThresholdDaoImpl().write(acceptableSyllableThresholdMap);
		Map<String, Double> temp = getAcceptableSyllableThresholdDaoImpl().read(repositoryPathWord, type);
		if(temp!=null){
			acceptableSyllableThresholdMap = temp;
		}
	}

	@Override
    public boolean checkIfBellowThreshold(String syllableName, RecognitionResult recognitionResult) {
		Double mfccVaue = recognitionResult.getDetails().getDistances()
				.get(extractorEnum);
            Double threshold = findSegmentThreshold(syllableName);
            //if is not defined do not filter
            if(threshold == null){
                return true;
            }
            
            boolean bellowThreshold = mfccVaue<threshold;
            if(!bellowThreshold){
                LOG.debug("[processEndedSegment] reject syllable {} and mfcc: {}",syllableName, mfccVaue);
            }
            return bellowThreshold ;
    }
	
	@Override
    public boolean checkIfFirstDeltaGreater(List<RecognitionResult> resultList, Double thresholdDelta) {
        if(resultList == null || resultList.size()<2){
            return true;
        }
        RecognitionResult first = resultList.get(0);
        Double firstScore = first.getScores().get(extractorEnum);
        RecognitionResult second = resultList.get(1);
        Double secondScore = second.getScores().get(extractorEnum);
        boolean sameLabels = first.getInfo().getName().equals(second.getInfo().getName());
        double scoreDelta = secondScore - firstScore;
        if(!sameLabels){
            return scoreDelta > thresholdDelta;
        }
        return true;
    }

	@Override
	public Double findSegmentThreshold(String syllableName) {
		if (!acceptableSyllableThresholdMap.containsKey(syllableName)) {
			return null;
		}
		Double threshold = acceptableSyllableThresholdMap.get(syllableName);
		return threshold;
	}

	public AcceptableSyllableThresholdDaoImpl getAcceptableSyllableThresholdDaoImpl() {
		if(acceptableSyllableThresholdDaoImpl == null){
			acceptableSyllableThresholdDaoImpl = new AcceptableSyllableThresholdDaoImpl();
		}
		return acceptableSyllableThresholdDaoImpl;
	}

	public void setAcceptableSyllableThresholdDaoImpl(
			AcceptableSyllableThresholdDaoImpl acceptableSyllableThresholdDaoImpl) {
		this.acceptableSyllableThresholdDaoImpl = acceptableSyllableThresholdDaoImpl;
	}




}
