package org.spnt.recognition.services.impl.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;

public class CorpusEntryExtractorTextGridMapImplTest {

	private CorpusEntryExtractorTextGridMapImpl corpusEntryExtractor;
	private @Mock MarkerDao markerDao;
	
	@Before
	public void onSetup(){
		MockitoAnnotations.initMocks(this);
		corpusEntryExtractor = new CorpusEntryExtractorTextGridMapImpl();
		corpusEntryExtractor.setMarkerDao(markerDao);
	}
	/**
	 * ....iii....<br>
	 * ...xxxxx...
	 */
	@Test
	public void shouldExtractInnerMatcher(){
		//given
		Marker marker = createMarker("Test", 100, 150);
		File fileToRead = new File(".");
		
		MarkerSetHolder msh = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker("a", 60, 105));
		ms.getMarkers().add(createMarker("b", 105, 145));
		ms.getMarkers().add(createMarker("c", 145, 185));
		
		msh.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ms);
		BDDMockito.given(markerDao.read(Mockito.any(File.class))).willReturn(msh);
		//when
		String label = corpusEntryExtractor.createLabel(fileToRead, marker, 0);
		//then
		Assert.assertEquals("Label","b", label);
	}
	/**
	 * .......iii.<br>
	 * .....xxx...
	 */
	@Test
	public void shouldExtractTooMuchOverlap(){
		//given
		Marker marker = createMarker("Test", 120, 310);
		File fileToRead = new File(".");
		
		MarkerSetHolder msh = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker("a", 90, 278));
		ms.getMarkers().add(createMarker("b", 278, 410));
		ms.getMarkers().add(createMarker("c", 410, 450));
		
		msh.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ms);
		BDDMockito.given(markerDao.read(Mockito.any(File.class))).willReturn(msh);
		//when
		String label = corpusEntryExtractor.createLabel(fileToRead, marker, 0);
		//then
		Assert.assertEquals("Label","a", label);
	}
	
	@Test
	public void shouldExtractManyLabels(){
		//given
		Marker marker = createMarker("Test", 100,150);
		File fileToRead = new File(".");
		
		MarkerSetHolder msh = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker("a", 60, 115));
		ms.getMarkers().add(createMarker("b", 115, 135));
		ms.getMarkers().add(createMarker("c", 135, 175));
		
		msh.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ms);
		BDDMockito.given(markerDao.read(Mockito.any(File.class))).willReturn(msh);
		//when
		String label = corpusEntryExtractor.createLabel(fileToRead, marker, 0);
		//then
		Assert.assertEquals("Label","a+b+c", label);
	}
	/**
	 * ..iiii..<br>
	 * ...xx...
	 */
	@Test
	public void shouldExtractSmallerLabels(){
		//given
		Marker marker = createMarker("Test", 276, 495);
		File fileToRead = new File(".");
		
		MarkerSetHolder msh = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker("a", 60, 200));
		ms.getMarkers().add(createMarker("b", 200, 502));
		ms.getMarkers().add(createMarker("c", 502, 550));
		
		msh.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ms);
		BDDMockito.given(markerDao.read(Mockito.any(File.class))).willReturn(msh);
		//when
		String label = corpusEntryExtractor.createLabel(fileToRead, marker, 0);
		//then
		Assert.assertEquals("Label","b", label);
	}
	
	@Test
	public void shouldExtractClosestLabelOnly(){
		//given
		Marker marker = createMarker("Test", 100, 150);
		File fileToRead = new File(".");
		
		MarkerSetHolder msh = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker("a", 50, 105));
		ms.getMarkers().add(createMarker("b", 105, 115));
		ms.getMarkers().add(createMarker("c", 115, 125));
		ms.getMarkers().add(createMarker("d", 125, 140));
		ms.getMarkers().add(createMarker("e", 145, 160));
		
		msh.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ms);
		BDDMockito.given(markerDao.read(Mockito.any(File.class))).willReturn(msh);
		//when
		String label = corpusEntryExtractor.createLabel(fileToRead, marker, 0);
		//then
		Assert.assertEquals("Label","b+c+d", label);
	}
	
	private Marker createMarker(String label, int start, int end){
		Marker marker = new Marker();
		marker.setStart((long) start);
		marker.setEnd((long) end);
		marker.setLabel(label);
		return marker;
	}
}
