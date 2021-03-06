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
package org.spantus.extractor.modifiers;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
import org.spantus.extractor.AbstractExtractor;
/**
 * Modifier abstract class. Modifier transforms extractor data.
 * @author Mindaugas Greibus
 * 
 * Created Feb 4, 2010
 *
 */
public abstract class AbstractExtractorModifier extends AbstractExtractor {
	public abstract IExtractor getExtractor();

	protected FrameValues newFrameValues(FrameValues window) {
		FrameValues calculatedValues = new FrameValues();
		calculatedValues.setSampleRate(window.getSampleRate());
		return calculatedValues;
	}
	
    @Override
    public void flush() {
        super.flush();
        getExtractor().flush();
    }
        @Override
    public String getRegistryName() {
        return getExtractor().getRegistryName();
    }

    @Override
    public ExtractorParam getParam() {
        return getExtractor().getParam();
    }

    @Override
    public void setParam(ExtractorParam param) {
        getExtractor().setParam(param);
    }
        
}
