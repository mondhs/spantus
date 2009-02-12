package org.spantus.exp.segment.test;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.impl.MakerComparisonTIAImpl;

public class MarkerComparisionTiaTest extends TestCase{
	MakerComparison makerComparison;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		makerComparison = new MakerComparisonTIAImpl();
	}
	
	public void testTiaComparision(){
		MarkerSet original = getOriginalMarkerSet();
		MarkerSet test = getTestMarkerSet();
		ComparisionResult result = makerComparison.compare(original, original);
		assertEquals(0f, result.getTotalResult());
		result = makerComparison.compare(original, test);
		assertEquals(0.27600533f, result.getTotalResult());
		
		
	}
	
	public MarkerSet getOriginalMarkerSet(){
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker(400, 530));
		ms.getMarkers().add(createMarker(1430, 630));
		return ms;
	}
	
	protected Marker createMarker(double start, double length ){
		Marker m = new Marker();
		m.setLabel(""+start);
		m.setStart(BigDecimal.valueOf(start));
		m.setLength(BigDecimal.valueOf(length));
		return m;
	}
	
	public MarkerSet getTestMarkerSet(){
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker(411, 509));
		ms.getMarkers().add(createMarker(1421, 309));
		ms.getMarkers().add(createMarker(1760, 279));
		return ms;
	}
}
