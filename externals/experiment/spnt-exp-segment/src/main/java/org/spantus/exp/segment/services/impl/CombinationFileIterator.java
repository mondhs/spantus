package org.spantus.exp.segment.services.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spantus.exception.ProcessingException;

public class CombinationFileIterator implements Iterator<Set<String>>{
	public static final String FILE_NAME = "./target/feature_combinations.data";
	String fileName = FILE_NAME;
	BufferedReader in;
	String currentLine;
	
	List<String> encNames;
	
	public CombinationFileIterator() {
		try {
			this.in = 
			    new BufferedReader(new FileReader(fileName));
			decodeHeader(in.readLine());
			currentLine = in.readLine();
		} catch (FileNotFoundException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		

	}
	
	public boolean hasNext() {
		return currentLine!=null;
	}
	public Set<String> next() {
		String rtnString = currentLine;
		try {
			if(currentLine != null){
				if((currentLine = in.readLine())==null){
					in.close();
				}
			}
			return processString(rtnString);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	
	public void remove() {}
	
	//Protected
	protected Set<String> processString(String str){
		String[] strArr = str.split(" ");
		Set<String> rtnSet = new LinkedHashSet<String>();
		for (String nameId : strArr) {
			Integer id = Integer.valueOf(nameId);
			rtnSet.add(getEncNames().get(id));
		}
		return rtnSet;
	}

	protected void decodeHeader(String header){
		String[] names = header.substring(header.indexOf("[")+1, header.lastIndexOf("]"))
					.split(", ");
		for (String str : names) {
			getEncNames().add(str);
		}
	}
	
	public List<String> getEncNames() {
		if(encNames == null){
			encNames = new ArrayList<String>();
		}
		return encNames;
	}

	public void setEncNames(List<String> encNames) {
		this.encNames = encNames;
	}
	
	
}
