/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.AbstractExtractor3D;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class WavformExtractor extends AbstractExtractor3D {
	private Logger log = Logger.getLogger(getClass());
	private Float previousMin = null;
	private Float previousMax = null;
	private int devideInto = 3;

	public WavformExtractor() {
//		devideInto = 5;
	}	

	
	public WavformExtractor(ExtractorParam param) {
		this();
		setParam(param);
	}
	
	public int getDimension() {
		return 2;
	}
	
	private FrameValues push(Float float1, Context ctx){
		FrameValues fv = null;
		ctx.max = Math.max(ctx.max, float1);
		ctx.min = Math.min(ctx.min, float1);
		if(ctx.index == ctx.chunkSize){
			fv = new FrameValues();
			previousMin = previousMin == null?ctx.min:previousMin;
			previousMax = previousMax == null?ctx.max:previousMax;
			fv.add((ctx.min+previousMin)*.5F);
			fv.add((ctx.max+previousMax)*.5F);
//			log.debug("min:{0}; ;max:{1}, index:{2}", ctx.min , ctx.max, ctx.index);
			previousMin = ctx.min;
			previousMax = ctx.max;
			ctx.max = -Float.MAX_VALUE;
			ctx.min = Float.MAX_VALUE;

		}
		ctx.index++;
		return fv;
	}
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = new FrameVectorValues();
		
		Context ctx = new Context();
		
		int chunkSize  = (window.size()/devideInto)+1;
		ctx.chunkSize = chunkSize;
		
		for (Float float1 : window) {
			FrameValues fv = push(float1, ctx);
			if(fv!=null){
				calculatedValues.add(fv);	
				ctx.chunkSize = ctx.index + chunkSize;
				ctx.chunkSize = Math.min(ctx.chunkSize, window.size()-1);
			}
		}
		

//		fv.add(min);
//		fv.add(max);

		

		return calculatedValues;
	}	
	public String getName() {
		return ExtractorEnum.WAVFORM_EXTRACTOR.toString();
	}
	
	@Override
	public float getExtractorSampleRate() {
		return super.getExtractorSampleRate()*devideInto;
	}

	public class Context{
		 int index = 0;
		 int chunkSize = 0;
		 Float max = -Float.MAX_VALUE;
		 Float min = Float.MAX_VALUE;
	}
	
}
