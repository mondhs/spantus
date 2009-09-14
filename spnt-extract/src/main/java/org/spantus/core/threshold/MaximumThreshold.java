package org.spantus.core.threshold;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.spantus.core.threshold.ExtreamEntry.SignalStates;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;

public class MaximumThreshold extends DynamicThreshold{
	

	Logger log = Logger.getLogger(MaximumThreshold.class);
	
	Vector<Float> lastMaxs;
	
	@Override
	public void flush() {
		super.flush();
		getThresholdValues().clear();
		getState().clear();
		Float previous = null;
		Map<Integer, ExtreamEntry> extriems = new TreeMap<Integer, ExtreamEntry>();

		SignalStates state = SignalStates.stable;
		int index = 0;
		Integer prevIndex = null;
		for (Float value : getOutputValues()) {
			if(previous == null) previous = value;
			if(value>previous ){
				state = SignalStates.maxExtream;
			}else {
				if(SignalStates.maxExtream.equals(state)){
					int entryIndex = index-1;
					ExtreamEntry currentExtreamEntry = new ExtreamEntry(entryIndex, previous, state);
					if(prevIndex != null){
						if(entryIndex-prevIndex<10){
							if(currentExtreamEntry.getValue()>extriems.get(prevIndex).getValue()){
								extriems.remove(prevIndex);
								extriems.put(entryIndex, currentExtreamEntry);
								prevIndex = entryIndex;
							}
						}else {
							extriems.put(entryIndex, currentExtreamEntry);
							prevIndex = entryIndex;
						}
					}else{
						extriems.put(entryIndex, currentExtreamEntry);
						prevIndex = entryIndex;
					}
					
				}
				state = SignalStates.decreasing;
			}
			previous = value;
			index++;
		}
		
		index = 0;previous = 0F;prevIndex = null;
		for (Float value : getOutputValues()) {
			if(previous == null) previous = value;
			if(value<previous ){
				state = SignalStates.minExtream;
			}else {
				if(SignalStates.minExtream.equals(state)){
					int entryIndex = index-1;
					ExtreamEntry currentExtreamEntry = new ExtreamEntry(entryIndex, previous, state);
					if(prevIndex != null){
						if(entryIndex-prevIndex<10){
						if(SignalStates.minExtream.equals(extriems.get(prevIndex).getSignalStates()) &&
								 currentExtreamEntry.getValue()<extriems.get(prevIndex).getValue()){
								extriems.remove(prevIndex);
								extriems.put(entryIndex, currentExtreamEntry);
								prevIndex = entryIndex;
							}
						}else{
								extriems.put(entryIndex, currentExtreamEntry);
								prevIndex = entryIndex;
						}
						
					}else{
						extriems.put(entryIndex, currentExtreamEntry);
						prevIndex = entryIndex;
					}
					
				}
				state = SignalStates.increasing;
			}
			previous = value;
			index++;
		}

		
		
		
		ExtreamSequences allMaximasList = new ExtreamSequences(extriems.values(), getOutputValues());
		Set<Integer> maximas = new HashSet<Integer>();
		Set<Integer> minimas = new HashSet<Integer>();
		
		for (ExtreamsListIterator iterator = allMaximasList.extreamsListIterator(); iterator.hasNext();) {
			ExtreamEntry entry = iterator.next();


			if(iterator.isPreviousMinExtream() && iterator.isCurrentMaxExtream()
					&& iterator.isNextMinExtream()){
				
				if(
						iterator.getArea()>160000 
//						&& iterator.getPeakLength()>5
						){
//					maximas.add(entry.getIndex());
//					minimas.add(iterator.getPreviousEntry().getIndex());
//					minimas.add(iterator.getNextEntry().getIndex());
					for (int i = iterator.getPreviousEntry().getIndex()+1; i < iterator.getNextEntry().getIndex()-1; i++) {
						maximas.add(i);
					}

					iterator.logCurrent();
				}
			}else if(!iterator.isPreviousMinExtream() && iterator.isCurrentMaxExtream()){
				
				
			}else if(entry.getSignalStates().equals(SignalStates.minExtream)){
//				minimas.add(entry.getIndex());
			}
		}
		

		
		
		index = 0;
		for (Float value : getOutputValues()) {
			if(maximas.contains(index)){
				getState().add(1F);
				getThresholdValues().add(value);	 
			}else if(minimas.contains(index)){
					getState().add(.0F);
					getThresholdValues().add(value);	
			}else{
				getState().add(0F);
				getThresholdValues().add(0F);	
				
			}
			index++;
		}
	}
	
//	@Override
//	protected void processDiscriminator(Long sample, Float value) {
//		if(previous == null){
//			previous = value;
//		}
//		getThresholdValues().add(0F);			
//		
//		if(value>previous){
//			getState().add(1F);
//		}else {
//			getState().add(0F);
//		}
//		previous = value;
//	}
	
	@Override
	protected boolean isTrained() {
		return true;
	}
	
}
