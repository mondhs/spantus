package org.spantus.extract.segments.online;

public abstract class ExtremeOnClassifierServiceFactory {
	
	public static ExtremeOnlineClusterService createClusterService(){
//		return new ExtremeOnlineClusterServiceImpl();
		return new ExtremeOnlineClusterServiceStaticImpl();
	}
}
