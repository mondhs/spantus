package org.spantus.exp.segment.services.impl;

import java.util.Iterator;
import java.util.Set;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class CombinationIterable implements Iterable<Set<String>>{

	public Iterator<Set<String>> iterator() {
		return new CombinationFileIterator();
	}

}
