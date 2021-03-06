/**
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
package org.spantus.core.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.IValues;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.11
 *
 */
public interface IGeneralExtractor<T extends IValues>{
	
	public String getName();
        public String getRegistryName();
	
	public T calculateWindow(Long sample, FrameValues values);
        
	public T calculateWindow(FrameValues values);
	
	public void flush();
	
	public Double getExtractorSampleRate();
	
	public IExtractorConfig getConfig();
	
	public void setConfig(IExtractorConfig config);
	
	public long getOffset();
	
	public T getOutputValues();
        
        public ExtractorParam getParam();

        public void setParam(ExtractorParam param);
}
