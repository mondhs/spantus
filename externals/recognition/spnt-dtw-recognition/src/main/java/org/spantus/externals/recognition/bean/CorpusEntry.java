package org.spantus.externals.recognition.bean;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class CorpusEntry {
	Long id;
	String name;
	Map<String, FeatureData> featureMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, FeatureData> getFeatureMap() {
		if(featureMap == null){
			featureMap = new HashMap<String, FeatureData>();
		}
		return featureMap;
	}
        @Override
        public String toString() {
            return MessageFormat.format("{0}]: [name:{1}, id:{2,number,#}", 
                    getClass().getSimpleName(),
                    getName(),
                    getId());
        }

}
