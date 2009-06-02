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
package org.spantus.extractor.impl;

import java.util.LinkedList;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.logger.Logger;
import org.spantus.math.MatrixUtils;
/**
 * 
 * Params logaritmic
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.06.02
 *
 */
public class MeanExtractor extends AbstractExtractor {
	Logger log = Logger.getLogger(getClass());
	

	private IExtractor extractor;
	LinkedList<Float> buffer;
	int order = 9;
	Float mean;
	Float stdev;

	
	
	public MeanExtractor() {
		getParam().setClassName(MeanExtractor.class.getSimpleName());
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = new FrameValues();
		FrameValues fv = getExtractor().calculateWindow(window);
		calculatedValues.add(calculateMean(fv));
		return calculatedValues;
	}	
	
	public Float calculateMean(FrameValues fv){
		Float meanCurrent = 0F;
		for (Float float1 : fv) {
			meanCurrent = calculateMean(float1);
		}
		return meanCurrent;
		
	}
	
	public Float calculateMean(Float value){
		LinkedList<Float> bufferValues = getBuffer(order);
		bufferValues.poll();
		bufferValues.add(value);
		int n = 0;
		Float meanShort = 0F;
		Float M2 = 0F;
		for (Float float1 : bufferValues) {
//			n = n + 1
			n++;
//			delta = x - mean
			Float delta = float1 - meanShort; 
//			mean = mean + delta/n
			meanShort = meanShort + delta/n;
//			M2 = M2 + delta*(x - mean)
			M2 = M2 + delta*(float1-meanShort);
		}
		mean = meanShort;
		if(n >0){
			stdev = M2/(n-1);
			stdev = (float)Math.sqrt(stdev);
		}
		return mean;
		
	}
	
//	public Float calculateMean(Double val){
//		//n = n + 1
//		index = index.add(BigDecimal.ONE);
//		BigDecimal bdval = BigDecimal.valueOf(val);
//		//delta = x - mean
//		BigDecimal delta = bdval.subtract(mean);
//		// mean = mean + delta/n
//		mean = mean.add(delta.divide(index,RoundingMode.HALF_UP));
//		//M2 = M2 + delta*(x - mean)
//		BigDecimal multiplicand = bdval.subtract(mean);
//		BigDecimal augend = delta.multiply(multiplicand);
//		variance = variance.add( augend ) ;
//		return mean.floatValue();
//		
//	}
//	private BigDecimal mean = BigDecimal.ZERO.setScale(6);
//	private BigDecimal index = BigDecimal.ZERO.setScale(0);
//	private BigDecimal variance = BigDecimal.ZERO.setScale(6);
//	private BigDecimal sum1 = BigDecimal.ZERO.setScale(6);
//	public Float calculateMean(Double val){
//		//n = n + 1
//		index = index.add(BigDecimal.ONE);
//		BigDecimal bdval = BigDecimal.valueOf(val);
//		//sum1 = sum1 + x
//		sum1 = sum1.add(bdval);
//		mean = sum1.divide(index,RoundingMode.HALF_UP);
//		return mean.floatValue();
//		
//	}
	
	LinkedList<Float> getBuffer(int order){
		if(buffer == null){
			buffer = new LinkedList<Float>();
			buffer.addAll(MatrixUtils.zeros(order));
		}
		return buffer;
	}
	
	public String getName() {
		return ExtractorModifiersEnum.mean.name()+"_" + getExtractor().getName();
	}
	
	@Override
	public void setConfig(IExtractorConfig conf) {
		extractor.setConfig(conf);
	}
	@Override
	public IExtractorConfig getConfig() {
		return extractor.getConfig();
	}
	
	public IExtractor getExtractor() {
		if(extractor == null){
			extractor = new EnergyExtractor();
		}
		return extractor;
	}

	public void setExtractor(IExtractor extractor) {
		this.extractor = extractor;
	}

	public Float getMean() {
		return mean.floatValue();
	}

	public Float getStdev() {
		//variance = M2/(n - 1)
//		BigDecimal divisor = index.subtract(BigDecimal.ONE);
//		if(divisor.equals(BigDecimal.ZERO)){
//			return 0F;
//		}
//		return variance.divide(divisor, RoundingMode.HALF_UP).floatValue();
		return stdev;
	}


}
