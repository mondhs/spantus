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

import java.util.Set;

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
	public MarkerSetHolder extractSegments(Set<IClassifier> classifiers) {
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
}
