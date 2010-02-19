package org.spantus.externals.recognition.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.converter.FrameValues3DConverter;
import org.spantus.work.services.converter.FrameValuesConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class CorpusRepositoryFileImpl implements CorpusRepository {

	private XStream xstream;
	
	private File repoDir = null;
	
	public final static String CORPUS_ENTRY_FILE_EXT =".cspnt.xml";
	
	public static final String DEFAULT_REPO_PATH = "./target/corpus";
	
	List<CorpusEntry> repo;
	
	public List<CorpusEntry> findAllEntries() {
		if(repo == null){
			repo = new ArrayList<CorpusEntry>();
			if(getRepoDir().isDirectory()){
				for (String fileName : repoDir.list()) {
					if(fileName.endsWith(CORPUS_ENTRY_FILE_EXT)){
						repo.add(read(new File(repoDir,fileName)));
					}
				}
			}
		}
		return repo;
	}
	public List<CorpusFileEntry> findAllFileEntries() {
			List<CorpusFileEntry> repoFiles = new ArrayList<CorpusFileEntry>();
			if(getRepoDir().isDirectory()){
				for (String fileName : getRepoDir().list()) {
					if(fileName.endsWith(CORPUS_ENTRY_FILE_EXT)){
						repoFiles.add(readFileEntry(new File(repoDir,fileName)));
					}
				}
			}
		return repoFiles;
	}

	public void save(CorpusEntry entry) {
		saveFile(entry);
	}
	
	public File saveFile(CorpusEntry entry) {
		if(entry.getId() == null){
			entry.setId(System.currentTimeMillis());
		}
		File file = new File(getRepoDir(),entry.getName() + CORPUS_ENTRY_FILE_EXT);
		if(file.exists()){
			file = new File(getRepoDir(),
					entry.getName() + "-" + System.currentTimeMillis() + CORPUS_ENTRY_FILE_EXT);
		}
		
		try {
			FileWriter outputFile = new FileWriter(file,false);	
			getXsteam().toXML(entry, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	/**
	 * 
	 * @param entry
	 */
	public void update(CorpusFileEntry entry){
		File file = null;
		if(entry.getEntryFile().exists()){
			entry.getEntryFile().delete();
			file = saveFile(entry.getCorpusEntry());
		}
		if(entry.getWavFile().exists()){
			String wavName = FileUtils.getOnlyFileName(entry.getWavFile());
			String entryName = FileUtils.getOnlyFileName(
					FileUtils.getOnlyFileName(file.getName())); 
			if(!wavName.equals(entryName)){
				File dest = new File(entry.getWavFile().getParent(),
						entryName
						+".wav");
				entry.getWavFile().renameTo(dest);		
			}
			
		}
	}
	
	protected CorpusEntry read(File entryFile) {
		CorpusEntry entry = null;
		try {
			FileReader inFile = new FileReader(entryFile);
			entry = (CorpusEntry)getXsteam().fromXML(inFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return entry;
		
	}
	protected CorpusFileEntry readFileEntry(File entryFile) {
		CorpusFileEntry fileEntry = null;
		try {
			FileReader inFile = new FileReader(entryFile);
			CorpusEntry corpusEntry = (CorpusEntry)getXsteam().fromXML(inFile);
			fileEntry = new CorpusFileEntry();
			fileEntry.setCorpusEntry(corpusEntry);
			fileEntry.setEntryFile(entryFile);
			File wavFile = new File(entryFile.getParent(),
					FileUtils.getOnlyFileName(
							FileUtils.getOnlyFileName(entryFile))+".wav");
			fileEntry.setWavFile(wavFile);
		} catch (FileNotFoundException e) {
			throw new ProcessingException(e);
		}
		return fileEntry;
		
	}
	
	protected XStream getXsteam(){
		if(xstream == null){
			xstream = new XStream();
			xstream.registerConverter(new EnumConverter());
			xstream.registerConverter(new FrameValuesConverter());
			xstream.registerConverter(new FrameValues3DConverter());
		}
		return xstream;
	}

	public File getRepoDir() {
		if(repoDir == null){
			repoDir = FileUtils.checkDirs(DEFAULT_REPO_PATH);
		}
		return repoDir;
	}

	public void setRepositoryPath(String repositoryPath) {
		repoDir = FileUtils.checkDirs(repositoryPath);
	}
}
