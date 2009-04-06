package org.spantus.segment.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ByteListInputStream extends InputStream {
	
//	private final List<Byte> buffer;
	private Iterator<Byte> bufferIterator;
	
	public ByteListInputStream(List<Byte> buffer) {
		List<Byte> _buffer = new LinkedList<Byte>(buffer);
		bufferIterator = _buffer.iterator();
	}
	
	@Override
	public int read() throws IOException {
		return bufferIterator.hasNext() ? (bufferIterator.next() & 0xff) : -1;
	}

}
