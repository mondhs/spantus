package org.spantus.exp.segment.services;

import org.spantus.exp.segment.services.impl.MakerComparisonTIAImpl;
import org.spantus.exp.segment.services.impl.ProcessReaderImpl;



/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Jun 11, 2008
 *
 */
public abstract class ExpServiceFactory {
	
	private static MakerComparison defaultMakerComparison;
	public static MakerComparison createMakerComparison(){
		if(defaultMakerComparison == null){
//			defaultMakerComparison = new MakerComparisonFisherImpl();
//			defaultMakerComparison = new MakerComparisonImpl();
			defaultMakerComparison = new MakerComparisonTIAImpl();
		}
		return defaultMakerComparison; 
	}
	private static ProcessReader defaultProcessReader;
	public static ProcessReader createProcessReader(){
		if(defaultProcessReader == null){
			ProcessReaderImpl pr = new ProcessReaderImpl();
			defaultProcessReader = pr;
		}
		return defaultProcessReader; 
	}
	
}
