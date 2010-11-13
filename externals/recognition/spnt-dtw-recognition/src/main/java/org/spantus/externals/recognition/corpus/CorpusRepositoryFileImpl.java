package org.spantus.externals.recognition.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.converter.FrameValues3DConverter;
import org.spantus.work.services.converter.FrameValuesConverter;
import org.spantus.logger.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.spantus.utils.Assert;

public class CorpusRepositoryFileImpl implements CorpusRepository {

	private XStream xstream;
	
        private static Logger log = Logger.getLogger(CorpusRepositoryFileImpl.class);

	private File repoDir = null;
	
	public final static String CORPUS_ENTRY_FILE_EXT =".cspnt.xml";
        public final static String WAV_FILE_EXT =".wav";

                
	public static final String DEFAULT_REPO_PATH = "./target/corpus";
	
	Map<Long, CorpusFileEntry> repository;

	public Collection<CorpusEntry> findAllEntries() {
		return getCastedRepository();
	}

//	public List<CorpusFileEntry> findAllFileEntries() {
//			List<CorpusFileEntry> repoFiles = new ArrayList<CorpusFileEntry>();
//			if(getRepoDir().isDirectory()){
//				for (String fileName : getRepoDir().list()) {
//					if(fileName.endsWith(CORPUS_ENTRY_FILE_EXT)){
//						repoFiles.add(readFileEntry(new File(repoDir,fileName)));
//					}
//				}
//			}
//		return repoFiles;
//	}
        /**
         * Save and reset repo
         * @param entry
         */
        public CorpusEntry save(CorpusEntry entry) {
            CorpusFileEntry  fileEntry = null;
                if (!(entry instanceof CorpusFileEntry)) {
                    if(entry.getId() == null){
                        fileEntry = new CorpusFileEntry();
                        fileEntry.setName(entry.getName());
                        fileEntry.getFeatureMap().putAll(entry.getFeatureMap());
                    }else{
                        throw new ProcessingException("Not implemented");
                    }
                }else{
                    fileEntry = (CorpusFileEntry)entry;
                }
            CorpusFileEntry file = saveOrUpdateFile((CorpusFileEntry) fileEntry);
            return file;
        }
        /**
         * Delete entry from repository
         * @param entry
         * @return
         */
        public CorpusEntry delete(CorpusEntry entry) {
            Assert.isTrue(entry.getId() != null,
                    "Entry withou id. You need save it before deletion");
            CorpusFileEntry fileEntry  = getRepository().get(entry.getId());
            Assert.isTrue(fileEntry != null);
            if(fileEntry.getEntryFile().exists()){
                boolean deleted = fileEntry.getEntryFile().delete();
                if(fileEntry.getWavFile() != null){
                   fileEntry.getWavFile().delete();  
                }
                if(deleted){
                    getRepository().remove(entry.getId());
                }else{
                    throw new ProcessingException("Cannot delete");
                }
            }
            return entry;
        }
	/**
         * Save or update entry in repo
         * @param entry
         * @return
         */
	public CorpusFileEntry saveOrUpdateFile(CorpusFileEntry entry) {
		if(entry.getId() == null){
			entry.setId(System.currentTimeMillis());
		}
                if(entry.getEntryFile() == null){
                    File file = new File(getRepoDir(),entry.getName() + "-" + entry.getId() + CORPUS_ENTRY_FILE_EXT);
                    entry.setEntryFile(file);
                }
		try {
			FileWriter outputFile = new FileWriter(entry.getEntryFile(),false);	
			getXsteam().toXML(entry, outputFile);
		} catch (IOException e) {
                    throw new ProcessingException(e);
		}
                if(getRepository().get(entry.getId())!=null){
                    if(!getRepository().get(entry.getId()).equals(entry)){
                        //replace
                        getRepository().put(entry.getId(), entry);
                    }
                }else{
                    //new
                    getRepository().put(entry.getId(), entry);
                }
		return entry;
	}
	/**
	 * 
	 * @param entry
	 */
	public CorpusEntry update(CorpusEntry entry){
                CorpusFileEntry fileEntry = (CorpusFileEntry)entry;
                fileEntry = saveOrUpdateFile(fileEntry);
                return fileEntry;
	}
        /**
         * 
         * @param corpusEntry
         * @param audioFile
         * @return
         */
	public CorpusEntry update(CorpusEntry entry, AudioInputStream ais) {
                CorpusFileEntry fileEntry = (CorpusFileEntry)entry;
                File wavFile = new File(getRepoDir(),entry.getName() + "-" + entry.getId() + WAV_FILE_EXT);

                try {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
                    log.debug("[update] saved to " + wavFile.getAbsolutePath());
                    fileEntry.setWavFile(wavFile);
                } catch (IOException ex) {
                     throw new ProcessingException(ex);
                }
       
                fileEntry = saveOrUpdateFile(fileEntry);
                return fileEntry;
        }
        
        public String findAudioFileById(Long id) {
            return getRepository().get(id).getWavFile().getAbsolutePath();
        }
        
	protected CorpusFileEntry read(File entryFile) {
		CorpusFileEntry entry = null;
		try {
			FileReader inFile = new FileReader(entryFile);
			entry = (CorpusFileEntry)getXsteam().fromXML(inFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return entry;
		
	}
	protected CorpusFileEntry readFileEntry(File entryFile) {
		CorpusFileEntry fileEntry = null;
		try {
			FileReader inFile = new FileReader(entryFile);
			fileEntry = (CorpusFileEntry)getXsteam().fromXML(inFile);
			fileEntry.setEntryFile(entryFile);
			File wavFile = new File(entryFile.getParent(),
					FileUtils.stripExtention(
							FileUtils.stripExtention(entryFile))+".wav");
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
        
        public Map<Long, CorpusFileEntry> getRepository() {
            if (repository == null) {
                repository= new HashMap<Long, CorpusFileEntry>();
                if (getRepoDir().isDirectory()) {
                    for (String fileName : repoDir.list()) {
                        if (fileName.endsWith(CORPUS_ENTRY_FILE_EXT)) {
                            CorpusFileEntry entry = read(new File(repoDir, fileName));
                            repository.put(entry.getId(), entry);
                        }
                    }
                }
            }
            return repository;
        }

        public void setRepository(Map<Long, CorpusFileEntry> repository) {
            this.repository = repository;
        }

        public Set<CorpusEntry> getCastedRepository() {
            return new HashSet<CorpusEntry>(getRepository().values());
        }


            
}
