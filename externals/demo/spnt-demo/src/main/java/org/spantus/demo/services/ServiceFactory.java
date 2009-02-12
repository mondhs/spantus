package org.spantus.demo.services;


public abstract class ServiceFactory {
	public static SampleService createSamplesService(){
		return new SamplesServiceImpl();
	}
	public static ReaderService createReaderService(){
		return new DefaultReaderService();
	}
}
