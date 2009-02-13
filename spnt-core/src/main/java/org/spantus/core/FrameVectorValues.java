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
package org.spantus.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class FrameVectorValues extends LinkedList<FrameValues>{
	
	public static float max = Float.MIN_VALUE;
	public static float min = Float.MAX_VALUE;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_FRAME_BUFFER_SIZE = 65536;
	int frameBufferSize;
	float sampleRate = 1;

	public FrameVectorValues() {
		setFrameBufferSize(DEFAULT_FRAME_BUFFER_SIZE);
	}
	
	@SuppressWarnings("unchecked")
	public FrameVectorValues(Collection collection){
		addAll(collection);
	}

	
	public int getFrameBufferSize() {
		return frameBufferSize;
	}
	public void setFrameBufferSize(int bufferSize) {
		this.frameBufferSize = bufferSize;
	}
	
	
	public void add(int index, FrameValues element) {
		super.add(index, element);
	}
	
	public void add(Float[] floats) {
		for (int i = 0; i < floats.length; i++) {
			max = Math.max(floats[i], max);
			min = Math.min(floats[i], min);
		}
		FrameValues values = new FrameValues(floats);
		super.add(values);
	}
	
	public void add(List<Float> floats) {
//		for (int i = 0; i < floats.length; i++) {
//			max = Math.max(floats[i], max);
//			min = Math.min(floats[i], min);
//		}
		FrameValues values = new FrameValues(floats);
		super.add(values);
	}

	
	public void addAll(FrameVectorValues floats) {
		super.addAll(floats);
	}

	
	public Float get(int x, int y){
		return this.get(0).get(0);
	}
	
	public FrameVectorValues transform(){
		FrameVectorValues fv3 = new FrameVectorValues();
		fv3.setSampleRate(this.getSampleRate());
		for (int i = 0; i < getFirst().size(); i++) {
			fv3.add(new FrameValues());				
		}
		for (FrameValues fv : this) {
			fv.setSampleRate(getSampleRate());
			for (int i = 0; i < fv.size(); i++) {
				fv3.get(i).add(fv.get(i));
			}
		}
		fv3.setSampleRate(this.getSampleRate());
		return fv3;
	}
	
	
	public FrameVectorValues subList(int fromIndex, int toIndex) {
		List<?> lst = super.subList(fromIndex, toIndex);
		return new FrameVectorValues(lst);
	}

	public float getTime() {
		return size()/sampleRate;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	public float toTime(int i){
		return (float)i / (sampleRate);
	}
	public int toIndex(float f){
		return (int)(f * sampleRate);
	}
}
