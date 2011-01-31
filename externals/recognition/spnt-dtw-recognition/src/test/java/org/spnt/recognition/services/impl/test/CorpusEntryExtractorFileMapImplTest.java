package org.spnt.recognition.services.impl.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.work.services.MarkerDao;

public class CorpusEntryExtractorFileMapImplTest {

	private CorpusEntryExtractorTextGridMapImpl corpusEntryExtractor;
	private @Mock MarkerDao markerDao;
	
	@Before
	public void onSetup(){
		MockitoAnnotations.initMocks(this);
		corpusEntryExtractor = new CorpusEntryExtractorTextGridMapImpl();
		corpusEntryExtractor.setMarkerDao(markerDao);
	}
	
	@Test
	public void testCreateLabel(){
		//given
		Marker marker = createMarker("Test", 100, 50);
		File fileToRead = new File(".");
		
		MarkerSetHolder msh = new MarkerSetHolder();
		MarkerSet ms = new MarkerSet();
		ms.getMarkers().add(createMarker("a", 70, 20));
		ms.getMarkers().add(createMarker("b", 90, 20));
		ms.getMarkers().add(createMarker("c", 110, 20));
		ms.getMarkers().add(createMarker("d", 130, 21));
		ms.getMarkers().add(createMarker("e", 151, 20));
		
		msh.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), ms);
		BDDMockito.given(markerDao.read(Mockito.any(File.class))).willReturn(msh);
		//when
		String label = corpusEntryExtractor.createLabel(fileToRead, marker, 0);
		//then
		Assert.assertEquals("Label","bcd", label);
	}
	
	private Marker createMarker(String label, int start, int length){
		Marker marker = new Marker();
		marker.setStart((long) start);
		marker.setLength((long) length);
		marker.setLabel(label);
		return marker;
	}
}
