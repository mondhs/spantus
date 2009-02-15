package org.spantus.exp.segment.services.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.exception.ProcessingException;

public class FileProcessReaderImpl extends ProcessReaderImpl {

	private PrintStream printStream;
	
	private Map<String, Integer> map;

	public FileProcessReaderImpl() {
		
	}

	public Iterable<Set<String>> generateAllCompbinations(Set<? extends IGeneralExtractor> fullSet, int combinationDepth){
		try {
			printStream = new PrintStream(new FileOutputStream(
					CombinationFileIterator.FILE_NAME));
			String header = encodeHeader(fullSet);
			printStream.println(header);
			generateList(fullSet, combinationDepth);
			printStream.close();
		} catch (FileNotFoundException e) {
			throw new ProcessingException(e);
		}
		return new CombinationIterable();
	}
	
	protected String encodeHeader(Set<? extends IGeneralExtractor> fullSet){
		Integer i = 0;
		for (IGeneralExtractor generalExtractor : fullSet) {
			if(!getMap().containsKey(generalExtractor.getName())){
				getMap().put(generalExtractor.getName(), i++);
			}
		}
		return getMap().keySet().toString();
	}
	/**
	 * 
	 */
	@Override
	protected boolean addToCombinationSet(Set<Set<String>> allCombinations,
			Set<IGeneralExtractor> set) {
		printStream.println(getEncName(set));
		return true;
	}
	/**
	 * 
	 * @param set
	 * @return
	 */
	protected String getEncName(Set<IGeneralExtractor> set){
		StringBuffer buf = new StringBuffer();
		String separator = "";
		for (IGeneralExtractor threshold : set) {
			buf.append(separator).append(getMap().get(threshold.getName()));
			separator = " ";
		}
		return buf.toString();	
	}

	public Map<String, Integer> getMap() {
		if(map == null){
			map = new LinkedHashMap<String, Integer>();
		}
		return map;
	}

	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}
	
}
