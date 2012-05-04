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
package org.spantus.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.spantus.utils.Assert;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.20
 *
 */
public class FrameValues extends LinkedList<Double> implements IValues, List<Double>{
	
//	Logger log = Logger.getLogger(FrameValues.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double sampleRate = null;
	private Double milsSamplePeriod = 1D; 
	private Double minValue = null;
	private Double maxValue = null;
    private Long frameIndex = null;


	public FrameValues() {
	}
	
	public FrameValues(Collection<Double> collection){
		addAll(collection);
		if(collection instanceof FrameValues){
			setSampleRate(((FrameValues)collection).getSampleRate());
		}
	}
	
	public FrameValues(Double mySampleRate ){
		setSampleRate(mySampleRate);
	}
	
	public FrameValues(Collection<Double> collection, Double mySampleRate ){
		addAll(collection);
		setSampleRate(mySampleRate);
	}
	
	public FrameValues(Double[] floats) {
		addAll(Arrays.asList(floats));
	}

	
	@Override
	public boolean add(Double e) {
		updateMinMax(e);
		return super.add(e);
	}
	@Override
	public boolean addAll(Collection<? extends Double> c) {
		if(c instanceof FrameValues){
			FrameValues fv = (FrameValues)c;
			updateMinMax(fv.getMinValue());
			updateMinMax(fv.getMaxValue());
		}
		if(c instanceof FrameValues){
//			setSampleRate(((FrameValues)c).getSampleRate());
		}
		return super.addAll(c);
	}
	
	public void add(int index, Double element) {
		super.add(index, new Double(element));
	}
	
	public void add(int index, double element) {
		super.add(index, new Double(element));
	}
	
	@SuppressWarnings("unchecked")
	public FrameValues subList(int fromIndex, int toIndex) {
		List<Double> lst = super.subList(fromIndex, toIndex);
		FrameValues fv = new FrameValues(lst); 
		fv.setSampleRate(this.getSampleRate());
		return fv;
	}
	
	
	public synchronized Double[] toArray() {
		Object[] objs = super.toArray();
		Double[] floats = new Double[objs.length];
		System.arraycopy(objs, 0, floats, 0, objs.length);
		return floats;
	}
        public synchronized double[] toDoubleArray() {
		Object[] objs = super.toArray();
		double[] doulbes = new double[objs.length];
                int i = 0;
                for (double p : this) {
                    doulbes[i] = p;
                }
		return doulbes;
	}
	
	public static Float[] toArray(List<Float> list) {
		Object[] objs = list.toArray();
		Float[] floats = new Float[objs.length];
		System.arraycopy(objs, 0, floats, 0, objs.length);
		return floats;
	}
	
	public Double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		this.sampleRate = sampleRate;
		milsSamplePeriod = 1000/sampleRate;
	}
	public Long getTime() {
		return indextoMils(size());
	}
	public Long toTime(int i){
		return indextoMils(i);
	}
	public Long toTime(long i){
		return indextoMils(i);
	}
	public Long indextoMils(long i){
		return (long)(milsSamplePeriod * i);
	}
	public int toIndex(Long time){
		Double fTime = time.doubleValue();
		return (int)(fTime * sampleRate/1000)-1;
	}

	public Double getMinValue() {
		return minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}
	public Double getDeltaValue() {
		Assert.isTrue(maxValue!= null);
		Assert.isTrue(minValue!= null);
		return maxValue-minValue;
	}
	
	
	public void updateMinMax(Double value){
		if(value == null){
			return;
		}
		if(minValue == null){
			minValue = value;
		}else{
			minValue = Math.min(minValue, value);
		}
		if(maxValue == null){
			maxValue = value;
		}else{
			maxValue = Math.max(maxValue, value);
		}
	}
        public int getDimention() {
            return 1;
        }

        public Long getFrameIndex() {
            return frameIndex;
        }

        public void setFrameIndex(Long frameIndex) {
            this.frameIndex = frameIndex;
        }




}
