package org.spantus.work.io;

import java.net.URL;

import org.spantus.core.io.SignalReader;
import org.spantus.core.io.SimpleSignalReader;
import org.spantus.exception.ProcessingException;
import org.spantus.work.WorkReadersEnum;

public abstract class WorkAudioFactory {
	public static SignalReader createAudioReader(URL url, WorkReadersEnum readerType){
		SignalReader signalReader = new WorkAudioReader(readerType);
		if(signalReader.isFormatSupported(url)){
			return signalReader;
		}else if((signalReader = new SimpleSignalReader()).isFormatSupported(url)){
			return signalReader;
		}else{
			throw new ProcessingException("Format not supported");
		}
	}
}
