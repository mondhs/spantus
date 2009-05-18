package org.spantus.work.wav;

public abstract class AudioManagerFactory {
	public static AudioManager createAudioManager(){
		return new WorkAudioManager();
	}
}
