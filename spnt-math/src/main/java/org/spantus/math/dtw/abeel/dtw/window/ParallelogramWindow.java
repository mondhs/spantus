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

import org.spantus.math.dtw.abeel.timeseries.TimeSeries;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class ParallelogramWindow extends SearchWindow {

	public ParallelogramWindow(TimeSeries tsI, TimeSeries tsJ, int searchRadius) {
		super(tsI.size(), tsJ.size());
		Double maxID = Double.valueOf(maxI());
		Double maxJD = Double.valueOf(maxJ());
		Double radius = Double.valueOf(searchRadius);

		double upperCornerI = Math.max(maxID / 2D - radius * (maxID / maxJD),
				minI());
		double upperCornerJ = Math.min(maxJD / 2D + radius * (maxJD / maxID),
				maxJ());
		double lowerCornerI = Math.min(maxID / 2D + radius * (maxID / maxJD),
				maxI());
		double lowerCornerJ = Math.max(maxJD / 2D - radius * (maxJD / maxID),
				minJ());
		boolean isIlargest = tsI.size() >= tsJ.size();

		for (double i = 0; i < tsI.size(); i++) {

			int maxJ = findMaxJ(i, isIlargest, upperCornerI, upperCornerJ,
					maxID, maxJD);
			int minJ = findMinJ(i, lowerCornerI, lowerCornerJ);

			int index = Double.valueOf(i).intValue();
//			if ((index > 0D || minJ > 0D) && (index < maxID || minJ < maxJD)) {
//				System.out.print(MessageFormat.format("{0}: j>{1}\n", i, minJ));
				super.markVisited(index, minJ);
//			}
//			if ((index > 0D || minJ > 0D) && (maxJ < maxJD)) {
//				System.out.print(MessageFormat.format("{0}: j<{1}\n", i, maxJ));
				super.markVisited(index, maxJ);
//			}
		}

	}

	private int findMinJ(double i, double lowerCornerI, double lowerCornerJ) {
		int minJ = 0;
		if (i <= lowerCornerI) {
			double interpRatio = i / lowerCornerI;
			minJ = (int) Math.round(interpRatio * lowerCornerJ);
		} else {
			double interpRatio = ((double) i - lowerCornerI)
					/ ((double) maxI() - lowerCornerI);
			minJ = (int) Math.round(lowerCornerJ + interpRatio
					* ((double) maxJ() - lowerCornerJ));
		}
		return minJ;
	}

	private int findMaxJ(double i, boolean isIlargest, double upperCornerI,
			double upperCornerJ, Double maxID, Double maxJD) {
		int maxJ = 0;
		if (i < upperCornerI) {
			if (isIlargest) {
				double interpRatio = i / upperCornerI;
				maxJ = (int) Math.round(interpRatio * upperCornerJ);
			} else {
				double interpRatio = (i + 1) / upperCornerI;
				maxJ = (int) Math.round(interpRatio * upperCornerJ) - 1;
			}
		} else {
			if (isIlargest) {
				double interpRatio = (i - upperCornerI)
						/ (maxID - upperCornerI);
				maxJ = (int) Math.round(upperCornerJ + interpRatio
						* (maxJD - upperCornerJ));
			} else {
				double interpRatio = ((i + 1) - upperCornerI)
						/ (maxID - upperCornerI);
				maxJ = (int) Math.round(upperCornerJ + interpRatio
						* (maxJD - upperCornerJ)) - 1;
			}
		}
		return Math.min(maxJD.intValue(), maxJ);
	}
}