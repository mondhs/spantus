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
package org.spantus.core.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.preemphasis.Preemphasis;
import org.spantus.core.extractor.preemphasis.PreemphasisFactory;
import org.spantus.math.windowing.HammingWindowing;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class WraperExtractorReader {
	private AudioFormat format;
	private IExtractorInputReader reader;
	private List<List<Byte>> shortBuffers;
	private Preemphasis preemphasisFilter;
	private Long sample;
	private Double lastValue;
	private boolean smooth = false;
	private Integer smoothingSize = null; 
	private HammingWindowing hammingWindowing;
	
	public WraperExtractorReader(IExtractorInputReader reader, int size) {
		this.reader = reader;
		shortBuffers = new ArrayList<List<Byte>>(size);
		for (int i = 0; i < size; i++) {
			List<Byte> shortBuffer = new ArrayList<Byte>(3);
			shortBuffers.add(shortBuffer);
		}
		preemphasisFilter = PreemphasisFactory.createPreemphasis(reader.getConfig().getPreemphasis());
		sample = 0L;
	}	
	
	public void put(byte value){
		switch (format.getSampleSizeInBits()) {
		case 8:
				put(AudioUtil.read8(value, getFormat()) );
				break;
		case 16:
			List<Byte> shortBuffer = shortBuffers.get(0); 
			shortBuffer.add(value);
			if(shortBuffer.size() == 2){
				Double d = AudioUtil.read16(shortBuffer.get(0), 
						shortBuffer.get(1), 
						getFormat());
				put(d );
				shortBuffer.clear();
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
		
	}
	/**
	 * put byte list
	 * @param value
	 */
	public void put(List<Byte> value){
		Double sum = null;
		switch (format.getSampleSizeInBits()) {
		case 8:
			sum = 0D;
			for (Byte byte1 : value) {
				sum += AudioUtil.read8(byte1, getFormat());
			}
				break;
		case 16:
			Iterator<Byte> valIterator = value.iterator();
			Iterator<List<Byte>> buffIterator = shortBuffers.iterator();
			while(valIterator.hasNext()){
				Byte ival = valIterator.next();
				buffIterator.next().add(ival);
			}
			if(shortBuffers.get(0).size() == 2){
				for (List<Byte> shortBuffer : shortBuffers) {
					if(shortBuffer.size()==2){
						sum = sum == null?0D:sum;
						sum += AudioUtil.read16(
								shortBuffer.get(0), 
							shortBuffer.get(1), 
							getFormat());
					}
					shortBuffer.clear();
				}
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
		//add value tooo the lis
		put(sum);
	}
	
	public void put(Double val){
		if(val!=null){
			if(smooth  == true && smoothingSize != null){
				val *=  getHammingWindowing().calculate(smoothingSize, sample.intValue());
		}
			reader.put(sample++, preemphasis(val));
		}
	}
	
	/**
	 * Calculate post process data
	 * @param currentValue
	 * @return
	 */
	protected Double preemphasis(Double currentValue){
		Double processedValue = preemphasisFilter.process(currentValue);
		setLastValue(processedValue);
		return processedValue;
	}

	
	public void pushValues(){
		reader.pushValues(sample);
	}
	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public IExtractorInputReader getReader() {
		return reader;
	}

	public Long getSample() {
		return sample;
	}

	public Double getLastValue() {
		return lastValue;
	}

	protected void setLastValue(Double lastValue) {
		this.lastValue = lastValue;
	}

	public HammingWindowing getHammingWindowing() {
		if(hammingWindowing == null){
			hammingWindowing = new HammingWindowing();
		}
		return hammingWindowing;
	}

	public boolean isSmooth() {
		return smooth;
	}

	public void setSmooth(boolean smooth) {
		this.smooth = smooth;
	}

	public Integer getSmoothingSize() {
		return smoothingSize;
	}

	public void setSmoothingSize(Integer smoothingSize) {
		this.smoothingSize = smoothingSize;
	}

	
}
