package org.spnt.servlet.dao.test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Track;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.spantus.server.dto.CorporaEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for test cases acting as samples for our Mongo API. They set up
 * two {@link Album}s and populate them with {@link Track}s.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public abstract class AbstractIntegrationTest {

//	private static final String COLLECTION = "corporaEntry";

	@Autowired
	MongoOperations operations;

	@Autowired
	MongoDbFactory mongoDbFactory;

	CorporaEntry firstEntry;
	CorporaEntry secondEntry;

	List<CorporaEntry> corporaEntries;

	@Before
	public void setUp() {
		operations.dropCollection(CorporaEntry.class);
		corporaEntries = new ArrayList<CorporaEntry>();
		firstEntry =  new CorporaEntry(1L, "first.wav");
		corporaEntries.add(firstEntry);
		secondEntry =  new CorporaEntry(2L, "second.wav");
		corporaEntries.add(secondEntry);	
	}


	/**
	 * Asserts the given query returns the first entry {@link CorporaEntry} and only
	 * that.
	 * 
	 * @param query
	 */
	protected void assertSingleFirstEntry(Query query) {

		List<CorporaEntry> result = operations.find(query, CorporaEntry.class);

		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(1));

		assertSingleFirstEntry(result.get(0));
	}

	/**
	 * Asserts the given {@link Album} is the Groo Grux album.
	 * 
	 * @param album
	 */
	protected void assertSingleFirstEntry(CorporaEntry album) {
		assertThat(album, is(notNullValue()));
		assertThat(album.getObjectId(), is(firstEntry.getObjectId()));
		assertThat(album.getFileName(), is("first.wav"));
	}

	/**
	 * Asserts the given query returns the Pursuit {@link Album} and only that
	 * one.
	 * 
	 * @param query
	 */
	protected void assertSingleSecondEntry(Query query) {
		List<CorporaEntry> result = operations.find(query, CorporaEntry.class);
		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(1));
		assertSingleSecondEntry(result.get(0));
	}

	/**
	 * 
	 * 
	 * @param album
	 */
	protected void assertSingleSecondEntry(CorporaEntry album) {
		assertThat(album, is(notNullValue()));
		assertThat(album.getObjectId(), is(secondEntry.getObjectId()));
		assertThat(album.getFileName(), is("second.wav"));
	}

	public MongoOperations getOperations() {
		return operations;
	}

//	public String getCollection() {
//		return COLLECTION;
//	}

}