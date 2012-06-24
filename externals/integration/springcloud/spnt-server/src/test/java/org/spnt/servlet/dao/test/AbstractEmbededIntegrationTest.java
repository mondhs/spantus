package org.spnt.servlet.dao.test;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;

public abstract class AbstractEmbededIntegrationTest extends AbstractIntegrationTest {

	private static MongodExecutable _mongodExe;
    private static MongodProcess _mongod;

	
	@BeforeClass
	public static void setUpClass() throws IOException {
		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
        _mongodExe = runtime.prepare(new MongodConfig(Version.V2_0, 27017,false));
        _mongod=_mongodExe.start();
	}
	
	@AfterClass
	public static void teadDownClass() {
		_mongod.stop();
        _mongodExe.cleanup();
	}
	
}
