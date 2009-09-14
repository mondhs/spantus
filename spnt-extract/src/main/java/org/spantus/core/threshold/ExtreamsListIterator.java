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
	
	private transient IteratationEntry header = new IteratationEntry(null,
			null, null);

	private IteratationEntry lastReturned = null;
	private IteratationEntry first = null;
	private IteratationEntry last = null;
	
	private int nextIndex;
	FrameValues allValues;

	
	public ExtreamsListIterator(LinkedList<ExtreamEntry> list, FrameValues allValues) {
		this.list = list;
		this.allValues = allValues;
		nextIndex = 0;
		IteratationEntry previous = null;
		IteratationEntry entry = null;
		for (ExtreamEntry element : list) {
			entry = new IteratationEntry(element, null, previous);
			if(first == null){
				first = entry;
			}
			if(previous != null){
				previous.next = entry;
				entry.previous = previous;
			}
			previous = entry;
		}
		last = entry;
	}


	// custom impl
	public boolean isPreviousMinExtream(){
		if(lastReturned.previous == null){
			return true;
		}
		return SignalStates.minExtream.equals(lastReturned.previous.element.getSignalStates());
	}
	public boolean isCurrentMaxExtream(){
		if(lastReturned.previous == null){
			return true;
		}
		return SignalStates.minExtream.equals(lastReturned.previous.element.getSignalStates());
	}
	public boolean isNextMinExtream(){
		if(lastReturned.next == null){
			return true;
		}
		return SignalStates.minExtream.equals(lastReturned.next.element.getSignalStates());
	}
	public ExtreamEntry getNextEntry(){
		if(lastReturned.next == null){
			return new ExtreamEntry(list.size(), lastReturned.element.getValue(),SignalStates.minExtream);
		}
		return lastReturned.next.element;
	}
	public ExtreamEntry getPreviousEntry(){
		if(lastReturned.previous == null){
			return new ExtreamEntry(0, lastReturned.element.getValue(),SignalStates.minExtream);
		}
		return lastReturned.previous.element;
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
			String out = MessageFormat.format("{0,number,#.###}/{1,number,#.###}\\{2,number,#.###};\t area: {3,number,#};\t\t length: {4}", 
					allValues.toTime(getPreviousEntry().getIndex()), 
					allValues.toTime(lastReturned.element.getIndex()), 
					allValues.toTime(getNextEntry().getIndex()),
//					getPreviousEntry().getIndex(), 
//					lastReturned.element.getIndex(), 
//					getNextEntry().getIndex(),
					getArea(), allValues.toTime(length));
			log.info(out);
			
//		}
	}
	
	//interface impl

	public boolean hasNext() {
		return nextIndex != list.size();
	}

	public ExtreamEntry next() {
		checkForComodification();
		if (nextIndex == list.size())
			throw new NoSuchElementException();
		if(lastReturned == null){
			lastReturned = first;
		}else{
			lastReturned = lastReturned.next;
		}
		nextIndex++;
		return lastReturned.element;
	}

	public boolean hasPrevious() {
		return nextIndex != 0;
	}

	public ExtreamEntry previous() {
		if (nextIndex == 0)
			throw new NoSuchElementException();

		lastReturned = lastReturned.previous;
		nextIndex--;
		checkForComodification();
		return lastReturned.element;
	}

	public int nextIndex() {
		return nextIndex;
	}

	public int previousIndex() {
		return nextIndex - 1;
	}

	public void remove() {
		
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

	class IteratationEntry {
		ExtreamEntry element;
		IteratationEntry next;
		IteratationEntry previous;

		public IteratationEntry(ExtreamEntry element, IteratationEntry next,
				IteratationEntry previous) {
			this.element = element;
			this.next = next;
			this.previous = previous;
		}
	}
}
