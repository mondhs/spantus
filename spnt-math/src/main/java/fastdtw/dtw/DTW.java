// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DTW.java

package fastdtw.dtw;

import java.util.Iterator;

import fastdtw.matrix.ColMajorCell;
import fastdtw.timeseries.ITimeSeries;
import fastdtw.timeseries.TimeSeries;

// Referenced classes of package dtw:
//            WarpPath, TimeWarpInfo, PartialWindowMatrix, WindowMatrix, 
//            SearchWindow

public class DTW {

	public DTW() {
	}

	public double calcWarpCost(WarpPath path, TimeSeries tsI, TimeSeries tsJ) {
		double totalCost = 0.0D;
		for (int p = 0; p < path.size(); p++) {
			ColMajorCell currWarp = path.get(p);
			totalCost += euclideanDist(tsI.getMeasurementVector(currWarp
					.getCol()), tsJ.getMeasurementVector(currWarp.getRow()));
		}

		return totalCost;
	}

	public double getWarpDistBetween(ITimeSeries tsI, ITimeSeries tsJ) {
		if (tsI.size() < tsJ.size())
			return getWarpDistBetween(tsJ, tsI);
		double lastCol[] = new double[tsJ.size()];
		double currCol[] = new double[tsJ.size()];
		int maxI = tsI.size() - 1;
		int maxJ = tsJ.size() - 1;
		currCol[0] = euclideanDist(tsI.getMeasurementVector(0), tsJ
				.getMeasurementVector(0));
		for (int j = 1; j <= maxJ; j++)
			currCol[j] = currCol[j - 1]
					+ euclideanDist(tsI.getMeasurementVector(0), tsJ
							.getMeasurementVector(j));

		for (int i = 1; i <= maxI; i++) {
			double temp[] = lastCol;
			lastCol = currCol;
			currCol = temp;
			currCol[0] = lastCol[0]
					+ euclideanDist(tsI.getMeasurementVector(i), tsJ
							.getMeasurementVector(0));
			for (int j = 1; j <= maxJ; j++) {
				double minGlobalCost = Math.min(lastCol[j], Math.min(
						lastCol[j - 1], currCol[j - 1]));
				currCol[j] = minGlobalCost
						+ euclideanDist(tsI.getMeasurementVector(i), tsJ
								.getMeasurementVector(j));
			}

		}

		return currCol[maxJ];
	}

	public WarpPath getWarpPathBetween(ITimeSeries tsI, ITimeSeries tsJ) {
		return DynamicTimeWarp(tsI, tsJ).getPath();
	}

	public TimeWarpInfo getWarpInfoBetween(ITimeSeries tsI, ITimeSeries tsJ) {
		return DynamicTimeWarp(tsI, tsJ);
	}

	private TimeWarpInfo DynamicTimeWarp(ITimeSeries tsI, ITimeSeries tsJ) {
		double costMatrix[][] = new double[tsI.size()][tsJ.size()];
		int maxI = tsI.size() - 1;
		int maxJ = tsJ.size() - 1;
		costMatrix[0][0] = euclideanDist(tsI.getMeasurementVector(0), tsJ
				.getMeasurementVector(0));
		for (int j = 1; j <= maxJ; j++)
			costMatrix[0][j] = costMatrix[0][j - 1]
					+ euclideanDist(tsI.getMeasurementVector(0), tsJ
							.getMeasurementVector(j));

		for (int i = 1; i <= maxI; i++) {
			costMatrix[i][0] = costMatrix[i - 1][0]
					+ euclideanDist(tsI.getMeasurementVector(i), tsJ
							.getMeasurementVector(0));
			for (int j = 1; j <= maxJ; j++) {
				double minGlobalCost = Math.min(costMatrix[i - 1][j], Math.min(
						costMatrix[i - 1][j - 1], costMatrix[i][j - 1]));
				costMatrix[i][j] = minGlobalCost
						+ euclideanDist(tsI.getMeasurementVector(i), tsJ
								.getMeasurementVector(j));
			}

		}

		double minimumCost = costMatrix[maxI][maxJ];
		WarpPath minCostPath = new WarpPath((maxI + maxJ) - 1);
		int i = maxI;
		int j = maxJ;
		minCostPath.addFirst(i, j);
		for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
			double diagCost;
			if (i > 0 && j > 0)
				diagCost = costMatrix[i - 1][j - 1];
			else
				diagCost = (1.0D / 0.0D);
			double leftCost;
			if (i > 0)
				leftCost = costMatrix[i - 1][j];
			else
				leftCost = (1.0D / 0.0D);
			double downCost;
			if (j > 0)
				downCost = costMatrix[i][j - 1];
			else
				downCost = (1.0D / 0.0D);
			if (diagCost <= leftCost && diagCost <= downCost) {
				i--;
				j--;
				continue;
			}
			if (leftCost < diagCost && leftCost < downCost) {
				i--;
				continue;
			}
			if (downCost < diagCost && downCost < leftCost) {
				j--;
				continue;
			}
			if (i <= j)
				j--;
			else
				i--;
		}

