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
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class WraperExtractorReader {
	AudioFormat format;
	IExtractorInputReader reader;
//	List<Byte> shortBuffer;
	List<List<Byte>> shortBuffers;
	Preemphasis preemphasisFilter;
	Long sample;
	Float lastValue;
	
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
				reader.put(sample++, preemphasis( 
						AudioUtil.read8(value, getFormat()) 
						));
				break;
		case 16:
			List<Byte> shortBuffer = shortBuffers.get(0); 
			shortBuffer.add(value);
			if(shortBuffer.size() == 2){
				float f = AudioUtil.read16(shortBuffer.get(0), 
						shortBuffer.get(1), 
						getFormat());
				reader.put(sample++, preemphasis(f));
				shortBuffer.clear();
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
		
	}
	//colleaction
	public void put(List<Byte> value){
		Float sum = 0F;
		switch (format.getSampleSizeInBits()) {
		case 8:
			
			for (Byte byte1 : value) {
				sum += AudioUtil.read8(byte1, getFormat());
			}
				reader.put(sample++, preemphasis(sum));
				break;
		case 16:
			Iterator<Byte> valIterator = value.iterator();
			Iterator<List<Byte>> buffIterator = shortBuffers.iterator();
			while(valIterator.hasNext()){
				Byte ival = valIterator.next();
				buffIterator.next().add(ival);
			}
			int size =  shortBuffers.get(0).size(); 
			if(shortBuffers.get(0).size() == 2){
				for (List<Byte> shortBuffer : shortBuffers) {
					if(shortBuffer.size()==2){
						sum += AudioUtil.read16(shortBuffer.get(0), 
							shortBuffer.get(1), 
							getFormat());
					}else{
						getFormat();
					}
						
					shortBuffer.clear();
				}
				reader.put(sample++, preemphasis(sum));
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(format.getSampleSizeInBits()
					+ " bits/sample not supported");
		}
		
	}
	
	/**
	 * Calculate post process data
	 * @param currentValue
	 * @return
	 */
	protected Float preemphasis(Float currentValue){
		setLastValue(currentValue);
		return preemphasisFilter.process(currentValue);
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

	public Float getLastValue() {
		return lastValue;
	}

	protected void setLastValue(Float lastValue) {
		this.lastValue = lastValue;
	}

	
}
