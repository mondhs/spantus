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

import junit.framework.TestCase;

import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.services.impl.MarkerTextGridDao;
/**
 * 
 * @author mondhs
 *
 */
public class MarkerTextGridDaoTest extends TestCase {
	MarkerTextGridDao markerDao;
	File inputFile;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		markerDao = new MarkerTextGridDao();
		inputFile = new File("../data/t_1_2.TextGrid");
	}
	/**
	 * test read functionality
	 * @throws Exception
	 */
	public void testRead() throws Exception {
		MarkerSetHolder  holder = markerDao.read(inputFile);
		assertNotNull(holder);
		MarkerSet markerSet = holder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		assertNotNull(markerSet);
		assertEquals(2,markerSet.getMarkers().size());

	}
}
