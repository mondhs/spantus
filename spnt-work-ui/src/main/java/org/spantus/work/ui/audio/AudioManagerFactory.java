package org.spantus.work.ui.audio;

public abstract class AudioManagerFactory {
	public static AudioManager createAudioManager(){
		return new WorkAudioManager();
	}
}
