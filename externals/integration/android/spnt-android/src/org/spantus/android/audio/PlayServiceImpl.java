package org.spantus.android.audio;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.spantus.logger.Logger;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayServiceImpl {
	private static final Logger LOG = Logger.getLogger(PlayServiceImpl.class);
	boolean stopped = false;
	private AudioManager audioManager;
	private boolean playing;
	

	public void play(URL url) {
		try {
			DataInputStream inputStream = new DataInputStream(url.openStream());
			play(inputStream);
		} catch (IOException e) {

			e.printStackTrace();
		}		
	}
	
	public void play(InputStream inputStream) throws MalformedURLException {
		beforePlay();
		int minBufferSize = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		int bufferSize = 512;
		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, minBufferSize,
				AudioTrack.MODE_STREAM);

		int i = 0;
		byte[] s = new byte[bufferSize];
		try {

			DataInputStream dis = new DataInputStream(inputStream);

			at.play();
			while ((i = dis.read(s, 0, bufferSize)) > -1) {
				if (stopped) {
					break;
				}
				at.write(s, 0, i);

			}
			at.stop();
			at.release();
			dis.close();
		} catch (FileNotFoundException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		} finally {
			afterPlay();
		}
	}

	void beforePlay() {
		playing = true;
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		audioManager.setSpeakerphoneOn(true);
	}

	void afterPlay() {
		playing = false;
		audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setSpeakerphoneOn(false);
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}

	public void close() {
		stopped = true;		
	}

	public boolean isPlaying() {
		return  playing;
	}


}