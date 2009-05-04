package org.spnt.recognition.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spantus.work.services.converter.FrameValues3DConverter;
import org.spantus.work.services.converter.FrameValuesConverter;
import org.spantus.work.util.FileUtils;
import org.spnt.recognition.bean.CorpusEntry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class CorpusRepositoryImpl implements CorpusRepository {

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

	public void save(CorpusEntry entry) {
		File file = new File(getRepoDir(),entry.getName() + CORPUS_ENTRY_FILE_EXT);
		if(file.exists()){
			file = new File(getRepoDir(),
					entry.getName() + "-" + System.currentTimeMillis() + CORPUS_ENTRY_FILE_EXT);
		}
		entry.setId(System.currentTimeMillis());
		try {
			FileWriter outputFile = new FileWriter(file,false);	
			getXsteam().toXML(entry, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
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
