package org.spantus.android.audio;

import java.net.URL;

import android.media.AudioManager;
import android.os.AsyncTask;

public class PlayUrlTask extends AsyncTask<URL, Integer, Long> {
	private PlayServiceImpl playService;


	public PlayUrlTask(AudioManager audioManager) {
		playService = new PlayServiceImpl();
		playService.setAudioManager(audioManager);
	}
	
	@Override
	protected Long doInBackground(URL... params) {
		for (URL url : params) {
			playService.play(url);
		}
		return 1L;
	}

	

//	private void play(URL url) throws MalformedURLException {
//		beforePlay();
//		int minBufferSize = AudioTrack.getMinBufferSize(8000,
//				AudioFormat.CHANNEL_CONFIGURATION_MONO,
//				AudioFormat.ENCODING_PCM_16BIT);
//		int bufferSize = 512;
//		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
//				AudioFormat.CHANNEL_CONFIGURATION_MONO,
//				AudioFormat.ENCODING_PCM_16BIT, minBufferSize,
//				AudioTrack.MODE_STREAM);
//
//		int i = 0;
//		byte[] s = new byte[bufferSize];
//		try {
//			
//			DataInputStream dis = new DataInputStream(url.openStream());
//
//			at.play();
//			while ((i = dis.read(s, 0, bufferSize)) > -1) {
//				if(stopped){
//					break;
//				}
//				at.write(s, 0, i);
//
//			}
//			at.stop();
//			at.release();
//			dis.close();
//		} catch (FileNotFoundException e) {
//			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, "File not found", e);
//		} catch (IOException e) {
//			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, "IOException", e);
//		} finally {
//			afterPlay();
//		}
//	}

//	private void beforePlay() {
//		plaing = true;
//		audioManager.setMode(AudioManager.MODE_IN_CALL);
//		audioManager.setSpeakerphoneOn(true);
//	}
//
//	private void afterPlay() {
//		plaing = false;
//		audioManager.setMode(AudioManager.MODE_NORMAL);
//		audioManager.setSpeakerphoneOn(false);
//	}


	public void close() {
		playService.close();
	}

	public boolean isPlaing() {
		return playService.isPlaying();
	}



}
