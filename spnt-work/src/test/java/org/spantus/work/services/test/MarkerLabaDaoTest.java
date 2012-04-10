package org.spantus.work.services.test;

import java.io.File;

import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.services.impl.MarkerLabaDao;

import junit.framework.TestCase;

public class MarkerLabaDaoTest extends TestCase {
	MarkerDao markerDao;
	File inputFile;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		markerDao = new MarkerLabaDao();
		inputFile = new File("../data/t_1_2.laba");
	}
	public void testRead() throws Exception {
		MarkerSetHolder  holder = markerDao.read(inputFile);
		assertNotNull(holder);
		MarkerSet markerSet = holder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		assertNotNull(markerSet);
		assertEquals(2,markerSet.getMarkers().size());

	}
}
