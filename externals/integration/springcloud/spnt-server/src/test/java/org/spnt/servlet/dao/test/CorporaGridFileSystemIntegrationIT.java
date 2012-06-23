package org.spnt.servlet.dao.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class CorporaGridFileSystemIntegrationIT extends
		AbstractEmbededIntegrationTest{

	String fileName = CorporaGridFileSystemIntegrationIT.class.getName() + ".wav";


	@Test
	public void storeFile() throws IOException {
		GridFS fs = new GridFS(mongoDbFactory.getDb(), "audio");
		File autioFile = new File("../../../../data/t_1_2.wav");
		Assert.assertTrue("File exists", autioFile.exists());
		GridFSInputFile gfsSaveFile = fs.createFile(autioFile);
		gfsSaveFile.setFilename(fileName);
		gfsSaveFile.save();
		GridFS gfsReadFile = new GridFS(mongoDbFactory.getDb(), "audio");
		GridFSDBFile audioForOutput = gfsReadFile.findOne((ObjectId) gfsSaveFile.getId());
		Assert.assertNotNull("retrieved filed", audioForOutput);
		
	}

	@After
	@Override
	public void teadDown(){
		GridFS gfsReadFile = new GridFS(mongoDbFactory.getDb(), "audio");
		gfsReadFile.remove(fileName);
		super.teadDown();
	}
	

}
