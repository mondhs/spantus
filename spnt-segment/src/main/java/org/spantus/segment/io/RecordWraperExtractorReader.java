package org.spantus.segment.io;

import java.util.LinkedList;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.WraperExtractorReader;

public class RecordWraperExtractorReader extends WraperExtractorReader{
	LinkedList<Byte> audioBuffer;
	long offset = 0;
	
	public RecordWraperExtractorReader(IExtractorInputReader reader) {
		super(reader);
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
