package org.spantus.extr.wordspot.guava;

import org.spantus.core.marker.Marker;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public class MarkerOrder extends Ordering<Marker> {
	@Override
	public int compare(Marker left,
			Marker right) {
		return Longs.compare(left.getStart(), right.getStart());
	}
}
