package org.spantus.core.threshold;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtremeEntry.SignalStates;
import org.spantus.logger.Logger;

public class ExtremeListIterator implements ListIterator<ExtremeEntry> {

	LinkedList<ExtremeEntry> list;

	Logger log = Logger.getLogger(ExtremeListIterator.class);
	

	private ExtremeEntry lastReturned = null;
	private ExtremeEntry first = null;
	@SuppressWarnings("unused")
	private ExtremeEntry last = null;
	
	private int nextIndex;
	FrameValues allValues;

	
	public ExtremeListIterator(LinkedList<ExtremeEntry> list, FrameValues allValues
			) {
		this.list = list;
		this.allValues = allValues;
		nextIndex = 0;
		ExtremeEntry previous = null;
		for (ExtremeEntry element : list) {
			element.link(previous, null);
			if(first == null){
				first = element;
			}
			if(previous != null){
				previous.setNext(element);
				element.setPrevious(previous);
			}
			previous = element;
		}
		if(previous != null){
			last = previous.getNext();
		}
	}


	// custom impl
	public boolean isPreviousMinExtream(){
		if(getLastReturned() == null || getLastReturned().getPrevious() == null){
			return true;
		}
		return SignalStates.minExtream.equals(getLastReturned().getPrevious().getSignalState());
	}
	public boolean isCurrentMaxExtream(){
		if(lastReturned == null){
			return true;
		}
		return SignalStates.maxExtream.equals(getLastReturned().getSignalState());
	}
	public boolean isNextMinExtream(){
		if(getLastReturned() == null || getLastReturned().getNext() == null){
			return true;
		}
		return SignalStates.minExtream.equals(getLastReturned().getNext().getSignalState());
	}
	public ExtremeEntry getNextEntry(){
		if(getLastReturned() == null || getLastReturned().getNext() == null){
			return new ExtremeEntry(list.size(), getLastReturned().getValue(),SignalStates.minExtream);
		}
		return getLastReturned().getNext();
	}
	public ExtremeEntry getPreviousEntry(){
		if(getLastReturned().getPrevious() == null){
			return new ExtremeEntry(0, getLastReturned().getValue(),SignalStates.minExtream);
		}
		return getLastReturned().getPrevious();
	}
	public Integer getPeakLength(){
		int length = getNextEntry().getIndex() - getPreviousEntry().getIndex(); 
		return length;
	}
	public Double getArea(){
		Double area = 0D;
		int index = 0;
		int length = getPeakLength(); 
		for (Iterator<Float> iterator = allValues.listIterator(getPreviousEntry().getIndex()); iterator.hasNext();) {
			area += iterator.next();
			if(index++>length){
				break;
			}
		}
		return area;
	}
	
	public void logCurrent(){
//		if(log.isDebugMode()){
//			int length = getPeakLength(); 
//			String out = MessageFormat.format("{0,number,#.###};{1,number,#.###};{2,number,#.###};\t area: {3,number,#};\t\t length: {4}", 
//					allValues.toTime(getPreviousEntry().getIndex()), 
//					allValues.toTime(lastReturned.getIndex()), 
//					allValues.toTime(getNextEntry().getIndex()),
//					getPreviousEntry().getIndex(), 
//					lastReturned.getIndex(), 
//					getNextEntry().getIndex(),
//					getPreviousEntry().getValue(), 
//					lastReturned.getValue(), 
//					getNextEntry().getValue(),
//					getArea(), allValues.toTime(length));
//			log.info(out);
			
//		}
	}
	
	//interface impl

	public boolean hasNext() {
		if(getLastReturned() == null){
			return first.getNext() != null;
		}
		return  getLastReturned().getNext() != null; 
	}

	public ExtremeEntry next() {
		checkForComodification();
		if (nextIndex == list.size())
			throw new NoSuchElementException();
		if(getLastReturned() == null){
			setLastReturned(first);
		}else{
			setLastReturned(getLastReturned().getNext());
		}
		nextIndex++;
		return getLastReturned();
	}
	public ExtremeEntry getNext(SignalStates signalState) {
		ExtremeEntry current = getLastReturned();
		while(current.getNext()!=null){
			current = current.getNext();
			if(signalState.equals(current.getSignalState())){
				return current;
			}
		}
		return null;
	}
	public ExtremeEntry getPrevious(SignalStates signalState) {
		ExtremeEntry current = getLastReturned();
		while(current.getPrevious()!=null){
			current = current.getPrevious();
			if(signalState.equals(current.getSignalState())){
				return current;
			}
		}
		return null;
		
	}

	public boolean hasPrevious() {
		return getLastReturned().getPrevious() != null;
	}

	public ExtremeEntry previous() {
		if (nextIndex == 0)
			throw new NoSuchElementException();

		setLastReturned(getLastReturned().getPrevious());
		nextIndex--;
		checkForComodification();
		return getLastReturned();
	}

	public int nextIndex() {
		return nextIndex;
	}

	public int previousIndex() {
		return nextIndex - 1;
	}

	public void remove() {
		log.info("remove current");
		remove(getLastReturned());
		ExtremeEntry previous = lastReturned.getPrevious();
		if(previous != null){
			lastReturned = previous;
			nextIndex--;
		}else {
			lastReturned = lastReturned.getNext();
		}
		
	}
	public void removeNext() {
		ExtremeEntry next = lastReturned.getNext();
		if(next != null){
			ExtremeEntry nextNext = lastReturned.getNext().getNext();
			if(nextNext != null){
				getLastReturned().setNext(nextNext);
				nextNext.setPrevious(getLastReturned());
			}else{
				getLastReturned().setNext(null);
			}
			list.remove(next);
		}
	}

	public void removePrevious() {
		ExtremeEntry previous = getLastReturned().getPrevious();
		if(previous != null){
			ExtremeEntry previousPrevious = getLastReturned().getPrevious().getPrevious();
			if(previousPrevious != null){
				getLastReturned().setPrevious(previousPrevious);
				previousPrevious.setNext(getLastReturned());
			}else{
				getLastReturned().setNext(null);
			}
			list.remove(previous);
		}
	}
	public void remove(ExtremeEntry extreamEntry) {
		log.info("remove: " + extreamEntry);

		ExtremeEntry next = extreamEntry.getNext();
		ExtremeEntry previous = extreamEntry.getPrevious();
		if(next != null){
			next.setPrevious(previous);
		}
		if(previous!=null){
			previous.setNext(next);
		}
		list.remove(extreamEntry);
	}

	
	public void set(ExtremeEntry o) {
	}

	public void add(ExtremeEntry o) {
	}

	final void checkForComodification() {
		// if (modCount != expectedModCount)
		// throw new ConcurrentModificationException();
		// }
	}


	public ExtremeEntry getLastReturned() {
		return lastReturned;
	}


	public void setLastReturned(ExtremeEntry lastReturned) {
		this.lastReturned = lastReturned;
	}
}