		return new TimeWarpInfo(minimumCost, minCostPath);
	}

	public double getWarpDistBetween(ITimeSeries tsI, ITimeSeries tsJ,
			SearchWindow window) {
		PartialWindowMatrix costMatrix = new PartialWindowMatrix(window);
		int maxI = tsI.size() - 1;
		int maxJ = tsJ.size() - 1;
		for (Iterator<ColMajorCell> matrixIterator = window.iterator(); matrixIterator
				.hasNext();) {
			ColMajorCell currentCell = matrixIterator.next();
			int i = currentCell.getCol();
			int j = currentCell.getRow();
			if (i == 0 && j == 0)
				costMatrix.put(i, j, euclideanDist(tsI.getMeasurementVector(0),
						tsJ.getMeasurementVector(0)));
			else if (i == 0)
				costMatrix.put(i, j, euclideanDist(tsI.getMeasurementVector(0),
						tsJ.getMeasurementVector(j))
						+ costMatrix.get(i, j - 1));
			else if (j == 0) {
				costMatrix.put(i, j, euclideanDist(tsI.getMeasurementVector(i),
						tsJ.getMeasurementVector(0))
						+ costMatrix.get(i - 1, j));
			} else {
				double minGlobalCost = Math.min(costMatrix.get(i - 1, j), Math
						.min(costMatrix.get(i - 1, j - 1), costMatrix.get(i,
								j - 1)));
				costMatrix.put(i, j, minGlobalCost
						+ euclideanDist(tsI.getMeasurementVector(i), tsJ
								.getMeasurementVector(j)));
			}
		}

		return costMatrix.get(maxI, maxJ);
	}

	public WarpPath getWarpPathBetween(ITimeSeries tsI, ITimeSeries tsJ,
			SearchWindow window) {
		return constrainedTimeWarp(tsI, tsJ, window).getPath();
	}

	public TimeWarpInfo getWarpInfoBetween(ITimeSeries tsI, ITimeSeries tsJ,
			SearchWindow window) {
		return constrainedTimeWarp(tsI, tsJ, window);
	}

	private TimeWarpInfo constrainedTimeWarp(ITimeSeries tsI, ITimeSeries tsJ,
			SearchWindow window) {
		WindowMatrix costMatrix = new WindowMatrix(window);
		int maxI = tsI.size() - 1;
		int maxJ = tsJ.size() - 1;
		for (Iterator<ColMajorCell> matrixIterator = window.iterator(); matrixIterator
				.hasNext();) {
			ColMajorCell currentCell = matrixIterator.next();
			int i = currentCell.getCol();
			int j = currentCell.getRow();
			if (i == 0 && j == 0)
				costMatrix.put(i, j, euclideanDist(tsI.getMeasurementVector(0),
						tsJ.getMeasurementVector(0)));
			else if (i == 0)
				costMatrix.put(i, j, euclideanDist(tsI.getMeasurementVector(0),
						tsJ.getMeasurementVector(j))
						+ costMatrix.get(i, j - 1));
			else if (j == 0) {
				costMatrix.put(i, j, euclideanDist(tsI.getMeasurementVector(i),
						tsJ.getMeasurementVector(0))
						+ costMatrix.get(i - 1, j));
			} else {
				double minGlobalCost = Math.min(costMatrix.get(i - 1, j), Math
						.min(costMatrix.get(i - 1, j - 1), costMatrix.get(i,
								j - 1)));
				costMatrix.put(i, j, minGlobalCost
						+ euclideanDist(tsI.getMeasurementVector(i), tsJ
								.getMeasurementVector(j)));
			}
		}

		double minimumCost = costMatrix.get(maxI, maxJ);
		WarpPath minCostPath = new WarpPath((maxI + maxJ) - 1);
		int i = maxI;
		int j = maxJ;
		minCostPath.addFirst(i, j);
		for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
			double diagCost;
			if (i > 0 && j > 0)
				diagCost = costMatrix.get(i - 1, j - 1);
			else
				diagCost = (1.0D / 0.0D);
			double leftCost;
			if (i > 0)
				leftCost = costMatrix.get(i - 1, j);
			else
				leftCost = (1.0D / 0.0D);
			double downCost;
			if (j > 0)
				downCost = costMatrix.get(i, j - 1);
			else
				downCost = (1.0D / 0.0D);
			if (diagCost <= leftCost && diagCost <= downCost) {
				i--;
				j--;
				continue;
			}
			if (leftCost < diagCost && leftCost < downCost) {
				i--;
				continue;
			}
			if (downCost < diagCost && downCost < leftCost) {
				j--;
				continue;
			}
			if (i <= j)
				j--;
			else
				i--;
		}

		costMatrix.freeMem();
		return new TimeWarpInfo(minimumCost, minCostPath);
	}

	private double euclideanDist(double vector1[], double vector2[]) {
		if (vector1.length != vector2.length)
			throw new InternalError(
					"ERROR:  cannot calculate the distance between vectors of different sizes.");
		double sqSum = 0.0D;
		for (int x = 0; x < vector1.length; x++)
			sqSum += Math.pow(vector1[x] - vector2[x], 2D);

		return Math.pow(Math.sqrt(sqSum), 2D);
	}
}
