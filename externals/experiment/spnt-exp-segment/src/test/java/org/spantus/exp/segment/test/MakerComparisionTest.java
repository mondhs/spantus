package org.spantus.exp.segment.test;

import junit.framework.TestCase;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
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
		MarkerSetHolder original = getOriginalMarkerSet();
		MarkerSetHolder test = getTestMarkerSet();
		ComparisionResult result = makerComparison.compare(original, test);
		assertEquals(0f, result.getTotalResult());
	}
	
	public MarkerSetHolder getOriginalMarkerSet(){
		MarkerSetHolder holder = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		Marker m = new Marker();
		m.setLabel("0");
		m.setStart(100L);
		m.setLength(50L);
		ms.getMarkers().add(m);
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), ms);
		return holder;
	}
	
	public MarkerSetHolder getTestMarkerSet(){
		MarkerSetHolder holder = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		Marker m = new Marker();
		m.setLabel("0");
		m.setStart(100L);
		m.setLength(50L);
		ms.getMarkers().add(m);
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), ms);
		return holder;
	}

}
