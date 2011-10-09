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
package org.spantus.extractor;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.logger.Logger;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.04.01
 * 
 */
public abstract class AbstractExtractorVector extends AbstractGeneralExtractor
		implements IExtractorVector {
	Logger log = Logger.getLogger(AbstractExtractorVector.class);

	public FrameVectorValues calculate(Long sampleNum, FrameValues values) {
		return (FrameVectorValues) super.calculate(sampleNum, values);
	}

	protected IValues calculateAndStoreWindow(FrameValues windowedWindow,
			IValues storedValues) {
		FrameVectorValues fv = (FrameVectorValues) storedValues;
		if (fv == null) {
			fv = new FrameVectorValues();
			storedValues = fv;
		}
		fv.addAll(calculateWindow(windowedWindow));
		return storedValues;
	}

//	protected FrameVectorValues calculateWindow(FrameValues windowedWindow,
//			FrameValues realValues) {
//		FrameVectorValues fv = calculateWindow(windowedWindow);
//		fv.setSampleRate(getExtractorSampleRate());
//		return fv;
//	}

	public FrameVectorValues getOutputValues() {
		throw new RuntimeException("Should be never call");
	}

	public Double getExtractorSampleRate() {
		return getWindowBufferProcessor().calculateExtractorSampleRate(
				getConfig());// (getConfig().getSampleRate()/(getConfig().getWindowSize()));
	}

}
