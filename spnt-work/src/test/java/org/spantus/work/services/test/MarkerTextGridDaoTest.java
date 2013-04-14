/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.services.test;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.services.impl.MarkerTextGridDao;
/**
 * 
 * @author mondhs
 *
 */

public class MarkerTextGridDaoTest  {
	MarkerTextGridDao markerDao;
	File inputFile;
	@Before
	public void setUp() throws Exception {
		markerDao = new MarkerTextGridDao();
		inputFile = new File("../data/t_1_2.TextGrid");
	}
	/**
	 * test read functionality
	 * @throws Exception
	 */
	@Test
	public void testRead() throws Exception {
		MarkerSetHolder  holder = markerDao.read(inputFile);
		Assert.assertNotNull(holder);
		MarkerSet markerSet = holder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		Assert.assertNotNull(markerSet);
		Assert.assertEquals(2,markerSet.getMarkers().size());
	}
	@Test
	public void testWrite() throws Exception {
		//given
		MarkerSetHolder holder = new  MarkerSetHolder();
		MarkerSet wordMarkerSet = new MarkerSet();
		wordMarkerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());
		holder.getMarkerSets().put(wordMarkerSet.getMarkerSetType(), wordMarkerSet);
		MarkerSet phoneMarkerSet = new MarkerSet();
		phoneMarkerSet.setMarkerSetType(MarkerSetHolderEnum.phone.name());
		holder.getMarkerSets().put(phoneMarkerSet.getMarkerSetType(), phoneMarkerSet);
		wordMarkerSet.getMarkers().add(new Marker(100L, 50L,"Vienas"));
		wordMarkerSet.getMarkers().add(new Marker(200L, 50L,"Du"));
		phoneMarkerSet.getMarkers().add(new Marker(100L, 25L,"Vie"));
		phoneMarkerSet.getMarkers().add(new Marker(125L, 25L,"nas"));
		phoneMarkerSet.getMarkers().add(new Marker(200L, 50L,"Du"));
		//when
		markerDao.write(holder, new File("./target/test.TextGrid"));
		//then
		//should be define assert logic
	}
}
