package org.spantus.extractor.segments.online;

import java.util.Iterator;
import java.util.ListIterator;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extractor.segments.offline.ExtremeOfflineClassifier;
import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.logger.Logger;
import org.spantus.math.IndexValue;
import org.spantus.utils.Assert;

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
		getMarkSet().getMarkers().clear(); 
		
		getOnlineCtx().reset();
		
		for (Double value : getOutputValues()) {
			processValue(getOnlineCtx() ,value);
		}
		endupPendingSegments(getOnlineCtx());
		processMarkers(getMarkSet());
		getThresholdValues().addAll(ExtremeOfflineClassifier.refreshThreasholdInfo(getMarkSet(), getOutputValues()));
	}

	protected void processMarkers(MarkerSet markerSet){
		
		for (Iterator<Marker> iterator = markerSet.iterator(); iterator.hasNext();) {
			Marker marker = iterator.next();
			if(marker.getLength()<60){
				iterator.remove();
			}else{
				break;
			}
		}
		
		
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
		if(previous!=null &&  previous.getLength()<120){
			markerSet.getMarkers().remove(previous);
		}
		
		//joint small chunks
		removed = false;
		previous = null;
//		for (Iterator<Marker> iterator = markerSet.iterator(); iterator.hasNext();) {
//			ExtremeSegment extremeSegment = (ExtremeSegment) iterator.next();
//			removed = false;
//			if(previous == null){
//				previous = extremeSegment;
//				continue;
//			}
//			if(extremeSegment.getLength()<60){
//				iterator.remove();
//				removed = true;
//			}
//			if(!removed){
//				previous = extremeSegment;
//			}
//		}
		
		for (Iterator<Marker> iterator = markerSet.iterator(); iterator.hasNext();) {
			ExtremeSegment extremeSegment = (ExtremeSegment) iterator.next();
			extremeSegment.setStart(extremeSegment.getStart()-60);
		}
		
		ExtremeSegment previousSegment  = null;
		for (Iterator<Marker> iterator = markerSet.iterator(); iterator.hasNext();) {
			ExtremeSegment extremeSegment = (ExtremeSegment) iterator.next();
			if(extremeSegment.getStart()<0){
				iterator.remove();
				continue;
			}
			if(previousSegment == null){
				previousSegment =extremeSegment;
				continue;
			}

			if(extremeSegment.getLength()<70 && previousSegment.getLength()>140){
				previousSegment.setEnd(extremeSegment.getEnd());
				iterator.remove();
			}
			previousSegment =extremeSegment;
		}

		
	}

	
	private boolean fixShortSegments(ExtremeSegment previous,ExtremeSegment extremeSegment) {
		Double startEndRatio =(2*previous.getEndEntry().getValue())/(previous.getStartEntry().getValue() + extremeSegment.getEndEntry().getValue());
		Double areaRatio  = 0D;
		if(previous.getCalculatedArea()>extremeSegment.getCalculatedArea()){
			areaRatio = previous.getCalculatedArea()/extremeSegment.getCalculatedArea();
		}else{
			areaRatio = extremeSegment.getCalculatedArea()/previous.getCalculatedArea();
		}

		
		//too big chunks to be merged
		if((previous.getCalculatedLength()>170 && extremeSegment.getCalculatedLength()>170)){
			if(areaRatio>1.5){
				return false;
			}else{
				extremeSegment.getCalculatedLength();
			}
		}
		int fixUpTo=getOutputValues().toIndex(1D);
		int i = 0; 
		IndexValue minValue =new IndexValue(previous.getEndEntry().getIndex(), previous.getEndEntry().getValue()); 
		i = 0;
//		minValue =new IndexValue(previous.getEndEntry().getIndex(), previous.getEndEntry().getValue()); 
		
		if(getOutputValues().size()<previous.getEndEntry().getIndex()){
//			throw new ProcessingException("size less than index");
			return false;
		}
		boolean changed = false;
		for (ListIterator<Double> valueIter = getOutputValues().listIterator(previous.getEndEntry().getIndex()); 
		valueIter.hasNext();) {
			Double value = (Double) valueIter.next();
			if(i>fixUpTo){
				break;
			}
			if(previous.getEndEntry().getIndex()+i>extremeSegment.getEndEntry().getIndex()){
				break;
			}
			if(minValue.getValue()>value){
				minValue.setValue(value);
				minValue.setIndex(previous.getEndEntry().getIndex()+i);
				Long newChangePoint = getOutputValues().indextoMils(minValue.getIndex());
				Long delta = newChangePoint-previous.getEnd();
				if(delta>150){
					changed = false;
					break;
				}
				
				previous.setEnd(newChangePoint);
				previous.setEndEntry(new ExtremeEntry(minValue.getIndex(), minValue.getValue(), FeatureStates.min));
//				previous.setStartEntry(new ExtremeEntry(minValue.getIndex(), minValue.getValue(), FeatureStates.min));

				if(extremeSegment.getLength()-delta<20){
					changed = true;
					break;
				}

				extremeSegment.setStart(newChangePoint);
				extremeSegment.setStartEntry(new ExtremeEntry(minValue.getIndex(), minValue.getValue(), FeatureStates.min));
				extremeSegment.setEnd(extremeSegment.getEnd()-delta);
				changed =false;
//				return true;
			}
			i++;
		}
		if(!changed){
			
			Assert.isTrue(previous.getEnd().compareTo(extremeSegment.getStart())<=0, "Start cannot be before end: "+ previous.getEnd() + ">" + extremeSegment.getStart());
			Assert.isTrue(previous.getEndEntry().after(extremeSegment.getStartEntry()), 
					"Start index cannot be before end: " +  previous.getEndEntry().getIndex() + ">" + extremeSegment.getStartEntry().getIndex());
		}
		return changed;
	}
	/**
	 * 
	 * @param previous
	 * @param extremeSegment
	 */
	private void fixFarSegments(ExtremeSegment previous,ExtremeSegment extremeSegment) {
		int fixUpTo= getOutputValues().toIndex(.3D);
		int i = 0; 
		IndexValue minValue = null;

		i = 0;
		minValue =new IndexValue(previous.getEndEntry().getIndex(), previous.getEndEntry().getValue()); 
		if(getOutputValues().size()<previous.getEndEntry().getIndex()){
//			throw new ProcessingException("size less than index");
			return ;
		}
		for (ListIterator<Double> valueIter = getOutputValues().listIterator(previous.getEndEntry().getIndex()); 
		valueIter.hasNext();) {
			Double value =valueIter.next();
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
