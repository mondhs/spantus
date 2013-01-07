package org.spantus.extr.wordspot.service.scrolling.impl.test;

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;

import com.google.common.base.Predicate;

public class MatchedPredicate implements Predicate<SignalSegment>{
	
	
	private Marker marker;
	
	public MatchedPredicate(Marker marker) {
		this.marker = marker; 
	}

	public MatchedPredicate(SignalSegment cKeySegment) {
		this.marker = cKeySegment.getMarker(); 
	}

	@Override
	public boolean apply(SignalSegment segment) {
		 long delta = Math.abs(marker.getStart() - segment.getMarker().getStart());
		 return delta<250;
	}

}
