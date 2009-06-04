package org.spantus.ui;

import java.util.Comparator;

public class ModelEntryByNameComparator implements Comparator<ModelEntry> {

	public int compare(ModelEntry o1, ModelEntry o2) {
		if(o1 == null && o2 == null){
			return 0;
		}
		if(o1 == null ){
			return -1;
		}
		if(o2 == null){
			return 1;
		}
		if(o1.getValue() == null && o2 == o1.getValue()){
			return 0;
		}
		if(o1.getValue() == null ){
			return -1;
		}
		if(o2.getValue() == null){
			return 1;
		}
		return o1.getKey().toString().compareTo(o2.getKey().toString());
	}

}
