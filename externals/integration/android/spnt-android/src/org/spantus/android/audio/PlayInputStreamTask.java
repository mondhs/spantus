package org.spantus.android.audio;

import java.io.InputStream;
import java.net.MalformedURLException;

import org.spantus.android.SpntConstant;
import org.spantus.android.dto.SpantusAudioCtx;

import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;

public class PlayInputStreamTask extends AsyncTask<InputStream, Void, Void> {

	private PlayServiceImpl playService;
	private SpantusAudioCtx ctx = new SpantusAudioCtx();

	public PlayInputStreamTask(AudioManager audioManager,SpantusAudioCtx ctx) {
		playService = new PlayServiceImpl();
		playService.setAudioManager(audioManager);
		this.ctx = ctx;
	}
	
	@Override
	protected Void doInBackground(InputStream... params) {
		ctx.setIsPlaying(true);
		try {
			for (InputStream inputStream : params) {
				playService.play(inputStream);
			}
		} catch (MalformedURLException e) {
			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, "Url issues", e);
		}
		ctx.setIsPlaying(false);
		return null;
	}
	

	public void close() {
		playService.close();
	}

	public boolean isPlaing() {
		return playService.isPlaying();
	}



}
