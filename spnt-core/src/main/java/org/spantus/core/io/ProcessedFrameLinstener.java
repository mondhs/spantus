package org.spantus.core.io;

public interface ProcessedFrameLinstener {
	public void started(Long total);
	public void processed(Long current, Long total);
	public void ended();
	public void registerProcessedFrameLinstener(ProcessedFrameLinstener linstener);

}
