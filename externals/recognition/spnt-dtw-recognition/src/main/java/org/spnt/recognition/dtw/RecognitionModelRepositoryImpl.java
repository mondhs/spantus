package org.spnt.recognition.dtw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumConverter;

public class RecognitionModelRepositoryImpl implements RecognitionModelRepository {

	private XStream xstream;
	
	private String repositoryPath = null;
	
	public static final String DEFAULT_REPO_PATH = "./target/";
	
	List<RecognitionModelEntry> repo;
	
	public List<RecognitionModelEntry> findAllEntries() {
		if(repo == null){
			repo = new ArrayList<RecognitionModelEntry>();
			repo.add(read("vienas"));
			repo.add(read("du"));
		}
		return repo;
	}

	protected RecognitionModelEntry read(String name) {
		File file = new File(getRepositoryPath()+ name + ".rmspnt.xml");
		RecognitionModelEntry entry = null;
		try {
			FileReader inFile = new FileReader(file);
			entry = (RecognitionModelEntry)getXsteam().fromXML(inFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return entry;
		
	}

	
	
	public void save(RecognitionModelEntry entry) {
		File file = new File(getRepositoryPath()+ entry.getName() + ".rmspnt.xml");
		try {
			FileWriter outputFile = new FileWriter(file,false);	
			getXsteam().toXML(entry, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected XStream getXsteam(){
		if(xstream == null){
			xstream = new XStream();
			xstream.registerConverter(new EnumConverter());
		}
		return xstream;
	}

	public String getRepositoryPath() {
		if(repositoryPath == null){
			repositoryPath = DEFAULT_REPO_PATH;
		}
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}
}
