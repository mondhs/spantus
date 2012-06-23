package org.spnt.servlet.dao.test;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.spantus.server.services.impl.test.SlowTests;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

public class CorporaEntryIntegrationTest extends AbstractEmbededIntegrationTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractIntegrationTests#setUp()
	 */
	@Before
	public void setUp() throws IOException {
		super.setUp();
		initializeData();
		// Stores both entries
		operations.insertAll(corporaEntries);
	}

	@Test
	@Category(SlowTests.class)
	public void lookupAlbumByIdWithQueryBuilder() throws Exception {
		// given
		// when
		Query build = new Query(where("_id").is(firstEntry.getObjectId()));
		// then
		assertSingleFirstEntry(build);
	}

	@Test
	@Category(SlowTests.class)
	public void lookupAlbumByIdUsingJson() throws Exception {
		// given
		// when
		Query query = parseQuery("{'_id' : { '$oid' : '%s' }}",
				firstEntry.getObjectId());
		// then
		assertSingleFirstEntry(query);
	}

	@Test
	@Category(SlowTests.class)
	public void lookupAlbumsByTrackNameUsingJson() throws Exception {
		// given
		// when
		Query query = parseQuery("{'fileName' : 'first.wav'}");
		// then
		assertSingleFirstEntry(query);
	}

	@Test
	@Category(SlowTests.class)
	public void lookupAlbumByTrackNameUsingQueryBuilder() {
		// given
		// when
		Query spec = new Query(where("fileName").is("first.wav"));
		// then
		assertSingleFirstEntry(spec);
	}

	private Query parseQuery(String query, Object... arguments) {
		return new BasicQuery(String.format(query, arguments));
	}

}
