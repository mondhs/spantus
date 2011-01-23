package org.spantus.exp.segment.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.impl.MakerComparisonTIAImpl;

public class MarkerComparisionTiaTest{
	MakerComparison makerComparison;
	
	@Before
	public void setUp() throws Exception {
		makerComparison = new MakerComparisonTIAImpl();
	}
        @Test
	public void testTiaComparision(){
		MarkerSetHolder original = getOriginalMarkerSet();
		MarkerSetHolder test = getTestMarkerSet();
		ComparisionResult result = makerComparison.compare(original, original);
		Assert.assertEquals(0D, result.getTotalResult(),0D);
		result = makerComparison.compare(original, test);
		Assert.assertEquals(0.1D, result.getTotalResult(), 0.1D);
		
		
	}
	
	public MarkerSetHolder getOriginalMarkerSet(){
		MarkerSetHolder holder = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker(400, 530));
		ms.getMarkers().add(createMarker(1430, 630));
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), ms);
		return holder;
	}
	
	protected Marker createMarker(long start, long length ){
		Marker m = new Marker();
		m.setLabel(""+start);
		m.setStart(start);
		m.setLength(length);
		return m;
	}
	
	public MarkerSetHolder getTestMarkerSet(){
		MarkerSetHolder holder = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker(411, 509));
		ms.getMarkers().add(createMarker(1421, 309));
		ms.getMarkers().add(createMarker(1760, 279));
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), ms);
		return holder;

	}
}
