package org.spantus.core.marker.service.test;

import org.junit.Assert;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.service.MarkerServiceImp;

public class MarkerServiceImpTest {

	MarkerServiceImp markerServiceImp;
	
	@Before
	public void onSetup() {
		markerServiceImp = new MarkerServiceImp();
	}
	
	@Test
	public void test_findAllByPhrase() {
		//given
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet phoneSet = new MarkerSet();
		String label1 = "c";
		String label2 = "d";
		markerSetHolder.getMarkerSets().put("phone", phoneSet);
		Long start = 0L;
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "a"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "b"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "c"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "d"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "e"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "a"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "b"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "c"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "d"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "e"));		
		//when
		Collection<Marker> result = markerServiceImp.findAllByPhrase(markerSetHolder, label1, label2);
		//then
		Assert.assertNotNull("Result", result);
		Assert.assertEquals("Result",2, result.size(),0);
	}
	
	@Test
	public void test_findAllByPhraseLietuvos() {
		//given
		MarkerSetHolder markerSetHolder = new MarkerSetHolder();
		MarkerSet phoneSet = new MarkerSet();
		String label1 = "c";
		String label2 = "d";
		markerSetHolder.getMarkerSets().put("phone", phoneSet);
		Long start = 0L;
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "l'"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "ai"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "l'"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "ie"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "t"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "u"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "v"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "o:"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "s"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "g'"));
		phoneSet.getMarkers().add(new Marker(start+=10, 10L, "i:"));		
		//when
		Collection<Marker> result = markerServiceImp.findAllByPhrase(markerSetHolder, "l'", "ie", "t", "u", "v", "o:", "s");
		//then
		Assert.assertNotNull("Result", result);
		Assert.assertEquals("Result",1, result.size(),0);
		Assert.assertEquals("Result","l'ietuvo:s", result.iterator().next().getLabel());
		
	}
	

}
