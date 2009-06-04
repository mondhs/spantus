package org.spantus.work.ui.container;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;

import org.spantus.ui.ModelEntry;

public class SortedListModel extends AbstractListModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Collection<ModelEntry> model;

	public SortedListModel(Collection<ModelEntry> collectionModelEntries) {
		model = collectionModelEntries;
	}
	
	public SortedListModel() {
		model = new LinkedList<ModelEntry>();
	}

	public int getSize() {
		return model.size();
	}
	
	@SuppressWarnings("unchecked")
	public void sort(Comparator<ModelEntry> comparator){
		if(model instanceof List<?>){
			Collections.sort((List)model,comparator);
		}
	}

	public Object getElementAt(int index) {
		return model.toArray()[index];
	}

	public void add(ModelEntry element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	@SuppressWarnings("unchecked")
	public void addAll(Object elements[]) {
		Collection c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

//	public Object firstElement() {
//		return model.first();
//	}

	public Iterator<ModelEntry> iterator() {
		return model.iterator();
	}
	public Iterable<ModelEntry> iteratable() {
		return model;
	}

//	public Object lastElement() {
//		return model.last();
//	}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
}
