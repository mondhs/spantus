package org.spantus.core.extractor.preemphasis;

import java.util.LinkedList;

import org.spantus.math.VectorUtils;

public class HighAvgPreemphasis implements Preemphasis {

	public static final Integer DEFAULT_BUFFER_SIZE = 10;
	
	private Preemphasis preemphasis;
	
	private LinkedList<Float> buffer;
	
	private Integer bufferSize;
	
	
	public HighAvgPreemphasis() {
		preemphasis = new HighPreemphasis();
		buffer = new LinkedList<Float>();
	}
	
	public Float process(Float currentValue) {
		buffer.addLast(currentValue);
		float avg = VectorUtils.avg(buffer);
		Float val = preemphasis.process(currentValue - avg);
		if(buffer.size()>getBufferSize()){
			buffer.removeFirst();
		}
		return val;
	}

	public Integer getBufferSize() {
		if(bufferSize == null){
			bufferSize = DEFAULT_BUFFER_SIZE;
		}
		return bufferSize;
	}

	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}
	


}
