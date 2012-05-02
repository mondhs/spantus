package org.spantus.math.dtw;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.spantus.math.dtw.abeel.dtw.AbeelDTW;
import org.spantus.math.dtw.abeel.dtw.TimeWarpInfo;
import org.spantus.math.dtw.abeel.dtw.WarpPath;
import org.spantus.math.dtw.abeel.dtw.constraint.AngleLocalConstaints;
import org.spantus.math.dtw.abeel.dtw.constraint.DefaultLocalConstaints;
import org.spantus.math.dtw.abeel.dtw.constraint.LocalConstaints;
import org.spantus.math.dtw.abeel.dtw.constraint.WideLocalConstaints;
import org.spantus.math.dtw.abeel.dtw.window.FullWindow;
import org.spantus.math.dtw.abeel.dtw.window.LinearWindow;
import org.spantus.math.dtw.abeel.dtw.window.ParallelogramWindow;
import org.spantus.math.dtw.abeel.dtw.window.SearchWindow;
import org.spantus.math.dtw.abeel.dtw.window.WarpPathWindow;
import org.spantus.math.dtw.abeel.matrix.ColMajorCell;
import org.spantus.math.dtw.abeel.timeseries.PAA;
import org.spantus.math.dtw.abeel.timeseries.TimeSeries;
import org.spantus.math.services.javaml.JavaMLSupport;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Apr 30, 2009
 * 
 */
public class DtwServiceJavaMLImpl implements DtwService {

	private JavaMLSearchWindow searchWindow;
	private Integer searchRadius;
	private JavaMLLocalConstraint localConstaints;

	public enum JavaMLSearchWindow {
		FullWindow, LinearWindow, ParallelogramWindow, ExpandedResWindow
	}
	public enum JavaMLLocalConstraint {
		Default, Angle, Wide
	}

	public static final Integer DEFAULT_RADIUS = 3;

	public Double calculateDistance(List<Double> targetVector,
			List<Double> sampleVector) {

		TimeSeries tsTarget = JavaMLSupport.toTimeSeries(targetVector);
		TimeSeries tsSample = JavaMLSupport.toTimeSeries(sampleVector);

		Double info = null;
		SearchWindow searchWindowInstance = null;

		if (getSearchWindow() == null) {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
		} else {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, getLocalConstaints());
		}
		info = AbeelDTW.getWarpDistBetween(tsTarget, tsSample,
				searchWindowInstance);

