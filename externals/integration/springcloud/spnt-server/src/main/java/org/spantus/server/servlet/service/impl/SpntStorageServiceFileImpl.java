package org.spantus.server.servlet.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.spantus.logger.Logger;
import org.spantus.server.dto.CorporaEntry;
import org.spantus.server.dto.CorporaEntryList;
import org.spantus.server.servlet.service.SpntEchoRepository;
import org.spantus.utils.FileUtils;

import com.mongodb.gridfs.GridFSDBFile;

public class SpntStorageServiceFileImpl implements SpntEchoRepository {
	private static Logger LOG = Logger.getLogger(SpntStorageServiceFileImpl.class);
	private File storageDir;

	/* (non-Javadoc)
	 * @see org.spnt.servlet.service.SpntStorageService#findAll()
	 */
	public CorporaEntryList findAll(){
		CorporaEntryList returnList = new CorporaEntryList();
		List<String> list = FileUtils.findAllMatchFullPath(getStorageDir()
				.getAbsolutePath(), "wav");
		for (String fileName : list) {
			CorporaEntry entry = new CorporaEntry();
			File file = new File(fileName);
			entry.setTimeStamp(file.lastModified());
			entry.setFileName(fileName);
			returnList.getCorporaEntry().add(entry);
		}
		return returnList;
	}

	/* (non-Javadoc)
	 * @see org.spnt.servlet.service.SpntStorageService#findLasEventAudio(java.lang.Long)
	 */
	public InputStream findLastEventAudio(Long lastEvent)
			throws FileNotFoundException {
		File file = FileUtils.findNewestMatchFullPath(getStorageDir()
				.getAbsolutePath(), "wav");
		return new FileInputStream(file);
	}

	/* (non-Javadoc)
	 * @see org.spnt.servlet.service.SpntStorageService#findLastEvent()
	 */
	public Long findLastEvent() {
		File file = FileUtils.findNewestMatchFullPath(getStorageDir()
				.getAbsolutePath(), "wav");
		if (file != null && file.exists()) {
			return file.lastModified();
		}
		return 0l;
	}

	public File getStorageDir() {
		if (this.storageDir == null) {
			File rootDir = new File(System.getProperty("java.io.tmpdir"));
			String baseName = "spnt-storage";
			this.storageDir = new File(rootDir, baseName);
			if (this.storageDir.mkdir()) {
				LOG.debug("[getStorageDir] Created storage dir {0}",
						this.storageDir);
			}
		}
		return this.storageDir;
	}

	/* (non-Javadoc)
	 * @see org.spnt.servlet.service.SpntStorageService#store(javax.sound.sampled.AudioInputStream)
	 */
	public CorporaEntry store(InputStream audioIn) {
		File file = new File(getStorageDir(), "out.wav");
//		AudioManagerFactory.createAudioManager().save(audioIn,
//				file.getAbsolutePath());
		return new CorporaEntry();
	}

	public void setStorageDir(File storageDir) {
		this.storageDir = storageDir;
	}

	@Override
	public CorporaEntry findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridFSDBFile findOutputById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CorporaEntry store(AudioInputStream audioIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CorporaEntry store(InputStream inputStream, AudioFormat audioFormat,
			AudioFormat requiredAudioFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CorporaEntry delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CorporaEntry update(String id, String description) {
		// TODO Auto-generated method stub
		return null;
	}

}
