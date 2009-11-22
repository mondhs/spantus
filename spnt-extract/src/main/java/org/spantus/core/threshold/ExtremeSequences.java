package org.spantus.core.threshold;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

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
	
	public Map<Integer, ExtremeEntry> toMap(){
		Map<Integer, ExtremeEntry> map = new TreeMap<Integer, ExtremeEntry>();
		for (Iterator<ExtremeEntry> iterator = this.iterator(); iterator.hasNext();) {
			ExtremeEntry entry = (ExtremeEntry) iterator.next();
			map.put(entry.getIndex(), entry);
		}
		return map;
	}
	
}