		return info;
	}

	protected SearchWindow createSearchWindow(TimeSeries tsTarget,
			TimeSeries tsSample, JavaMLLocalConstraint localConstraint) {
		return createSearchWindow(tsTarget, tsSample, getSearchRadius(),
				getSearchWindow(), localConstraint);
	}

	public Double calculateDistanceVector(List<List<Double>> targetMatrix,
            List<List<Double>> sampleMatrix) {
        TimeSeries tsSample = JavaMLSupport.toTimeSeries(sampleMatrix, sampleMatrix.get(0).size());
        TimeSeries tsTarget = JavaMLSupport.toTimeSeries(targetMatrix, targetMatrix.get(0).size());

        Double info = null;
        SearchWindow searchWindowInstance = null;
        if(getSearchWindow()==null){
        	searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
        }else{
            searchWindowInstance = createSearchWindow(tsTarget, tsSample, getLocalConstaints());
          
        }
        info = AbeelDTW.getWarpDistBetween(tsTarget, tsSample, searchWindowInstance);
        return info;

    }

	/**
	 * 
	 * @param targetVector
	 * @param sampleVector
	 * @return results
	 */
	public DtwResult calculateInfo(List<Double> targetVector,
			List<Double> sampleVector) {
		TimeSeries tsTarget = JavaMLSupport.toTimeSeries(targetVector);
		TimeSeries tsSample = JavaMLSupport.toTimeSeries(sampleVector);

		TimeWarpInfo info = null;
		SearchWindow searchWindowInstance = null;
		if (getSearchWindow() == null) {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
		} else {
			 searchWindowInstance = createSearchWindow(tsTarget,
					tsSample, getLocalConstaints());
			
		}

		info = AbeelDTW.getWarpInfoBetween(tsTarget, tsSample,
				searchWindowInstance);
		DtwResult result = newDtwResult(info);
		return result;
	}

	/**
	 * 
	 * @param targetMatrix
	 * @param sampleMatrix
	 * @return
	 */
	public DtwResult calculateInfoVector(List<List<Double>> targetMatrix,
			List<List<Double>> sampleMatrix) {
		TimeSeries tsSample = JavaMLSupport.toTimeSeries(sampleMatrix,
				sampleMatrix.get(0).size());
		TimeSeries tsTarget = JavaMLSupport.toTimeSeries(targetMatrix,
				targetMatrix.get(0).size());

		TimeWarpInfo info = null;
		SearchWindow searchWindowInstance = null;
		if (getSearchWindow() == null) {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
		} else {
			 searchWindowInstance = createSearchWindow(tsTarget,
					tsSample, getLocalConstaints());
			
		}
		info = AbeelDTW.getWarpInfoBetween(tsTarget, tsSample,
				searchWindowInstance);
		DtwResult result = newDtwResult(info);
		return result;

	}
	/**
	 * 
	 * @param info
	 * @return
	 */
	private DtwResult newDtwResult(TimeWarpInfo info) {
		DtwResult result = new DtwResult();
		result.setResult(info.getDistance());
		result.setCostMatrix(info.getCostMatrix());
		result.setStatisticalSummary(info.getStatisticalSummary());

		for (int i = 1; i < info.getPath().size() ; i++) {
			ColMajorCell cell = info.getPath().get(i);
			Point point = new Point(cell.getCol(), cell.getRow());
			result.getPath().add(point);
		}
		return result;
	}

	public static SearchWindow createSearchWindow(TimeSeries tsTarget,
			TimeSeries tsSample, Integer radius, JavaMLSearchWindow searchWindow,JavaMLLocalConstraint localConstaints) {
		SearchWindow searchWindowObj = null;
		if (JavaMLSearchWindow.FullWindow.equals(searchWindow)) {
			searchWindowObj = new FullWindow(tsTarget, tsSample);
		} else if (JavaMLSearchWindow.LinearWindow.equals(searchWindow)) {
			if (radius == null) {
				searchWindowObj = new LinearWindow(tsTarget, tsSample);
			} else {
				searchWindowObj = new LinearWindow(tsTarget, tsSample, radius);
			}
		} else if (JavaMLSearchWindow.ParallelogramWindow.equals(searchWindow)) {
			if (radius == null) {
				throw new IllegalArgumentException(
						"please set setSearchRadius()");
			}
			searchWindowObj = new ParallelogramWindow(tsTarget, tsSample, radius);
		} else if (JavaMLSearchWindow.ExpandedResWindow.equals(searchWindow)) {
			if (radius == null) {
				throw new IllegalArgumentException(
						"please set setSearchRadius()");
			}
			PAA shrunkTarget = new PAA(tsTarget, (int) Math.round(Math
					.sqrt((double) tsTarget.size())));
			PAA shrunkSample = new PAA(tsSample, (int) Math.round(Math
					.sqrt((double) tsSample.size())));

			WarpPath coarsePath = AbeelDTW.getWarpPathBetween(shrunkTarget,
					shrunkSample,createSearchWindow(shrunkTarget, shrunkSample, 0,
							JavaMLSearchWindow.FullWindow, localConstaints));
			WarpPath expandedPath = expandPath(coarsePath, shrunkTarget,
					shrunkSample);
			searchWindowObj = new WarpPathWindow(expandedPath, radius);
		}
		
		
		searchWindowObj.setLocalConstaints(createLocalConstraint(localConstaints));
		return searchWindowObj;
	}

	private static LocalConstaints createLocalConstraint(
			JavaMLLocalConstraint localConstaints) {
		switch (localConstaints) {
		case Default:
			return new DefaultLocalConstaints();
		case Angle:
			return new AngleLocalConstaints();
		case Wide:
			return new WideLocalConstaints();
		default:
			throw new IllegalArgumentException("Not Implemented");
		}
	}

	public JavaMLSearchWindow getSearchWindow() {
		return searchWindow;
	}

	public void setSearchWindow(JavaMLSearchWindow searchWindow) {
		this.searchWindow = searchWindow;
	}

	private Integer getSearchRadius() {
		return this.searchRadius;
	}

	public void setSearchRadius(Integer searchRadius) {
		this.searchRadius = searchRadius;
	}

	private static WarpPath expandPath(WarpPath path, PAA tsI, PAA tsJ) {
		List<Integer> iPoints = new ArrayList<Integer>();
		List<Integer> jPoints = new ArrayList<Integer>();

		iPoints.add(new Integer(0));
		jPoints.add(new Integer(0));
		int startI = 0; // tsI.aggregatePtSize(0);
		int startJ = 0; // tsJ.aggregatePtSize(0);
		if (path.get(1).getCol() != 0)
			startI = tsI.aggregatePtSize(0) - 1;
		else
			startI = (tsI.aggregatePtSize(0) - 1) / 2;

		if (path.get(1).getRow() != 0)
			startJ = tsJ.aggregatePtSize(0) - 1;
		else
			startJ = (tsJ.aggregatePtSize(0) - 1) / 2;

		int lastI = 0;
		int lastJ = 0;

		for (int x = 1; x < path.size() - 1; x++) {
			int currentI = path.get(x).getCol();
			int currentJ = path.get(x).getRow();

			if ((lastI != currentI)) {
				if (lastI == 0)
					startI = tsI.aggregatePtSize(0) - 1;

				if (currentI == path.get(path.size() - 1).getCol())
					startI -= tsI.aggregatePtSize(currentI) / 2;
				{
					iPoints.add(new Integer(startI
							+ tsI.aggregatePtSize(currentI) / 2));
					startI += tsI.aggregatePtSize(currentI);
				}

				lastI = currentI;
			} else {
				iPoints.add(new Integer(startI));
			}

			if ((lastJ != currentJ)) {
				if (lastJ == 0)
					startJ = tsJ.aggregatePtSize(0) - 1;

				if (currentJ == path.get(path.size() - 1).getRow())
					startJ -= tsJ.aggregatePtSize(currentJ) / 2;
				{
					jPoints.add(new Integer(startJ
							+ tsJ.aggregatePtSize(currentJ) / 2));
					startJ += tsJ.aggregatePtSize(currentJ);
				}

				lastJ = currentJ;
			} else {
				jPoints.add(new Integer(startJ));
			}
		} // end for loop

		iPoints.add(new Integer(tsI.originalSize() - 1));
		jPoints.add(new Integer(tsJ.originalSize() - 1));

		// Interpolate between coarse warp path points.
		final WarpPath expandedPath = new WarpPath();

		startI = 0;
		startJ = 0;
		int endI;
		int endJ;

		for (int p = 1; p < iPoints.size(); p++) {
			endI = (Integer) iPoints.get(p);
			endJ = ((Integer) jPoints.get(p)).intValue();
			expandedPath.addLast(startI, startJ);

			if ((endI - startI) >= (endJ - startJ)) {
				for (int i = startI + 1; i < endI; i++)
					expandedPath.addLast(
							i,
							(int) Math.round(startJ + ((double) i - startI)
									/ ((double) endI - startI)
									* (endJ - startJ)));
			} else {
				for (int j = startJ + 1; j < endJ; j++)
					expandedPath.addLast(
							(int) Math.round(startI + ((double) j - startJ)
									/ ((double) endJ - startJ)
									* (endI - startI)), j);
			} // end if

			startI = endI;
			startJ = endJ;
		} // end for loop

		expandedPath.addLast(tsI.originalSize() - 1, tsJ.originalSize() - 1);
		return expandedPath;
	}

	public void setLocalConstaints(JavaMLLocalConstraint localConstaints) {
		this.localConstaints = localConstaints;
		
	}

	public JavaMLLocalConstraint getLocalConstaints() {
		if(localConstaints == null){
			localConstaints = JavaMLLocalConstraint.Default;
		}
		return localConstaints;
	}

}
