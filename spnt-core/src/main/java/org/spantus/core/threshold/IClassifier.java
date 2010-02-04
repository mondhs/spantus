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
package org.spantus.core.threshold;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.segment.ISegmentator;
/**
 * Classification interface
 * @author mondhs
 *
 */
public interface IClassifier extends IExtractor,ISegmentator{
	public FrameValues getThresholdValues();
	/**
	 * Add listener
	 * @param classificationListener
	 * @return
	 */
	public boolean addClassificationListener(IClassificationListener classificationListener);
	/**
	 * remove listener
	 * @param classificationListener
	 * @return
	 */
	public boolean removeClassificationListener(IClassificationListener classificationListener);
}
