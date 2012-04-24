package org.spnt.servlet.dao.test;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

public class CorporaEntryIntegrationTest extends AbstractIntegrationTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractIntegrationTests#setUp()
	 */
	@Before
	public void setUp() {
		super.setUp();

		// Stores both entries
		operations.insertAll(corporaEntries);
	}

	@Test
	public void lookupAlbumByIdWithQueryBuilder() throws Exception {
		Query build = new Query(where("_id").is(firstEntry.getObjectId()));
		assertSingleFirstEntry(build);
	}

	@Test
	public void lookupAlbumByIdUsingJson() throws Exception {

		Query query = parseQuery("{'_id' : { '$oid' : '%s' }}",
				firstEntry.getObjectId());
		assertSingleFirstEntry(query);
	}

	@Test
	public void lookupAlbumsByTrackNameUsingJson() throws Exception {
		Query query = parseQuery("{'fileName' : 'first.wav'}");
		assertSingleFirstEntry(query);
	}

	@Test
	public void lookupAlbumByTrackNameUsingQueryBuilder() {
		Query spec = new Query(where("fileName").is("first.wav"));
		assertSingleFirstEntry(spec);
	}

	private Query parseQuery(String query, Object... arguments) {
		return new BasicQuery(String.format(query, arguments));
	}

}
