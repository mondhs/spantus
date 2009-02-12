package org.spantus.demo.audio;

public abstract class AudioManagerFactory {
	public static AudioManager createAudioManager(){
		return new DemoAudioManager();
	}
}
