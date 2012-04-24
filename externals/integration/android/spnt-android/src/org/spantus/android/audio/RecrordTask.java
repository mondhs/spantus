package org.spantus.android.audio;

import org.spantus.android.HandlerUtils;
import org.spantus.android.dto.ExtractorReaderCtx;
import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.core.io.RecordWraperExtractorReader;

import android.os.AsyncTask;
import android.os.Handler;

public class RecrordTask extends AsyncTask<Void, Void, Void> {
	private SpantusAudioCtx ctx;
	private RecordServiceReader recordService;
	private Handler mHandler;

	public RecrordTask(SpantusAudioCtx ctx, Handler mHandler) {
		this.ctx = ctx;
		this.mHandler = mHandler;
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	}

	@Override
	protected Void doInBackground(Void... params) {
		ctx.setIsRecording(true);
		HandlerUtils.addMessage(mHandler, "Recording Started");
		if (Boolean.TRUE.equals(ctx.getIsOnline())) {
			getRecordService().recordToUrl(ctx);
		} else {
			ExtractorReaderCtx readerCtx = getRecordService().createReader(ctx);
			getRecordService().recordToReader(ctx,
					new RecordWraperExtractorReader(readerCtx.getReader()));

		}
		ctx.setIsRecording(false);
		HandlerUtils.addMessage(mHandler, "Recording Stoped");
		return null;
	}


	public RecordServiceReader getRecordService() {
		if (recordService == null) {
			recordService = new RecordServiceReader();
			recordService.setRecordUrl(getCtx().getRecordUrl());
		}
		return recordService;
	}

	public void setRecordService(RecordServiceReader recordService) {
		this.recordService = recordService;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		recordService.setCancel(true);
	}

	public SpantusAudioCtx getCtx() {
		return ctx;
	}

	public void setCtx(SpantusAudioCtx ctx) {
		this.ctx = ctx;
	}

}
