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

import java.util.LinkedList;

import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.extractor.impl.EnergyExtractor;
import org.spantus.logger.Logger;
import org.spantus.math.MatrixUtils;
/**
 * 
 * Mean of the frame
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2009.06.02
 *
 */
public class MeanExtractor extends AbstractExtractorModifier {
	Logger log = Logger.getLogger(getClass());
	

	private IExtractor extractor;
	LinkedList<Double> buffer;
	int order = 9;
	Double mean;
	Double stdev;

	
	
	public MeanExtractor() {
		getParam().setClassName(MeanExtractor.class.getSimpleName());
	}

	public FrameValues calculateWindow(FrameValues window) {
		FrameValues calculatedValues = new FrameValues();
		FrameValues fv = getExtractor().calculateWindow(window);
		calculatedValues.add(calculateMean(fv));
		return calculatedValues;
	}	
	
	public Double calculateMean(FrameValues fv){
		Double meanCurrent = 0D;
		for (Double float1 : fv) {
			meanCurrent = calculateMean(float1);
		}
		return meanCurrent;
		
	}
	
	public Double calculateMean(Double value){
		LinkedList<Double> bufferValues = getBuffer(getOrder());
		bufferValues.poll();
		bufferValues.add(value);
		int n = 0;
		Double meanShort = 0D;
		Double M2 = 0D;
		for (Double float1 : bufferValues) {
//			n = n + 1
			n++;
//			delta = x - mean
			Double delta = float1 - meanShort; 
//			mean = mean + delta/n
			meanShort = meanShort + delta/n;
//			M2 = M2 + delta*(x - mean)
			M2 = M2 + delta*(float1-meanShort);
		}
		mean = meanShort;
		if(n >0){
			stdev = M2/(n-1);
			stdev = Math.sqrt(stdev);
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
	
	LinkedList<Double> getBuffer(int order){
		if(buffer == null){
			buffer = new LinkedList<Double>();
			buffer.addAll(MatrixUtils.zeros(order));
		}
		return buffer;
	}
	
	public String getName() {
		return getExtractor().getName();
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

	public Double getStdev() {
		//variance = M2/(n - 1)
//		BigDecimal divisor = index.subtract(BigDecimal.ONE);
//		if(divisor.equals(BigDecimal.ZERO)){
//			return 0F;
//		}
//		return variance.divide(divisor, RoundingMode.HALF_UP).floatValue();
		return stdev;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

        

}
