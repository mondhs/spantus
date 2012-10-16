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
package org.spantus.math.dtw.abeel.dtw;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.spantus.math.dtw.abeel.dtw.window.SearchWindow;
import org.spantus.math.dtw.abeel.matrix.ColMajorCell;
import org.spantus.math.dtw.abeel.timeseries.TimeSeries;

import scikit.util.Pair;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public final class AbeelDTW {

    private AbeelDTW() {
    }


    public static double getWarpDistBetween(TimeSeries tsI, TimeSeries tsJ, SearchWindow window) {
    	CostMatrix costMatrix = createCostMatrix(tsI, tsJ, window);
        int maxI = tsI.size() - 1;
        int maxJ = tsJ.size() - 1;
        return costMatrix.get(maxI, maxJ);
    }

    public static WarpPath getWarpPathBetween(TimeSeries tsI, TimeSeries tsJ, SearchWindow window) {
        return constrainedTimeWarp(tsI, tsJ, window).getPath();
    }

    public static TimeWarpInfo getWarpInfoBetween(TimeSeries tsI, TimeSeries tsJ, SearchWindow window) {
        return constrainedTimeWarp(tsI, tsJ, window);
    }

    private static TimeWarpInfo constrainedTimeWarp(TimeSeries tsI, TimeSeries tsJ, SearchWindow window) {
        CostMatrix costMatrix = createCostMatrix(tsI, tsJ, window);
        
        int maxI = tsI.size() - 1;
        int maxJ = tsJ.size() - 1;
        
        Double minimumCost = costMatrix.get(maxI, maxJ);
        if(minimumCost.isInfinite()){
        	return new TimeWarpInfo(minimumCost, null);
        }
        WarpPath minCostPath = new WarpPath((maxI + maxJ) - 1);
        int i = maxI;
        int j = maxJ;
        minCostPath.addFirst(i, j);
        for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
            Map<Pair<Integer, Integer>, Double> localConstraints = window.getLocalConstaints().createLocalConstraints(i, j);
            debug("[constrainedTimeWarp] [{1};{2}]localConstraints: {0}", localConstraints, i, j);
            //remove previous values. This happens due constraints.
//            if(localConstraints.containsKey(new Pair<Integer,Integer>(i,j)) ){
//            	 localConstraints.remove(new Pair<Integer,Integer>(i,j));
//            	 localConstraints.put(new Pair<Integer,Integer>(0,0),null);
//            }
            
            
//            for (Pair<Integer, Integer> pair : localConstraints.keySet()) {
//				if(j == pair.snd() ){
//					localConstraints.containsKey(new Pair<Integer,Integer>(i,j));
//				}
//			}
//            if(j==0){
//            	localConstraints.containsKey(new Pair<Integer,Integer>(i,j));
//            }
           
            DtwCalculationCtx values = extractConstraintValues(localConstraints, costMatrix);
            debug("[constrainedTimeWarp] min:{1}; extractConstraintValues: {0}", values.getLocalValues(), values.getMinPair());
            Pair<Integer,Integer> nextStep = values.getMinPair();
            
            if(nextStep == null){
            	break;
            }
            i = nextStep.fst();
            j = nextStep.snd();
        }
        minCostPath.addFirst(0, 0);
        
        
        TimeWarpInfo timeWarpInfo =  new TimeWarpInfo(minimumCost, minCostPath);
        
        //this can be too big and too slow?
        Pair<StatisticalSummary, RealMatrix> resultCostMatrix = transformCostMatrix(costMatrix, maxI, maxJ);
        timeWarpInfo.setStatisticalSummary(resultCostMatrix.fst());
        timeWarpInfo.setCostMatrix(resultCostMatrix.snd());

        costMatrix.freeMem();
        
       
        
        
        return timeWarpInfo;
    }
    
    private static void debug(String pattern, Object...arguments) {
//    	String msg = MessageFormat.format("[AbeelDTW]"+pattern, arguments);
//    	System.out.println(msg);
	}





	public static CostMatrix createCostMatrix(TimeSeries tsI, TimeSeries tsJ, SearchWindow window){
    	WindowMatrix costMatrix = new WindowMatrix(window);
//    	Pair<Integer, Integer> lastMinPair = null;
    	for (ColMajorCell currentCell : window) {
            int i = currentCell.getCol();
            int j = currentCell.getRow();
            
           
            Double euclideanDist = euclideanDist(tsI.getMeasurementVector(i), tsJ.getMeasurementVector(j));
            Map<Pair<Integer, Integer>, Double> localConstraints = window.getLocalConstaints().createLocalConstraints(i, j);
           
            debug("[createCostMatrix] [{1};{2}]localConstraints: {0}", localConstraints, i, j);
//            if(localConstraints!=null){
//            	Set<Pair<Integer, Integer>> toRemove = new HashSet<Pair<Integer,Integer>>();
//            	for (Pair<Integer, Integer> pair : localConstraints.keySet()) {
//            		 if(lastMinPair != null && pair.snd().equals(lastMinPair.snd())){
////                		 localConstraints.containsKey(new Pair<Integer,Integer>(i,j));
//            			 toRemove.add(pair);
//                	 }
//				}
//            	for (Pair<Integer, Integer> pair : toRemove) {
//            		localConstraints.remove(pair);
//				}
//            }
            		
            
            DtwCalculationCtx values = extractConstraintValues(localConstraints, costMatrix);
            
           
            Double minGlobalCost = 0D;
            if(localConstraints!=null && !localConstraints.isEmpty()){
//            	Set<Pair<Integer, Integer>> toRemove = new HashSet<Pair<Integer,Integer>>();
//                for (Entry<Pair<Integer, Integer>, Double> pairValues : values.getLocalValues().entrySet()) {
//                	if(lastMinPair != null && pairValues.getKey().snd().equals(lastMinPair.snd())
//                			&& pairValues.getKey().fst()>0 && pairValues.getKey().snd()>0){
//                		 localConstraints.containsKey(new Pair<Integer,Integer>(i,j));
//                		 toRemove.add(pairValues.getKey());
//                	}
//                	if(lastMinPair != null && pairValues.getKey().fst().equals(lastMinPair.fst())
//                			&& pairValues.getKey().fst()>0 && pairValues.getKey().snd()>0){
//                		 localConstraints.containsKey(new Pair<Integer,Integer>(i,j));
//                		 toRemove.add(pairValues.getKey());
//                	}
//                	for (Pair<Integer, Integer> pair : toRemove) {
//                		Double val = values.getLocalValues().get(pair);
//                		if(val != null){
//                			values.getLocalValues().put(pair, val*10);
//                		}
//    				}
//    			}
            	
            	Pair<Integer, Integer> minPair = values.getMinPair();
            	 minGlobalCost = values.getLocalValues().get(minPair);
//            	 minGlobalCost = minGlobalCost == null?Double.POSITIVE_INFINITY:minGlobalCost;
//            	 lastMinPair = minPair;
                 debug("[createCostMatrix] min:{1}; localConstraints: {0}; val {2}", values.getLocalValues(), 
                		 	values.getMinPair(), euclideanDist+minGlobalCost);
            }
            debug("[createCostMatrix] [{0};{1}]; euclideanDist: {2} + minGlobalCost {3}", i, j, euclideanDist, minGlobalCost);
            costMatrix.put(i, j, euclideanDist+minGlobalCost);
        }
        return costMatrix;
    	
    }
    


	private static DtwCalculationCtx extractConstraintValues(
			Map<Pair<Integer, Integer>, Double> localConstraints, CostMatrix costMatrix) {
		
		if(localConstraints == null){
			return null;
		}

		Double minValue = null;
		Pair<Integer, Integer> minPair = null;
		Map<Pair<Integer, Integer>, Double> localValues = new HashMap<Pair<Integer, Integer>, Double>();
		for (Entry<Pair<Integer, Integer>, Double> pairEntry : localConstraints.entrySet()) {
			Pair<Integer, Integer> pair = pairEntry.getKey();
			Double cost = costMatrix.get(pair.fst(), pair.snd());
			 debug("[extractConstraintValues] pair:{0}; cost: {1};", pair,cost);
			localValues.put(pair, cost);
			if (minPair == null) {
				minPair = pair;
				minValue = cost;
			}
			//if atleast one [0;0] item in the list this will always win.
			if(pair.fst() == 0 && pair.snd() ==0 ){
				minPair = pair;
				minValue = cost;
				break;
			}
			if (minValue > cost) {
				minPair = pair;
				minValue = cost;
			}
		}
//		System.out.println("[createCostMatrix]minPair: " +minPair);
		if(minValue !=null &&  minValue.isInfinite()){
//			System.out.println("Infinity");
		}
		DtwCalculationCtx ctx = new DtwCalculationCtx(localValues, minPair);
		return ctx;
	}
	

	private static Pair<StatisticalSummary, RealMatrix> transformCostMatrix(CostMatrix costMatrix,
			int maxI, int maxJ) {
    	Array2DRowRealMatrix resultCostMatrix = new Array2DRowRealMatrix(maxJ,maxI);
    	SummaryStatistics stats = new SummaryStatistics();
    	for (int i = 0; i < maxI; i++) {
			for (int j = 0; j < maxJ; j++) {
				//Abeel and Apache has different oreder understanding:
				//Abeel: col, row
				Double val = costMatrix.get(i, j);
				//Apache row, col
				resultCostMatrix.setEntry(j, i, val);
				if(!val.isInfinite()){
					stats.addValue(val);
				}
			}
		}
		return new Pair<StatisticalSummary, RealMatrix>(stats,resultCostMatrix);
	}

	private static double euclideanDist(double vector1[], double vector2[]) {
        if (vector1.length != vector2.length)
            throw new InternalError("ERROR:  cannot calculate the distance between vectors of different sizes.");
        double sqSum = 0.0D;
        for (int x = 0; x < vector1.length; x++){
            double diff=vector1[x]-vector2[x];
            sqSum+=diff*diff;
        }
        return Math.sqrt(sqSum);
    }
}