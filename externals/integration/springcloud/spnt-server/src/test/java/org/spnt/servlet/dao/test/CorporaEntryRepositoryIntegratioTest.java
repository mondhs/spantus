package org.spnt.servlet.dao.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spantus.server.servlet.repository.CorporaEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
@Ignore
@ContextConfiguration
public class CorporaEntryRepositoryIntegratioTest extends AbstractIntegrationTest {
	
	@Autowired
	CorporaEntryRepository repository;

	@Before
	public void purgeRepository() {
		repository.deleteAll();
		super.setUp();
	}

	@Test
	public void createEntries() throws Exception {
		repository.save(corporaEntries);
		assertSingleFirstEntry(repository.findOne(firstEntry.getObjectId()));
	}

//	@Test 
//	public void findsAlbumByConcreteTrackName() throws Exception {
//		signalSegmentEntryRepository.save(albums);
//		assertSingleGruxAlbum(signalSegmentEntryRepository.findByFileName("Grux"));
//	}



}
