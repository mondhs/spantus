package org.spantus.core.marker.service;

public abstract class MarkerServiceFactory {
	public static IMarkerService createMarkerService(){
		return new MarkerServiceImp();
	}
}
