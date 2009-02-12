package org.spantus.segment.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class ByteListInputStream extends InputStream {
	
	List<Byte> buffer;
	Iterator<Byte> bufferIterator;
	
	public ByteListInputStream(List<Byte> buffer) {
		this.buffer = buffer;
		bufferIterator = buffer.iterator();
	}
	
	@Override
	public int read() throws IOException {
		return bufferIterator.hasNext() ? (bufferIterator.next() & 0xff) : -1;
	}

}
