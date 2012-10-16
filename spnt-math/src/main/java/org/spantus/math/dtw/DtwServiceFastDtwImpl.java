package org.spantus.math.dtw;

import com.dtw.FastDTW;
import com.dtw.FullWindow;
import com.dtw.LinearWindow;
import com.dtw.ParallelogramWindow;
import com.timeseries.TimeSeries;
import com.timeseries.TimeSeriesPoint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spantus.math.VectorUtils;
import com.dtw.SearchWindow;
import com.dtw.TimeWarpInfo;
import com.dtw.WarpPath;
import com.dtw.WarpPathWindow;
import com.matrix.ColMajorCell;
import com.timeseries.PAA;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1 Created Apr 30, 2009
 * 
 */
public class DtwServiceFastDtwImpl implements DtwService {
    public static final String DISTANCE_FUNCTION_NAME = 
            "ManhattanDistance";
//            "EuclideanDistance";

	private JavaMLSearchWindow searchWindow;
	private Float searchRadius;
	private JavaMLLocalConstraint localConstaints;




	public static final float DEFAULT_RADIUS = 10;

        
        public static TimeSeries toTimeSeries(List<Double> values) {
		TimeSeries ts = new TimeSeries(values);
		return ts;
	}

	public static TimeSeries toTimeSeries(List<List<Double>> matrix,
			int numOfDimensions) {
		TimeSeries ts = new TimeSeries(numOfDimensions);
		double i = 1;
		for (List<Double> values : matrix) {
			Collection<Double> doubles = VectorUtils.toDoubleList(values);
			TimeSeriesPoint tsp = new TimeSeriesPoint(doubles);
			ts.addLast(i++, tsp);
		}
		
		return ts;
	}
        
	public Double calculateDistance(List<Double> targetVector,
			List<Double> sampleVector) {

		TimeSeries tsTarget = toTimeSeries(targetVector);
		TimeSeries tsSample = toTimeSeries(sampleVector);

		Double info = null;
		SearchWindow searchWindowInstance = null;

		if (getSearchWindow() == null) {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0F,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
		} else {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, getLocalConstaints());
		}
                DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName(DISTANCE_FUNCTION_NAME); 
		info = FastDTW.getWarpDistBetween(tsTarget, tsSample,
				distFn);

		return info;
	}

	protected SearchWindow createSearchWindow(TimeSeries tsTarget,
			TimeSeries tsSample, JavaMLLocalConstraint localConstraint) {
		return createSearchWindow(tsTarget, tsSample, getSearchRadius(),
				getSearchWindow(), localConstraint);
	}

	public Double calculateDistanceVector(List<List<Double>> targetMatrix,
            List<List<Double>> sampleMatrix) {
        TimeSeries tsSample = toTimeSeries(sampleMatrix, sampleMatrix.get(0).size());
        TimeSeries tsTarget = toTimeSeries(targetMatrix, targetMatrix.get(0).size());

        Double info = null;
        SearchWindow searchWindowInstance = null;
        if(getSearchWindow()==null){
        	searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0F,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
        }else{
            searchWindowInstance = createSearchWindow(tsTarget, tsSample, getLocalConstaints());
          
        }
        DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName(DISTANCE_FUNCTION_NAME); 
        info = FastDTW.getWarpDistBetween(tsTarget, tsSample, distFn);
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
		TimeSeries tsTarget = toTimeSeries(targetVector);
		TimeSeries tsSample = toTimeSeries(sampleVector);

		TimeWarpInfo info = null;
		SearchWindow searchWindowInstance = null;
		if (getSearchWindow() == null) {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0F,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
		} else {
			 searchWindowInstance = createSearchWindow(tsTarget,
					tsSample, getLocalConstaints());
			
		}
                DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName(DISTANCE_FUNCTION_NAME);     
		info = FastDTW.getWarpInfoBetween(tsTarget, tsSample, 3,
				distFn);
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
		TimeSeries tsSample = toTimeSeries(sampleMatrix,
				sampleMatrix.get(0).size());
		TimeSeries tsTarget = toTimeSeries(targetMatrix,
				targetMatrix.get(0).size());

		TimeWarpInfo info = null;
		SearchWindow searchWindowInstance = null;
		if (getSearchWindow() == null) {
			searchWindowInstance = createSearchWindow(tsTarget, tsSample, 0F,
					JavaMLSearchWindow.FullWindow, getLocalConstaints());
		} else {
			 searchWindowInstance = createSearchWindow(tsTarget,
					tsSample, getLocalConstaints());
			
		}
                DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName(DISTANCE_FUNCTION_NAME); 
		info = FastDTW.getWarpInfoBetween(tsTarget, tsSample, 3,
				distFn);
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
//		result.setCostMatrix(info.getCostMatrix());
//		result.setStatisticalSummary(info.getStatisticalSummary());

		if(info.getPath() != null){
			for (int i = 1; i < info.getPath().size() ; i++) {
				ColMajorCell cell = info.getPath().get(i);
				Point point = new Point(cell.getCol(), cell.getRow());
				result.getPath().add(point);
			}
		}
		return result;
	}

	private static Integer transformWindowRadiusPercent(int targetSize, int sampleSize, Float radiusPercent){
		if(radiusPercent == null){
			return null;
		}
		Double avg = Math.sqrt((double)targetSize*sampleSize/2);
		avg = (avg*radiusPercent)/100;
		avg = Math.max(3, avg);
		return avg.intValue();
	}
	
	public static SearchWindow createSearchWindow(TimeSeries tsTarget,
			TimeSeries tsSample, Float radiusPercent, JavaMLSearchWindow searchWindow,JavaMLLocalConstraint localConstaints) {
		SearchWindow searchWindowObj = null;
		
		Integer radius = transformWindowRadiusPercent(tsTarget.size(), tsSample.size(), radiusPercent);
		
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

                        DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName(DISTANCE_FUNCTION_NAME); 
                        
                        //createSearchWindow(shrunkTarget, shrunkSample, null, JavaMLSearchWindow.FullWindow, localConstaints)
                        
			WarpPath coarsePath = FastDTW.getWarpPathBetween(shrunkTarget,
					shrunkSample,distFn);
			WarpPath expandedPath = expandPath(coarsePath, shrunkTarget,
					shrunkSample);
			searchWindowObj = new WarpPathWindow(expandedPath, radius);
		}
		
		
//		searchWindowObj.setLocalConstaints(createLocalConstraint(localConstaints));
		return searchWindowObj;
	}

//	private static LocalConstaints createLocalConstraint(
//			JavaMLLocalConstraint localConstaints) {
//		switch (localConstaints) {
//		case Default:
//			return new DefaultLocalConstaints();
//		case Angle:
//			return new AngleLocalConstaints();
//		case Wide:
//			return new WideLocalConstaints();
//		default:
//			throw new IllegalArgumentException("Not Implemented");
//		}
//	}

	public JavaMLSearchWindow getSearchWindow() {
		return searchWindow;
	}

	public void setSearchWindow(JavaMLSearchWindow searchWindow) {
		this.searchWindow = searchWindow;
	}

	private Float getSearchRadius() {
		return this.searchRadius;
	}

	public void setSearchRadius(Float searchRadius) {
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
