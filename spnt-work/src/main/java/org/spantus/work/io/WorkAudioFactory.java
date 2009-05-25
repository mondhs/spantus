package org.spantus.work.io;

import org.spantus.core.io.AudioReader;
import org.spantus.work.WorkReadersEnum;

public abstract class WorkAudioFactory {
	public static AudioReader createAudioReader(WorkReadersEnum readerType){
		return new WorkAudioReader(readerType);
	}
}
