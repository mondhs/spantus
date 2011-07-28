/**
 * 
 */
package org.spantus.exp.recognition.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mgreibus
 *
 */
public class QSegmentExpHsqlDaoTest {

	private QSegmentExpHsqlDao qSegmentExpDaoImpl;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		qSegmentExpDaoImpl = new QSegmentExpHsqlDao();
		qSegmentExpDaoImpl.init();
	}

	/**
	 * Test method for {@link org.spantus.exp.recognition.dao.QSegmentExpHsqlDao#findMatches()}.
	 */
	@Test
	public void testFindMatches() {
		qSegmentExpDaoImpl.findMatches("TRI4_AK1");
	}
	
	@After
	public void teardown() throws Exception {
		qSegmentExpDaoImpl.destroy();
	}

	
}
