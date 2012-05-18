package org.spantus.extr.wordspot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	private List<SignalSegment> signalSegments = new ArrayList<SignalSegment>();
	private List<SignalSegment> signalSegmentsLevel1 = new ArrayList<SignalSegment>();
	
	private CorpusService corpusServiceLevel2;
	private String repositoryPathLevel2;

	private String target;

	public WordSpottingListenerLogImpl(String target, String repositoryPathLevel2) {
		this.repositoryPathLevel2 = repositoryPathLevel2;
		this.target = target;
	}
	
	@Override
	public String foundSegment(String sourceId, SignalSegment newSegment,  List<RecognitionResult> recognitionResults){
		
		RecognitionResult rtnRecognitionResult = null;
		
		int index = 0; 
		for (RecognitionResult recognitionResult : recognitionResults) {
			//take first one as default
			if(rtnRecognitionResult == null){
				rtnRecognitionResult = recognitionResult;
			}
			if(recognitionResult.getInfo().getName().toLowerCase().startsWith("Liet".toLowerCase()) ||
					recognitionResult.getInfo().getName().toLowerCase().startsWith("uvos".toLowerCase())
					){
				RecognitionResult level2Match = matchLevel2(signalSegmentsLevel1, newSegment);
				if(level2Match != null){
					getSignalSegments().add(newSegment);
					rtnRecognitionResult= level2Match;
					signalSegmentsLevel1.clear();
					break;
				}
				signalSegmentsLevel1.add(newSegment);
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
				rtnName, newSegment.getMarker().getStart(),
				newSegment.getMarker().getEnd());
		return rtnName;
	}

	private RecognitionResult matchLevel2(List<SignalSegment> signalSegments, SignalSegment newSegment){
		if(signalSegments.size() == 0){
			return null;
		}
		SignalSegment lastSegment = signalSegments.get(signalSegments.size()-1);
		SignalSegment segmentLevel2 = new SignalSegment();
		segmentLevel2.setMarker(new Marker());
		segmentLevel2.getMarker().setStart(lastSegment.getMarker().getStart());
		segmentLevel2.getMarker().setEnd(newSegment.getMarker().getEnd());

		Map<String, FrameVectorValuesHolder> level2VectorMap = segmentLevel2.getFeatureFrameVectorValuesMap();
		FrameVectorValuesHolder lastVector = lastSegment.getFeatureFrameVectorValuesMap().get(ExtractorEnum.MFCC_EXTRACTOR.name());
		FrameVectorValuesHolder newVector = newSegment.getFeatureFrameVectorValuesMap().get(ExtractorEnum.MFCC_EXTRACTOR.name());
		FrameVectorValuesHolder matchHolder = new FrameVectorValuesHolder(new FrameVectorValues(lastVector.getValues()));
		matchHolder.getValues().addAll(newVector.getValues());
		level2VectorMap.put(ExtractorEnum.MFCC_EXTRACTOR.name(), matchHolder);
		RecognitionResult result = getCorpusServiceLevel2().matchByCorpusEntry(segmentLevel2);
		if(result.getDistance().compareTo(80D)>0){
			return null;
		}
		return result;
	}
	
	public CorpusService getCorpusServiceLevel2() {
		if(corpusServiceLevel2 == null){
			Assert.isTrue(StringUtils.hasText(repositoryPathLevel2), "Repository path not set");
			corpusServiceLevel2 = RecognitionServiceFactory.createCorpusService(repositoryPathLevel2);
		}
		return corpusServiceLevel2;
	}
	
	public List<SignalSegment> getSignalSegments() {
		return signalSegments;
	}

	public void setSignalSegments(List<SignalSegment> signalSegments) {
		this.signalSegments = signalSegments;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
