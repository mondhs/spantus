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
import org.spantus.extractor.AbstractExtractorVector;
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
public class WavformExtractor extends AbstractExtractorVector {
	public int getDevideInto() {
		return devideInto;
	}


	public void setDevideInto(int devideInto) {
		this.devideInto = devideInto;
	}

	private Logger log = Logger.getLogger(getClass());
	private Double previousMin = null;
	private Double previousMax = null;
	private int devideInto = 3;
	Context ctx = new Context();

	public WavformExtractor() {
//		devideInto = 5;
	}	

	
	public WavformExtractor(ExtractorParam param) {
		this();
		setParam(param);
		if(log.isDebugMode()){
			log.debug("param:{0};", param);
		}

	}
	
	public int getDimension() {
		return 2;
	}
	
	private FrameValues push(Double float1, Context ctx){
		FrameValues fv = null;
		ctx.max = Math.max(ctx.max, float1);
		ctx.min = Math.min(ctx.min, float1);
		if(ctx.index <= 0){
			fv = new FrameValues();
			fv.setSampleRate((double) 1);
			previousMin = previousMin == null?ctx.min:previousMin;
			previousMax = previousMax == null?ctx.max:previousMax;
			fv.add((ctx.min+previousMin)*.5F);
			fv.add((ctx.max+previousMax)*.5F);
			previousMin = ctx.min;
			previousMax = ctx.max;
			ctx.max = -Double.MAX_VALUE;
			ctx.min = Double.MAX_VALUE;

		}
		ctx.index--;
		return fv;
	}
	
	public FrameVectorValues calculateWindow(FrameValues window) {
		FrameVectorValues calculatedValues = newFrameVectorValues();

		
		//if single value in the window, put got sample as min and max
		if(window.size() == 1){
			FrameValues fv = new FrameValues();
			fv.addAll(window);
			fv.addAll(window);
			calculatedValues.add(fv);	
			return calculatedValues;	
		}
		
		int chunkSize  = (window.size()/devideInto)+1;
//		ctx.chunkSize = chunkSize;
		if(ctx.index<0){
			ctx.index = chunkSize;
		}
		
		for (Double float1 : window) {
			FrameValues fv = push(float1, ctx);
			if(fv!=null){
				calculatedValues.add(fv);	
				ctx.index = chunkSize;
//				ctx.chunkSize = Math.min(ctx.chunkSize, window.size()-1);
			}
		}
		

//		fv.add(min);
//		fv.add(max);
		return calculatedValues;
	}	
	public String getName() {
		return ExtractorEnum.WAVFORM_EXTRACTOR.name();
	}
	
	@Override
	public Double getExtractorSampleRate() {
		return super.getExtractorSampleRate()*devideInto;
	}

	public class Context{
		 int index = -1;
//		 int chunkSize = 0;
		 Double max = -Double.MAX_VALUE;
		 Double min = Double.MAX_VALUE;
	}
	
}
