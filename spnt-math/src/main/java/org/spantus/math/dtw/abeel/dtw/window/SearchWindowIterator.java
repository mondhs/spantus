package org.spantus.math.dtw.abeel.dtw.window;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.spantus.math.dtw.abeel.matrix.ColMajorCell;

final class SearchWindowIterator implements Iterator<ColMajorCell> {

	private int currentI;

	private int currentJ;
	
	private final SearchWindow searchWindow;

	private boolean hasMoreElements;

	private final int expectedModCount;

	public SearchWindowIterator(SearchWindow searchWindow) {
		this.searchWindow = searchWindow;
		hasMoreElements = searchWindow.size() > 0;
		currentI = searchWindow.minI();
		currentJ = searchWindow.minJ();
		expectedModCount = searchWindow.modCount;
	}



	public boolean hasNext() {
		return hasMoreElements;
	}

	public ColMajorCell next() {
		if (this.searchWindow.modCount != expectedModCount)
			throw new ConcurrentModificationException();
		if (!hasMoreElements)
			throw new NoSuchElementException();
		ColMajorCell cell = new ColMajorCell(currentI, currentJ);
		if (++currentJ > searchWindow.maxJforI(currentI))
			if (++currentI <= searchWindow.maxI())
				currentJ = searchWindow.minJforI(currentI);
			else
				hasMoreElements = false;
		return cell;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}