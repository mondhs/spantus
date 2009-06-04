package org.spantus.ui;


public class ModelEntryByOrderComparator extends ModelEntryByNameComparator {

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
		if(o1.getOrder() == null && o2.getOrder() == null){
			return 0;
		}
		if(o1.getOrder() == null ){
			return -1;
		}
		if(o2.getOrder() == null){
			return 1;
		}
		int compare = o1.getOrder().compareTo(o2.getOrder());
		compare = compare == 0?super.compare(o1, o2):compare;
		return compare;
	}

}
