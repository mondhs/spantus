/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.exp.segment.services;

import java.util.Map;
import java.util.Set;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public interface ProcessReader {
	public SampleInfo processReader(IExtractorInputReader reader, ProcessReaderInfo processReaderInfo);
	public Set<IThreshold> getFilterThresholdByName(Set<IThreshold> set, String contains);
	public Map<String, Set<String>> generateAllCompbinations(Set<? extends IGeneralExtractor> thresholds, int combinationDepth);
	public <T extends IGeneralExtractor> Set<T> getThresholdSet(Set<T> thresholds, Set<String> thresholdNames);
	public String getName(IGeneralExtractor threshold);
}
