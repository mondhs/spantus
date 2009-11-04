package org.spantus.core.threshold;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import org.spantus.core.FrameValues;

public class ExtremeSequences extends LinkedList<ExtremeEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4371289261556262428L;

	FrameValues allValues;
	
	public ExtremeSequences(Collection<ExtremeEntry> c, FrameValues allValues) {
		super(c);
		this.allValues = allValues;
	}
	
	@Override
	public ListIterator<ExtremeEntry> listIterator() {
		return super.listIterator();
	}

	public ExtremeListIterator extreamsListIterator() {
		return new ExtremeListIterator(this, allValues);
	}
	
}
