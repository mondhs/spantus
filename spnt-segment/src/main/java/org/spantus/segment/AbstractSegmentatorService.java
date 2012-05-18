/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.segment.offline.BaseDecisionSegmentatorParam;
/**
 * Abstract class for segmentation services
 * 
 * @author Mindaugas Greibus
 *
 */
public abstract class AbstractSegmentatorService implements ISegmentatorService {
	/*
	 * (non-Javadoc)
	 * @see org.spantus.segment.ISegmentatorService#extractSegments(java.util.Set)
	 */
	public MarkerSetHolder extractSegments(Collection<IClassifier> classifiers) {
		return extractSegments(classifiers, null);
	}
	/**
	 * safe parame creation. if null create new, else return the same
	 * @param param
	 * @return
	 */
	protected BaseDecisionSegmentatorParam createSafeParam(SegmentatorParam param){
		if(param != null && param instanceof BaseDecisionSegmentatorParam){
			return (BaseDecisionSegmentatorParam)param; 
		}
		return new BaseDecisionSegmentatorParam();
		
	}
	
	/**
	 * 
	 * @param votes
	 * @return
	 */
	private static List<Double> calcCoefs(List<Double> votes){
		List<Double> coefs = new ArrayList<Double>(votes.size());
		for (int z = 0; z < votes.size(); z++) {
			coefs.add(calcCoef(votes, z));
		}
		return coefs;
	}
	/**
	 * 
	 * @param votes
	 * @param z
	 * @return
	 */
	private static Double calcCoef(List<Double> votes, int z){
		Double result = 1D;
		double ez = votes.get(z);
		double alpha = Math.pow(.9, 2);
		int j =-1;
		for (Double ej : votes) {
			j++;
			if(j==z){
				continue;
			}
			double dzj=Math.pow(ez-ej,2);
			result *= dzj/alpha;
			
		}
		result = 1/(1+result);
		return result;
	}
	/**
	 * 
	 * @param collection
	 * @return
	 */
	public Double calculateVoteResult(Collection<Double> collection){
		List<Double> coefs = calcCoefs(new ArrayList<Double>(collection));
		Iterator<Double> voteIterator = collection.iterator();
		Double upSum = 0D;
		Double downSum = 0D;
		for (Iterator<Double> coefIterator = coefs.iterator(); coefIterator.hasNext();) {
			Double coefVal = (Double) coefIterator.next();
			Double voteVal = (Double) voteIterator.next();
			upSum += coefVal*voteVal;
			downSum += coefVal;  
		}
		return upSum/downSum;
	}
	
}
