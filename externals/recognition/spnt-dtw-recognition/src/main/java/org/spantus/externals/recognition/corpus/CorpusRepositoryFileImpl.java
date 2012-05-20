package org.spantus.externals.recognition.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.IValues;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusRepository;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.externals.recognition.services.RecognitionServiceFactory;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.converter.FrameValues3DConverter;
import org.spantus.work.services.converter.FrameValuesConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class CorpusRepositoryFileImpl implements CorpusRepository {

	private XStream xstream;

	private static Logger log = Logger
			.getLogger(CorpusRepositoryFileImpl.class);

	private File repoDir = null;

	public final static String CORPUS_ENTRY_FILE_EXT = ".cspnt.xml";
	public final static String WAV_FILE_EXT = ".wav";

	public static final String DEFAULT_REPO_PATH = "./target/corpus";

	Map<String, CorpusFileEntry> repository;

	@Override
	public Long count() {
		return (long) findAllEntries().size();
	}
	
	public Collection<SignalSegment> findAllEntries() {
		return getCastedRepository();
	}

	// public List<CorpusFileEntry> findAllFileEntries() {
	// List<CorpusFileEntry> repoFiles = new ArrayList<CorpusFileEntry>();
	// if(getRepoDir().isDirectory()){
	// for (String fileName : getRepoDir().list()) {
	// if(fileName.endsWith(CORPUS_ENTRY_FILE_EXT)){
	// repoFiles.add(readFileEntry(new File(repoDir,fileName)));
	// }
	// }
	// }
	// return repoFiles;
	// }
	/**
	 * Save and reset repo
	 * 
	 * @param entry
	 */
	public SignalSegment save(SignalSegment entry) {
		CorpusFileEntry fileEntry = null;
		if (!(entry instanceof CorpusFileEntry)) {
			if (entry.getId() == null) {
				fileEntry = new CorpusFileEntry();
				fileEntry.setName(entry.getName());
				fileEntry.putAll(entry);
			} else {
				throw new ProcessingException("Not implemented");
			}
		} else {
			fileEntry = (CorpusFileEntry) entry;
		}
		CorpusFileEntry file = saveOrUpdateFile((CorpusFileEntry) fileEntry);
		return file;
	}

	/**
	 * Delete entry from repository
	 * 
	 * @param entry
	 * @return
	 */
	public SignalSegment delete(String id) {
		Assert.isTrue(id != null);
		CorpusFileEntry fileEntry = getRepository().get(id);
		Assert.isTrue(fileEntry != null);
		if (fileEntry.getEntryFile().exists()) {
			boolean deleted = fileEntry.getEntryFile().delete();
			if (fileEntry.getWavFile() != null) {
				fileEntry.getWavFile().delete();
			}
			if (deleted) {
				getRepository().remove(id);
			} else {
				throw new ProcessingException("Cannot delete");
			}
		}
		log.debug("[delete] {0} }", fileEntry);

		return fileEntry;
	}

	/**
	 * Save or update entry in repo
	 * 
	 * @param entry
	 * @return
	 */
	public CorpusFileEntry saveOrUpdateFile(CorpusFileEntry entry) {
		if (entry.getId() == null) {
			entry.setId(Long.valueOf(System.currentTimeMillis()).toString());
		}
		if (entry.getEntryFile() == null) {
			File file = new File(getRepoDir(), entry.getName() + "-"
					+ entry.getId() + CORPUS_ENTRY_FILE_EXT);
			entry.setEntryFile(file.getAbsoluteFile());
		}
		try {
			FileWriter outputFile = new FileWriter(entry.getEntryFile(), false);
			getXsteam().toXML(entry, outputFile);
			log.debug("[saveOrUpdateFile] {0} saved to {1}", entry, outputFile);
		} catch (NoSuchElementException e) {
			throw new ProcessingException(
					"outputFile: " + entry.getEntryFile(), e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		if (getRepository().get(entry.getId()) != null) {
			if (!getRepository().get(entry.getId()).equals(entry)) {
				// replace
				getRepository().put(entry.getId(), entry);
			}
		} else {
			// new
			getRepository().put(entry.getId(), entry);
		}
		return entry;
	}

	/**
	 * 
	 * @param entry
	 */
        @Override
	public SignalSegment update(SignalSegment entry) {
		CorpusFileEntry fileEntry = (CorpusFileEntry) entry;
		fileEntry = saveOrUpdateFile(fileEntry);
		return fileEntry;
	}

	/**
	 * 
	 * @param corpusEntry
	 * @param audioFile
	 * @return
	 */
	public SignalSegment update(SignalSegment entry, AudioInputStream ais) {

		CorpusFileEntry fileEntry = (CorpusFileEntry) entry;
		if (fileEntry.getId() == null) {
			fileEntry
					.setId(Long.valueOf(System.currentTimeMillis()).toString());
		}

		File wavFile = new File(getRepoDir(), entry.getName() + "-"
				+ fileEntry.getId() + WAV_FILE_EXT);
		wavFile = wavFile.getAbsoluteFile();
		try {
			if (ais != null) {
				AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
				log.debug("[update] saved to " + wavFile.getAbsolutePath());
				fileEntry.setWavFile(wavFile);
			} else {
				log.debug("[update] not saved to as it is not audio");
			}
		} catch (IOException ex) {
			throw new ProcessingException(ex);
		}
		fileEntry = saveOrUpdateFile(fileEntry);

		return fileEntry;
	}

	/**
	 * 
	 * @param label
	 * @param featureDataMap
	 * @return
	 */
	public SignalSegment create(String label,
			Map<String, IValues> featureDataMap) {
		return RecognitionServiceFactory.createSignalSegment(label,
				featureDataMap);
	}

	public String findAudioFileById(String id) {
		if (getRepository().get(id) == null
				|| getRepository().get(id).getWavFile() == null) {
			return null;
		}
		File file = getRepository().get(id).getWavFile();
		// if does not exist were it says, check repo as well.
		if (!file.exists()) {
			file = new File(getRepoDir(), file.getName());
		}
		if (!file.exists()) {
			return null;
		}

		return file.getAbsolutePath();
	}

	protected CorpusFileEntry read(File entryFile) {
		CorpusFileEntry entry = null;
		try {
			FileReader inFile = new FileReader(entryFile);
			entry = (CorpusFileEntry) getXsteam().fromXML(inFile);
		} catch (ConversionException e) {
			throw new ProcessingException(
					"Error processing file: " + entryFile, e);
		} catch (FileNotFoundException e) {
			throw new ProcessingException("Erro processing file: " + entryFile,
					e);
		}
		return entry;

	}

	protected CorpusFileEntry readFileEntry(File entryFile) {
		CorpusFileEntry fileEntry = null;
		try {
			FileReader inFile = new FileReader(entryFile);
			fileEntry = (CorpusFileEntry) getXsteam().fromXML(inFile);
			fileEntry.setEntryFile(entryFile);
			File wavFile = new File(entryFile.getParent(),
					FileUtils.stripExtention(FileUtils
							.stripExtention(entryFile)) + ".wav");
			fileEntry.setWavFile(wavFile);
		} catch (FileNotFoundException e) {
			throw new ProcessingException(e);
		}
		return fileEntry;

	}

	protected XStream getXsteam() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.registerConverter(new EnumConverter());
			xstream.registerConverter(new FrameValuesConverter());
			xstream.registerConverter(new FrameValues3DConverter());
		}
		return xstream;
	}

	public File getRepoDir() {
		if (repoDir == null) {
			repoDir = FileUtils.checkDirs(DEFAULT_REPO_PATH);
		}
		return repoDir;
	}

	public void setRepositoryPath(String repositoryPath) {
		repoDir = FileUtils.checkDirs(repositoryPath).getAbsoluteFile();
	}

	public Map<String, CorpusFileEntry> getRepository() {
		if (repository == null) {
			log.debug("[getRepository] init repo from {0}", getRepoDir());
			repository = new HashMap<String, CorpusFileEntry>();
			if (getRepoDir().isDirectory()) {
				for (String fileName : getRepoDir().list()) {
					if (fileName.endsWith(CORPUS_ENTRY_FILE_EXT)) {
						CorpusFileEntry entry = read(new File(repoDir, fileName));
						repository.put(entry.getId(), entry);
					}
				}
			}
			if (repository.size() > 3000) {
				throw new ProcessingException(
						"Big repositories support not implemented");
			}
		}
		return repository;
	}

	public void flush() {
		this.repository = null;
	}

	public void setRepository(Map<String, CorpusFileEntry> repository) {
		this.repository = repository;
	}

	public Set<SignalSegment> getCastedRepository() {
		return new HashSet<SignalSegment>(getRepository().values());
	}

}
