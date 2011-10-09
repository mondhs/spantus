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
package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.logger.Logger;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.01
 *
 */
public abstract class AbstractExtractor extends AbstractGeneralExtractor implements IExtractor {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(AbstractExtractor.class);



	public FrameValues calculate(Long sampleNum, FrameValues values) {
		return (FrameValues) super.calculate(sampleNum, values);
	}

	protected  IValues calculateAndStoreWindow(FrameValues windowedWindow, IValues storedValues){
		FrameValues fv = (FrameValues) storedValues;
		if(fv == null){
			fv = new FrameValues();
			storedValues = fv;
		}
		fv.addAll(calculateWindow(windowedWindow));
		return storedValues;
	}
	protected FrameValues calculateWindow(FrameValues windowedWindow, IValues storedValues){
            FrameValues fv = calculateWindow(windowedWindow);
            fv.setSampleRate(getExtractorSampleRate());
            return fv;
	}
	
	public int getDimension() {
		return 1;
	}

	
	public FrameValues getOutputValues() {
		throw new RuntimeException("Should be never called");
	}
	
	public Double getExtractorSampleRate() {
		return getWindowBufferProcessor().calculateExtractorSampleRate(getConfig());//(getConfig().getSampleRate()/(getConfig().getWindowSize()));
	}



}
