package org.spantus.extractor.segments.online;

import java.util.Iterator;
import java.util.ListIterator;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.extractor.segments.offline.ExtremeOfflineClassifier;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.logger.Logger;
import org.spantus.math.IndexValue;

public class ExtremeOfflineRuleClassifier extends ExtremeOnlineRuleClassifier {

	private Logger log = Logger.getLogger(ExtremeOfflineRuleClassifier.class);

	public ExtremeOfflineRuleClassifier() {
		super();
	}


	/**
	 * 
	 */
	@Override
	public void flush() {
		super.flush();
		
		log.debug("[flush] recalculating ");
		
		getThresholdValues().clear();
		getOnlineCtx().setIndex(0);
		getMarkSet().getMarkers().clear(); 
		getOnlineCtx().getExtremeSegments().clear();
		getOnlineCtx().setCurrentSegment(null);

		getOnlineCtx().setPreviousValue(null);
		getOnlineCtx().setSkipLearn(Boolean.TRUE);
		for (Float value : getOutputValues()) {
			processValue(value);
		}
		endupPendingSegments(getOnlineCtx());
		processMarkers(getMarkSet());
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));
	}

	protected void processMarkers(MarkerSet markerSet){
		
		
		
		ExtremeSegment previous = null;
		for (Iterator<Marker> iterator = markerSet.iterator(); iterator.hasNext();) {
			ExtremeSegment extremeSegment = (ExtremeSegment) iterator.next();
			if(previous == null){
				previous = extremeSegment;
				continue;
			}
			long prevEnd = previous.getEnd();
			long currentStart = extremeSegment.getStart();
//
//			IndexValue currentArg = VectorUtils.minArg(extremeSegment.getValues());
//			IndexValue prevArg = VectorUtils.minArg(previous.getValues());
//			long length = previous.getLength();
			//segments together
			if((currentStart-prevEnd)<10){
				if((previous.getValues().getLast()*extremeSegment.getValues().getFirst())>(previous.getValues().getFirst()*extremeSegment.getValues().getLast())){
					previous.getValues().addAll(extremeSegment.getValues());
					previous.setLength(previous.getLength()+extremeSegment.getLength());
					iterator.remove();
					continue;
				}
				//segments long distance
			}else if((currentStart-prevEnd)>300){
					int fixUpTo=10;
					
					int i = 0; 
					IndexValue minValue =new IndexValue(extremeSegment.getStartEntry().getIndex(), extremeSegment.getStartEntry().getValue()); 
//					for (ListIterator<Float> valueIter = getOutputValues().listIterator(extremeSegment.getStartEntry().getIndex()); 
//					valueIter.hasPrevious();) {
//						Float value = (Float) valueIter.previous();
//						if(i>fixUpTo){
//							break;
//						}
//						if(minValue.getValue()>value){
//							minValue.setValue(value);
//							minValue.setIndex(extremeSegment.getStartEntry().getIndex()-i);
//							extremeSegment.setStart(getOutputValues().indextoMils(minValue.getIndex()));
//						}
//						i++;
//					}
//					
					i = 0;
					minValue =new IndexValue(previous.getEndEntry().getIndex(), previous.getEndEntry().getValue()); 
					for (ListIterator<Float> valueIter = getOutputValues().listIterator(previous.getEndEntry().getIndex()); 
					valueIter.hasNext();) {
						Float value = (Float) valueIter.next();
						if(i>fixUpTo){
							break;
						}
						if(minValue.getValue()>value){
							minValue.setValue(value);
							minValue.setIndex(previous.getEndEntry().getIndex()+i);
							previous.setEnd(getOutputValues().indextoMils(minValue.getIndex()));
						}
						i++;
					}
					
			}
				
			previous = extremeSegment;
		}
		
		
		
	}
}
