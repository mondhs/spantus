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

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.02.29
 * 
 */
public class FrameVectorValues extends LinkedList<List<Double>> implements
		IValues {

	public Double maxValue = -Double.MAX_VALUE;
	public Double minValue = Double.MAX_VALUE;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_FRAME_BUFFER_SIZE = 65536;
	int frameBufferSize;
	private Double sampleRate = null;
	private Double milsSamplePeriod = 1D; 
	
	public FrameVectorValues() {
		setFrameBufferSize(DEFAULT_FRAME_BUFFER_SIZE);
	}

	public FrameVectorValues(List<List<Double>> collection) {
		if(collection instanceof FrameVectorValues){
			FrameVectorValues fvv = (FrameVectorValues)collection;
			setMinValue(fvv.getMinValue());
			setMinValue(fvv.getMaxValue());
		}else{
			for (List<Double> list : collection) {
				for (Double value : list) {
					updateMinMax(value);
				}
			}
		}
		addAll(collection);
	}

	public FrameVectorValues(Double sampleRate) {
		setSampleRate(sampleRate);
	}

	public int getFrameBufferSize() {
		return frameBufferSize;
	}

	public void setFrameBufferSize(int bufferSize) {
		this.frameBufferSize = bufferSize;
	}

	public void add(int index, FrameValues element) {
		updateMinMax(element.getMinValue());
		updateMinMax(element.getMaxValue());
		super.add(index, element);
	}

	public void add(Double[] floats) {
		for (int i = 0; i < floats.length; i++) {
			updateMinMax(floats[i]);
		}
		FrameValues values = new FrameValues(floats);
		super.add(values);
	}

	public boolean add(List<Double> floats) {
		FrameValues values = new FrameValues(floats);
		
		if(floats instanceof FrameValues){
			FrameValues fv = (FrameValues)floats;
			updateMinMax(fv.getMinValue());
			updateMinMax(fv.getMaxValue());
			values.setSampleRate(fv.getSampleRate());
			values.setFrameIndex(fv.getFrameIndex());
		}else{
			for (Double double1 : floats) {
				updateMinMax(double1);
			}
		}
		
		
		return super.add(values);
	}

	public void addAll(FrameVectorValues floats) {
		setMinValue(floats.getMinValue());
		setMaxValue(floats.getMaxValue());
		super.addAll(floats);
	}
        @Override
        public <T extends IValues> void addValues(T values) {
            this.addAll((FrameVectorValues)values);
        }

	public Double get(int x, int y) {
		return this.get(0).get(0);
	}

	public FrameVectorValues transform() {
		FrameVectorValues fv3 = new FrameVectorValues();
		fv3.setSampleRate(this.getSampleRate());
		for (int i = 0; i < getFirst().size(); i++) {
			fv3.add(new FrameValues());
		}
		for (List<Double> fv : this) {
			// fv.setSampleRate(getSampleRate());
			for (int i = 0; i < fv.size(); i++) {
				fv3.get(i).add(fv.get(i));
			}
		}
		fv3.setSampleRate(this.getSampleRate());
		return fv3;
	}

	@SuppressWarnings("unchecked")
	public FrameVectorValues subList(int fromIndex, int toIndex) {
		List<List<Double>> lst = super.subList(fromIndex, toIndex);
		FrameVectorValues fv = new FrameVectorValues(lst);
		fv.setSampleRate(this.getSampleRate());
		return fv;
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

	public Long getTime() {
		return indextoMils(size());
	}

	public Double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(Double sampleRate) {
		milsSamplePeriod = 1000/sampleRate;
		this.sampleRate = sampleRate;
	}

	public Long toTime(int i) {
		return indextoMils(i);
	}
	public Long toTime(long i) {
		return indextoMils(i);
	}

	public int toIndex(Long time){
		Double fTime = time.doubleValue();
		int aTime= (int)(fTime * sampleRate/1000)-1;
		return Math.max(aTime, 0);
	}
	
	public Long indextoMils(long i){
		return (long)(milsSamplePeriod * i);
	}

	public int getDimention() {
		if (size() == 0) {
			return 0;
		} else {
			return get(0).size();
		}
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}


}
