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
