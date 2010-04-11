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

import java.util.LinkedList;

import org.spantus.core.extractor.IExtractorInputReader;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public class RecordWraperExtractorReader extends WraperExtractorReader{
	LinkedList<Byte> audioBuffer;
	long offset = 0;
	
//	private Logger log = Logger.getLogger(getClass());
	
	public RecordWraperExtractorReader(IExtractorInputReader reader) {
		super(reader, 1);
	}	

	protected Integer getSampleInBytes(){
		return getFormat().getSampleSizeInBits() >> 3;
	}
	
	public void put(byte value){
		super.put(value);
		getAudioBuffer().add(value);
		int i = getAudioBuffer().size() - (getReader().getConfig().getBufferSize()*100);
		while( i > 0 ){
			for (int j = 0; j < getSampleInBytes(); j++) {
				audioBuffer.poll();
			}
			i--;
			offset++;
		}
//		log.error("alue:" + value);
	}

	public LinkedList<Byte> getAudioBuffer() {
		if(audioBuffer == null){
			audioBuffer = new LinkedList<Byte>();
		}
		return audioBuffer;
	}

	public Long getOffset() {
		return offset;
	}

	
}
