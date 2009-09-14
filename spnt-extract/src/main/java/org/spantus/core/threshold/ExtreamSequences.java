package org.spantus.core.threshold;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import org.spantus.core.FrameValues;

public class ExtreamSequences extends LinkedList<ExtreamEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4371289261556262428L;

	FrameValues allValues;
	
	public ExtreamSequences(Collection<ExtreamEntry> c, FrameValues allValues) {
		super(c);
		this.allValues = allValues;
	}
	
	@Override
	public ListIterator<ExtreamEntry> listIterator() {
		return super.listIterator();
	}

	public ExtreamsListIterator extreamsListIterator() {
		return new ExtreamsListIterator(this, allValues);
	}
	
}
