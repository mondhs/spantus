package org.spantus.core.extractor.preemphasis;

import org.spantus.core.extractor.preemphasis.Preemphasis.PreemphasisEnum;
import org.spantus.utils.StringUtils;


public abstract class PreemphasisFactory {
	public static Preemphasis createPreemphasis(String preemphasisCode){
		if(!StringUtils.hasText(preemphasisCode)){
			preemphasisCode = PreemphasisEnum.full.name();
		}
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
		case highAvg:
				return new HighAvgPreemphasis();	
		default:
			return new FullPreemphasis();			
		}
	}
}
