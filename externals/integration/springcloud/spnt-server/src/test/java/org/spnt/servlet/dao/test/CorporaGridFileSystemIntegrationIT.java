package org.spnt.servlet.dao.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
@Ignore
public class CorporaGridFileSystemIntegrationTest extends
		AbstractIntegrationTest {

	String fileName = CorporaGridFileSystemIntegrationTest.class.getName() + ".wav";
	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractIntegrationTests#setUp()
	 */
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void storeFile() throws IOException {
		GridFS fs = new GridFS(mongoDbFactory.getDb(), "audio");
		File autioFile = new File("../../trunk/data/t_1_2.wav");
		GridFSInputFile gfsSaveFile = fs.createFile(autioFile);
		gfsSaveFile.setFilename(fileName);
		gfsSaveFile.save();
		GridFS gfsReadFile = new GridFS(mongoDbFactory.getDb(), "audio");
		GridFSDBFile audioForOutput = gfsReadFile.findOne(autioFile.getName());
		Assert.assertNotNull(audioForOutput);
		
	}

	@After
	public void teadDown(){
		GridFS gfsReadFile = new GridFS(mongoDbFactory.getDb(), "audio");
		gfsReadFile.remove(fileName);
	}
	

}
