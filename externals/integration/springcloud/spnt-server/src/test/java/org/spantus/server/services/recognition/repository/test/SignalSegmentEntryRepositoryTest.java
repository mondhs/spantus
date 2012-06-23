package org.spantus.server.services.recognition.repository.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.spantus.core.beans.SignalSegment;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.services.impl.test.SlowTests;
import org.spantus.server.services.recognition.repository.SignalSegmentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.Mongo;

import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SignalSegmentEntryRepositoryTest {

	@Autowired
	SignalSegmentEntryRepository repository;
	
	private MongodExecutable _mongodExe;
    private MongodProcess _mongod;

    private Mongo _mongo;
    private static final String DATABASENAME = "mongo_test";
	
	@Before
	public void onSetup() throws IOException{
		
		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
        _mongodExe = runtime.prepare(new MongodConfig(Version.V2_0, 27017,false));
        _mongod=_mongodExe.start();
        _mongo = new Mongo("localhost", 27017);
		
		for (int i = 0; i < 30; i++) {
			SignalSegmentEntry entry = new SignalSegmentEntry();
			entry.setSignalSegment(new SignalSegment());
			entry.setRecognizable(true);
			repository.save(entry);
		}
	}
	
	@After
	public void onTeardown(){
		repository.deleteAll();
		_mongod.stop();
        _mongodExe.cleanup();
	}

	@Test
	@Category(SlowTests.class)
	public void findByTarget() {
		Page<SignalSegmentEntry> entries = repository.findByRecognizable(true,
				new PageRequest(1, 3));
		assertThat(entries.getTotalElements(), is(30L));
		assertThat(entries.getNumber(), is(1));
	}

}
