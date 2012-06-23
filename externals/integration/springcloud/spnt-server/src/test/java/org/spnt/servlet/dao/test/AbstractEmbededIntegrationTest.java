package org.spnt.servlet.dao.test;

import java.io.IOException;

import org.junit.Before;

import com.mongodb.Mongo;

import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;

public abstract class AbstractEmbededIntegrationTest extends AbstractIntegrationTest {

	private MongodExecutable _mongodExe;
    private MongodProcess _mongod;

    private Mongo _mongo;
    private static final String DATABASENAME = "mongo_test";
	
	@Before
	@Override
	public void setUp() throws IOException {
		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
        _mongodExe = runtime.prepare(new MongodConfig(Version.V2_0, 27017,false));
        _mongod=_mongodExe.start();
        _mongo = new Mongo("localhost", 27017);
	}
	
	@Override
	public void teadDown() {
		super.teadDown();
		_mongod.stop();
        _mongodExe.cleanup();
	}
	
}
