package org.spantus.demo.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.demo.services.ReadersEnum;

public class ReaderDto {
	ReadersEnum reader;
	
	Set<String> extractors;
	
	
	public Set<String> getExtractors() {
		if(extractors == null){
			extractors = new LinkedHashSet<String>();
		}
		return extractors;
	}


	public ReadersEnum getReader() {
		if(reader == null){
			reader = ReadersEnum.mpeg7;
		}
		return reader;
	}


	public void setReader(ReadersEnum reader) {
		this.reader = reader;
	}

}
