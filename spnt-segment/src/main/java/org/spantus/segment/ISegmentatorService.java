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

import java.util.Collection;
import java.util.Set;

import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
/**
 * Segmentation service for extracting/consolidating segment information from multiple classifiers
 *  
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 1, 2010
 *
 */
public interface ISegmentatorService {
	/**
	 * Segmentation with parameters. {@link #extractSegments(Set)} 
	 * @param classifiers
	 * @param param
	 * @return
	 */
	public MarkerSetHolder extractSegments(Collection<IClassifier> classifiers, SegmentatorParam param);
	/**
	 * extract segments information using default parameters. It should extract information from 
	 * classifiers {@link MarkerSetHolder#getMarkerSets()} should return at least {@link MarkerSetHolderEnum#word} level information. 
	 * in some case it can return other level info too.
	 * @param classifiers
	 * @return
	 */
	public MarkerSetHolder extractSegments(Set<IClassifier> classifiers);
}
