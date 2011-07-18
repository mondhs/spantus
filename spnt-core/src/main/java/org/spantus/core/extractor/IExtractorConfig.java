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
package org.spantus.core.extractor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
/**
 * Configuration interface. Interface is used as it would be possible write adapters for different feature calculations 
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public interface IExtractorConfig extends Cloneable, Serializable {
	public Double getSampleRate();
	public void setSampleRate(Double sampleRate);
	public int getWindowSize();
	public void setWindowSize(int windowSize);
//	public int getBitsPerSample();
	public int getBufferSize();
	public void setBufferSize(int bufferSize);
	public int getFrameSize();
	public void setFrameSize(int frameSize);
	public int getWindowOverlap();
	public void setWindowOverlap(int windowOverlap);
	public Set<String> getExtractors();
	public Map<String,ExtractorParam> getParameters();
	public String getWindowing();
	public void setWindowing(String windowing);
	public String getPreemphasis();
	public void setPreemphasis(String preemphasis);
	
}
