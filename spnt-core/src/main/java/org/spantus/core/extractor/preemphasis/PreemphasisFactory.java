package org.spantus.core.extractor.preemphasis;

import org.spantus.core.extractor.preemphasis.Preemphasis.PreemphasisEnum;


public abstract class PreemphasisFactory {
	public static Preemphasis createPreemphasis(String preemphasisCode){
		PreemphasisEnum preemphasisEnum = null;
                try{
                    preemphasisEnum = PreemphasisEnum.valueOf(preemphasisCode);
                }catch(Exception e){}
		if(preemphasisCode == null){
			return new FullPreemphasis();			
		}
		switch (preemphasisEnum) {
		case full:
			return new FullPreemphasis();			
		case high:
			return new HighPreemphasis();			
		case middle:
			return new MiddlePreemphasis();			
		default:
			return new FullPreemphasis();			
		}
	}
}
