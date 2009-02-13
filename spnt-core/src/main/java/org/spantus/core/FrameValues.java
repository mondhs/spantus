
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.20
 *
 */
public class FrameValues extends LinkedList<Float>{
	
//	Logger log = Logger.getLogger(FrameValues.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	public static final int DEFAULT_FRAME_BUFFER_SIZE = 65536;
//	int frameBufferSize;
	float sampleRate = 1;


	public FrameValues() {
//		setFrameBufferSize(DEFAULT_FRAME_BUFFER_SIZE);
	}
	
	@SuppressWarnings("unchecked")
	public FrameValues(Collection collection){
		addAll(collection);
	}
	public FrameValues(Float[] floats) {
		addAll(Arrays.asList(floats));
	}

	
//	public int getFrameBufferSize() {
//		return frameBufferSize;
//	}
//	public void setFrameBufferSize(int bufferSize) {
//		this.frameBufferSize = bufferSize;
//	}
	
	public void add(int index, float element) {
		super.add(index, new Float(element));
	}
	
	
	public FrameValues subList(int fromIndex, int toIndex) {
		List<Float> lst = super.subList(fromIndex, toIndex);
		return new FrameValues(lst);
	}
	
	
	public Float[] toArray() {
		Object[] objs = super.toArray();
		Float[] floats = new Float[objs.length];
		System.arraycopy(objs, 0, floats, 0, objs.length);
		
		return floats;
	}
	
	public static Float[] toArray(List<Float> list) {
		Object[] objs = list.toArray();
		Float[] floats = new Float[objs.length];
		System.arraycopy(objs, 0, floats, 0, objs.length);
		return floats;
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
