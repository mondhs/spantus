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
		processMarkers(getMarkSet());
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));
	}

	protected void processMarkers(MarkerSet markerSet){
		
		
		ExtremeSegment previous = null;
		boolean removed = false;
		for (Iterator<Marker> iterator = markerSet.iterator(); iterator.hasNext();) {
			ExtremeSegment extremeSegment = (ExtremeSegment) iterator.next();
			removed = false;
			if(previous == null){
				previous = extremeSegment;
				continue;
			}
			long prevEnd = previous.getEnd();
			long prevStart = previous.getStart();
			long prevLength = previous.getLength();
			long currentStart = extremeSegment.getStart();
			long currentEnd = extremeSegment.getEnd();
			long currentLength = extremeSegment.getLength();
//
//			IndexValue currentArg = VectorUtils.minArg(extremeSegment.getValues());
//			IndexValue prevArg = VectorUtils.minArg(previous.getValues());
//			long length = previous.getLength();
			//segments together
			if((currentStart-prevEnd)<20){
				removed = 
//					false
					fixShortSegments(previous, extremeSegment)
					;
				if(removed){
					previous.getValues().addAll(
							extremeSegment.getValues());
					previous.setEnd(extremeSegment.getEnd());
					iterator.remove();
				}
				
				//segments long distance
			}else if((currentStart-prevEnd)>300){
					fixFarSegments(previous, extremeSegment);
			}
			if(!removed){
				previous = extremeSegment;
			}
		}
		
		
	}

	private boolean fixShortSegments(ExtremeSegment previous,ExtremeSegment extremeSegment) {
		float ratio =(previous.getValues().getLast()*extremeSegment.getValues().getFirst())/(previous.getValues().getFirst()*extremeSegment.getValues().getLast());
//		if((previous.getValues().getLast()*extremeSegment.getValues().getFirst())>(previous.getValues().getFirst()*extremeSegment.getValues().getLast())){
//			previous.getValues().addAll(extremeSegment.getValues());
//			previous.setLength(previous.getLength()+extremeSegment.getLength());
//			iterator.remove();
//			continue;
//		}
		int fixUpTo=getOutputValues().toIndex(.3F);
		int i = 0; 
		IndexValue minValue =new IndexValue(previous.getEndEntry().getIndex(), previous.getEndEntry().getValue()); 
//		for (ListIterator<Float> valueIter = getOutputValues().listIterator(previous.getEndEntry().getIndex()); 
//		valueIter.hasPrevious();) {
//			Float value = (Float) valueIter.previous();
//			if(i>fixUpTo){
//				break;
//			}
//			if(minValue.getValue()>value){
//				minValue.setValue(value);
//				minValue.setIndex(previous.getEndEntry().getIndex()-i);
//				Long newChangePoint = getOutputValues().indextoMils(minValue.getIndex());
//				Long delta = previous.getEnd()-newChangePoint;
//				if(previous.getLength()-delta<20){
//					return false;
//				}
//				previous.setEnd(newChangePoint);
//				extremeSegment.setStart(newChangePoint);
//				extremeSegment.setLength(extremeSegment.getLength()+delta);
//			}
//			i++;
//		}
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
				Long newChangePoint = getOutputValues().indextoMils(minValue.getIndex());
				Long delta = newChangePoint-previous.getEnd();
				if(extremeSegment.getLength()-delta<20){
					return true;
				}
				previous.setEnd(newChangePoint);
				extremeSegment.setStart(newChangePoint);
				extremeSegment.setLength(extremeSegment.getLength()-delta);
			}
			i++;
		}
		return false;
	}
	/**
	 * 
	 * @param previous
	 * @param extremeSegment
	 */
	private void fixFarSegments(ExtremeSegment previous,ExtremeSegment extremeSegment) {
		int fixUpTo= getOutputValues().toIndex(.3F);
		int i = 0; 
		IndexValue minValue = null;

//		minValue =new IndexValue(extremeSegment.getStartEntry().getIndex(), extremeSegment.getStartEntry().getValue()); 
//		for (ListIterator<Float> valueIter = getOutputValues().listIterator(extremeSegment.getStartEntry().getIndex()); 
//		valueIter.hasPrevious();) {
//			Float value = (Float) valueIter.previous();
//			if(i>fixUpTo){
//				break;
//			}
//			if(minValue.getValue()>value*3){
//				minValue.setValue(value);
//				minValue.setIndex(extremeSegment.getStartEntry().getIndex()-i);
//				long newStart = getOutputValues().indextoMils(minValue.getIndex());
//				long delta = extremeSegment.getStart()-newStart;
//				extremeSegment.setStart(newStart);
//				extremeSegment.setLength(extremeSegment.getLength()+delta);
//			}
//			i++;
//		}
//		
		i = 0;
		minValue =new IndexValue(previous.getEndEntry().getIndex(), previous.getEndEntry().getValue()); 
		for (ListIterator<Float> valueIter = getOutputValues().listIterator(previous.getEndEntry().getIndex()); 
		valueIter.hasNext();) {
			Float value = (Float) valueIter.next();
			if(i>fixUpTo){
				break;
			}
			if(minValue.getValue()>value*2){
				minValue.setValue(value);
				minValue.setIndex(previous.getEndEntry().getIndex()+i);
				previous.setEnd(getOutputValues().indextoMils(minValue.getIndex()));
			}
			i++;
		}		
	}
	
}
