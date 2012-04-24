package org.spantus.android.ui.record.listener;

import org.spantus.android.SpntConstant;
import org.spantus.android.audio.RecordServiceReader;
import org.spantus.android.audio.RecordState;
import org.spantus.android.dto.SpantusAudioCtx;

import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class RecordListener implements OnCheckedChangeListener {
	private SpantusAudioCtx ctx = new SpantusAudioCtx();
	private RecordServiceReader recordService = new RecordServiceReader();
	private Thread onCreateThread;
	private TextView logTxt;

	public RecordListener(TextView logTxt) {
		this.logTxt = logTxt;
	}

	public void onCheckedChanged(CompoundButton button, boolean isRecording) {
		Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[onCheckedChanged]+++ "
				+ isRecording + "; " + ctx);
		if(!(RecordState.STOP.equals(ctx.getRecordState()) || RecordState.RECORD.equals(ctx.getRecordState()))){
			Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[onCheckedChanged]do nothing ");
			return;
		}
				
		if (isRecording) {
			ctx.setRecordState(RecordState.REQUEST_RECORD);
			Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[onCheckedChanged]"
					+ ctx);
			initRecording(button);
		} else{
			ctx.setRecordState(RecordState.REQUEST_STOP);
			Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[onCheckedChanged]"
					+ ctx);
		}

	}

	private void initRecording(final CompoundButton button) {
		onCreateThread = new Thread(new Runnable() {
			public void run() {
				Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[initRecording]thread started "
						+ ctx);
				while (ctx != null) {
					logTxt.setText(ctx.getRecordState().name());
					if (RecordState.REQUEST_RECORD.equals(ctx.getRecordState())) {
						button.setEnabled(false);
						ctx.setRecordState(RecordState.INIT_RECORD);
						Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[initRecording]"
								+ ctx);
						recordService.recordToUrl(ctx);
					} else if (RecordState.REQUEST_STOP.equals(ctx
							.getRecordState())) {
						Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[initRecording]"
								+ ctx);
						button.setEnabled(false);
					} else if (RecordState.STOP.equals(ctx.getRecordState())) {
						button.setEnabled(true);
						Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[initRecording]"
								+ ctx);
					} else if (RecordState.RECORD.equals(ctx.getRecordState())) {
						Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, "[initRecording]"
								+ ctx);
						button.setEnabled(true);
					}
				}

			}

		}, "Voice Detection Thread");
		onCreateThread.start();
	}

}
