package org.spantus.exp.segment.test;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.impl.MakerComparisonImpl;

public class MakerComparisionTest extends TestCase {

	MakerComparison makerComparison;
	
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		makerComparison = new MakerComparisonImpl();
	}
	
	public void testDefaultComparision(){
		MarkerSet original = getOriginalMarkerSet();
		MarkerSet test = getTestMarkerSet();
		ComparisionResult result = makerComparison.compare(original, test);
		assertEquals(0f, result.getTotalResult());
	}
	
	public MarkerSet getOriginalMarkerSet(){
		MarkerSet ms = new MarkerSet();
		Marker m = new Marker();
		m.setLabel("0");
		m.setStart(BigDecimal.valueOf(100));
		m.setLength(BigDecimal.valueOf(50));
		ms.getMarkers().add(m);
		return ms;
	}
	
	public MarkerSet getTestMarkerSet(){
		MarkerSet ms = new MarkerSet();
		Marker m = new Marker();
		m.setLabel("0");
		m.setStart(BigDecimal.valueOf(100));
		m.setLength(BigDecimal.valueOf(50));
		ms.getMarkers().add(m);
		return ms;
	}

}
