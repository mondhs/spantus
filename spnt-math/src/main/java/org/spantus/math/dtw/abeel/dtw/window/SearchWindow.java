/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2010, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 */
package org.spantus.math.dtw.abeel.dtw.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.spantus.math.dtw.abeel.dtw.constraint.DefaultLocalConstaints;
import org.spantus.math.dtw.abeel.dtw.constraint.LocalConstaints;
import org.spantus.math.dtw.abeel.matrix.ColMajorCell;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public abstract class SearchWindow implements Iterable<ColMajorCell> {

	private LocalConstaints localConstaints;
	private final int minValues[];
	private final int maxValues[];
	private final int maxJ;
	private int size;

	int modCount;

	public SearchWindow(int tsIsize, int tsJsize) {
		minValues = new int[tsIsize];
		maxValues = new int[tsIsize];
		Arrays.fill(minValues, -1);
		maxJ = tsJsize - 1;
		size = 0;
		modCount = 0;
		localConstaints = new DefaultLocalConstaints();
	}

	public final boolean isInWindow(int i, int j) {
		return i >= minI() && i <= maxI() && minValues[i] <= j
				&& maxValues[i] >= j;
	}

	public final int minI() {
		return 0;
	}

	public final int maxI() {
		return minValues.length - 1;
	}

	public final int minJ() {
		return 0;
	}

	public final int maxJ() {
		return maxJ;
	}

	public final int minJforI(int i) {
		return minValues[i];
	}

	public final int maxJforI(int i) {
		return maxValues[i];
	}

	public final int size() {
		return size;
	}

	public final Iterator<ColMajorCell> iterator() {
		return new SearchWindowIterator(this);
	}

	public final String toString() {
		StringBuilder outStr = new StringBuilder();
		for (int i = minI(); i <= maxI(); i++) {
			outStr.append("i=" + i + ", j=" + minValues[i] + "..."
					+ maxValues[i]);
			if (i != maxI())
				outStr.append("\n");
		}

		return outStr.toString();
	}

	protected int getModCount() {
		return modCount;
	}

	protected final void expandWindow(int radius) {
		if (radius > 0) {
			expandSearchWindow(1);
			expandSearchWindow(radius - 1);
		}
	}

	private final void expandSearchWindow(int radius) {
		if (radius > 0) {
			ArrayList<ColMajorCell> windowCells = new ArrayList<ColMajorCell>(
					size());
			for (Iterator<ColMajorCell> cellIter = iterator(); cellIter
					.hasNext(); windowCells.add(cellIter.next()))
				;
			for (int cell = 0; cell < windowCells.size(); cell++) {
				ColMajorCell currentCell = (ColMajorCell) windowCells.get(cell);
				int targetCol;
				int targetRow;
				if (currentCell.getCol() != minI()
						&& currentCell.getRow() != maxJ()) {
					targetCol = currentCell.getCol() - radius;
					targetRow = currentCell.getRow() + radius;
					if (targetCol >= minI() && targetRow <= maxJ()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = Math.max(minI() - targetCol,
								targetRow - maxJ());
						markVisited(targetCol + cellsPastEdge, targetRow
								- cellsPastEdge);
					}
				}
				if (currentCell.getRow() != maxJ()) {
					targetCol = currentCell.getCol();
					targetRow = currentCell.getRow() + radius;
					if (targetRow <= maxJ()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = targetRow - maxJ();
						markVisited(targetCol, targetRow - cellsPastEdge);
					}
				}
				if (currentCell.getCol() != maxI()
						&& currentCell.getRow() != maxJ()) {
					targetCol = currentCell.getCol() + radius;
					targetRow = currentCell.getRow() + radius;
					if (targetCol <= maxI() && targetRow <= maxJ()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = Math.max(targetCol - maxI(),
								targetRow - maxJ());
						markVisited(targetCol - cellsPastEdge, targetRow
								- cellsPastEdge);
					}
				}
				if (currentCell.getCol() != minI()) {
					targetCol = currentCell.getCol() - radius;
					targetRow = currentCell.getRow();
					if (targetCol >= minI()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = minI() - targetCol;
						markVisited(targetCol + cellsPastEdge, targetRow);
					}
				}
				if (currentCell.getCol() != maxI()) {
					targetCol = currentCell.getCol() + radius;
					targetRow = currentCell.getRow();
					if (targetCol <= maxI()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = targetCol - maxI();
						markVisited(targetCol - cellsPastEdge, targetRow);
					}
				}
				if (currentCell.getCol() != minI()
						&& currentCell.getRow() != minJ()) {
					targetCol = currentCell.getCol() - radius;
					targetRow = currentCell.getRow() - radius;
					if (targetCol >= minI() && targetRow >= minJ()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = Math.max(minI() - targetCol, minJ()
								- targetRow);
						markVisited(targetCol + cellsPastEdge, targetRow
								+ cellsPastEdge);
					}
				}
				if (currentCell.getRow() != minJ()) {
					targetCol = currentCell.getCol();
					targetRow = currentCell.getRow() - radius;
					if (targetRow >= minJ()) {
						markVisited(targetCol, targetRow);
					} else {
						int cellsPastEdge = minJ() - targetRow;
						markVisited(targetCol, targetRow + cellsPastEdge);
					}
				}
				if (currentCell.getCol() == maxI()
						|| currentCell.getRow() == minJ())
					continue;
				targetCol = currentCell.getCol() + radius;
				targetRow = currentCell.getRow() - radius;
				if (targetCol <= maxI() && targetRow >= minJ()) {
					markVisited(targetCol, targetRow);
				} else {
					int cellsPastEdge = Math.max(targetCol - maxI(), minJ()
							- targetRow);
					markVisited(targetCol - cellsPastEdge, targetRow
							+ cellsPastEdge);
				}
			}

		}
	}

	protected final void markVisited(int col, int row) {
		if (minValues[col] == -1) {
			minValues[col] = row;
			maxValues[col] = row;
			size++;
			modCount++;
		} else if (minValues[col] > row) {
			size += minValues[col] - row;
			minValues[col] = row;
			modCount++;
		} else if (maxValues[col] < row) {
			size += row - maxValues[col];
			maxValues[col] = row;
			modCount++;
		}
	}

	public LocalConstaints getLocalConstaints() {
		return localConstaints;
	}

	public void setLocalConstaints(LocalConstaints localConstaints) {
		this.localConstaints = localConstaints;
	}

}