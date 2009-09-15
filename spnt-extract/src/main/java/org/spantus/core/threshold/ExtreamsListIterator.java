package org.spantus.core.threshold;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.spantus.core.FrameValues;
import org.spantus.core.threshold.ExtreamEntry.SignalStates;
import org.spantus.logger.Logger;

public class ExtreamsListIterator implements ListIterator<ExtreamEntry> {

	LinkedList<ExtreamEntry> list;

	Logger log = Logger.getLogger(ExtreamsListIterator.class);
	

	private ExtreamEntry lastReturned = null;
	private ExtreamEntry first = null;
	@SuppressWarnings("unused")
	private ExtreamEntry last = null;
	
	private int nextIndex;
	FrameValues allValues;

	
	public ExtreamsListIterator(LinkedList<ExtreamEntry> list, FrameValues allValues) {
		this.list = list;
		this.allValues = allValues;
		nextIndex = 0;
		ExtreamEntry previous = null;
		for (ExtreamEntry element : list) {
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
		last = previous.getNext();
	}


	// custom impl
	public boolean isPreviousMinExtream(){
		if(lastReturned.getPrevious() == null){
			return true;
		}
		return SignalStates.minExtream.equals(lastReturned.getPrevious().getSignalStates());
	}
	public boolean isCurrentMaxExtream(){
		return SignalStates.maxExtream.equals(lastReturned.getSignalStates());
	}
	public boolean isNextMinExtream(){
		if(lastReturned.getNext() == null){
			return true;
		}
		return SignalStates.minExtream.equals(lastReturned.getNext().getSignalStates());
	}
	public ExtreamEntry getNextEntry(){
		if(lastReturned.getNext() == null){
			return new ExtreamEntry(list.size(), lastReturned.getValue(),SignalStates.minExtream);
		}
		return lastReturned.getNext();
	}
	public ExtreamEntry getPreviousEntry(){
		if(lastReturned.getPrevious() == null){
			return new ExtreamEntry(0, lastReturned.getValue(),SignalStates.minExtream);
		}
		return lastReturned.getPrevious();
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
			int length = getPeakLength(); 
			String out = MessageFormat.format("{0,number,#.###};{1,number,#.###};{2,number,#.###};\t area: {3,number,#};\t\t length: {4}", 
					allValues.toTime(getPreviousEntry().getIndex()), 
					allValues.toTime(lastReturned.getIndex()), 
					allValues.toTime(getNextEntry().getIndex()),
//					getPreviousEntry().getIndex(), 
//					lastReturned.getIndex(), 
//					getNextEntry().getIndex(),
//					getPreviousEntry().getValue(), 
//					lastReturned.getValue(), 
//					getNextEntry().getValue(),
					getArea(), allValues.toTime(length));
			log.info(out);
			
//		}
	}
	
	//interface impl

	public boolean hasNext() {
		if(lastReturned == null){
			return first.getNext() != null;
		}
		return  lastReturned.getNext() != null; 
	}

	public ExtreamEntry next() {
		checkForComodification();
		if (nextIndex == list.size())
			throw new NoSuchElementException();
		if(lastReturned == null){
			lastReturned = first;
		}else{
			lastReturned = lastReturned.getNext();
		}
		nextIndex++;
		return lastReturned;
	}
	public ExtreamEntry getNext(SignalStates signalState) {
		ExtreamEntry current = lastReturned;
		while(current.getNext()!=null){
			current = current.getNext();
			if(signalState.equals(current.getSignalStates())){
				return current;
			}
		}
		return null;
	}
	public ExtreamEntry getPrevious(SignalStates signalState) {
		ExtreamEntry current = lastReturned;
		while(current.getPrevious()!=null){
			current = current.getPrevious();
			if(signalState.equals(current.getSignalStates())){
				return current;
			}
		}
		return null;
		
	}

	public boolean hasPrevious() {
		return lastReturned.getPrevious() != null;
	}

	public ExtreamEntry previous() {
		if (nextIndex == 0)
			throw new NoSuchElementException();

		lastReturned = lastReturned.getPrevious();
		nextIndex--;
		checkForComodification();
		return lastReturned;
	}

	public int nextIndex() {
		return nextIndex;
	}

	public int previousIndex() {
		return nextIndex - 1;
	}

	public void remove() {
		log.info("remove current");
		remove(lastReturned);
		ExtreamEntry previous = lastReturned.getPrevious();
		if(previous != null){
			lastReturned = previous;
			nextIndex--;
		}else {
			lastReturned = lastReturned.getNext();
		}
		
	}
	public void removeNext() {
		ExtreamEntry next = lastReturned.getNext();
		if(next != null){
			ExtreamEntry nextNext = lastReturned.getNext().getNext();
			if(nextNext != null){
				lastReturned.setNext(nextNext);
				nextNext.setPrevious(lastReturned);
			}else{
				lastReturned.setNext(null);
			}
			list.remove(next);
		}
	}

	public void removePrevious() {
		ExtreamEntry previous = lastReturned.getPrevious();
		if(previous != null){
			ExtreamEntry previousPrevious = lastReturned.getPrevious().getPrevious();
			if(previousPrevious != null){
				lastReturned.setPrevious(previousPrevious);
				previousPrevious.setNext(lastReturned);
			}else{
				lastReturned.setNext(null);
			}
			list.remove(previous);
		}
	}
	public void remove(ExtreamEntry extreamEntry) {
		log.info("remove: " + extreamEntry);

		ExtreamEntry next = extreamEntry.getNext();
		ExtreamEntry previous = extreamEntry.getPrevious();
		if(next != null){
			next.setPrevious(previous);
		}
		if(previous!=null){
			previous.setNext(next);
		}
		list.remove(extreamEntry);
	}

	
	public void set(ExtreamEntry o) {
	}

	public void add(ExtreamEntry o) {
	}

	final void checkForComodification() {
		// if (modCount != expectedModCount)
		// throw new ConcurrentModificationException();
		// }
	}
}
