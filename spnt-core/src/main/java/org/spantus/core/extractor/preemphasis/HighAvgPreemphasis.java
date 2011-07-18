package org.spantus.core.extractor.preemphasis;

import java.util.LinkedList;

import org.spantus.math.VectorUtils;

public class HighAvgPreemphasis implements Preemphasis {

	public static final Integer DEFAULT_BUFFER_SIZE = 10;
	
	private Preemphasis preemphasis;
	
	private LinkedList<Double> buffer;
	
	private Integer bufferSize;
	
	
	public HighAvgPreemphasis() {
		preemphasis = new HighPreemphasis();
		buffer = new LinkedList<Double>();
	}
	
	public Double process(Double currentValue) {
		 buffer.addLast(currentValue);
		 Double avg = VectorUtils.avg(buffer);
		 Double val = preemphasis.process(currentValue - avg);
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
