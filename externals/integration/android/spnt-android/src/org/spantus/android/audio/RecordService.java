package org.spantus.android.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;

import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.android.service.SpantusAudioCtxService;
import org.spantus.logger.Logger;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RecordService {

	private static final Logger LOG = Logger.getLogger(RecordService.class);

	private static final int SAMPLE_RATE_IN_HZ = 8000;
//	public static final int SAMPLE_SIZE_IN_BITS = 16;
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;;
	private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
	// private static final int MAX_VOL = 600;
	// private static final int MIN_VAL = 0;
	// private static final int START_RECORD_FROM = 350;
	// private static final int CHECK_BLOCK_COUNT = 5;

	private URL recordUrl = null;
	private boolean cancel;
	private boolean recording;

	SpantusAudioCtxService audioCtxService;

	protected void beforeRecord(SpantusAudioCtx ctx) {
		ctx.setRecordState(RecordState.RECORD);
		cancel = false;
		recording = true;
	}

	protected void afterRecord(SpantusAudioCtx ctx) {
		ctx.setRecordState(RecordState.STOP);
		recording = false;
	}

	

	/**
	 * 
	 * @param ctx
	 */
	public void recordToFile(SpantusAudioCtx ctx) {
		beforeRecord(ctx);

		RecordFormat recordFormat = newRecordFormat(ctx);
		ctx.setLastFileName("tmp.json");
		File file = getAudioCtxService().recreateFile(ctx);
		LOG.debug("[recordToFile]Record to file: {0}", file.getAbsolutePath());

		try {
			AudioRecord audioRecord = newAudioRecord(recordFormat);
			byte[] buffer = new byte[recordFormat.getBufferSize()];
			LinkedList<Byte> bytes = new LinkedList<Byte>();
			int totalSize = 0;
			audioRecord.startRecording();
			while (RecordState.RECORD.equals(ctx.getRecordState())) {
				int bufferReadResult = audioRecord.read(buffer, 0,
						recordFormat.getBufferSize());
				if (bufferReadResult < 0) {
					throw new IllegalArgumentException("Nothing to read");
				}
				totalSize += bufferReadResult;
				for (Byte byte1 : buffer) {
					bytes.add(byte1);
				}
				if (totalSize > ctx.getMaxLengthInSamples()) {
					LOG.debug("[recordToFile]time out: {0} >{1}", totalSize,
							ctx.getMaxLengthInSamples());
					break;
				}
				if (cancel) {
					LOG.debug("[recordToFile]canceled");
					break;
				}
			}

			audioRecord.stop();
			audioRecord.release();

			FileOutputStream out = new FileOutputStream(file);
			saveDataToFile(bytes, out, recordFormat, totalSize);
			ctx.setRecordState(RecordState.STOP);

		} catch (Exception t) {
			LOG.error(t);
		}
		afterRecord(ctx);
	}

	protected AudioRecord newAudioRecord(RecordFormat recordFormat) {
		AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE,
				recordFormat.getSampleRate(),
				recordFormat.getChannelConfiguration(),
				recordFormat.getAudioEncoding(), recordFormat.getBufferSize());
		return audioRecord;
	}

	protected RecordFormat newRecordFormat(SpantusAudioCtx ctx) {
		RecordFormat recordFormat = new RecordFormat();

		Float sampleRateFlt = ctx.getSampleRate();

		int sampleRate = sampleRateFlt == null ? SAMPLE_RATE_IN_HZ
				: sampleRateFlt.intValue();
		int channelConfiguration = CHANNEL_CONFIG;
		int audioEncoding = AUDIO_FORMAT;
		int channels = 1;
		// Create a new AudioRecord object to record the audio.
		int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
				channelConfiguration, audioEncoding);

		if (AudioRecord.ERROR_BAD_VALUE == bufferSize) {
			LOG.error("Error bad value");
		}
		bufferSize = Math.max(bufferSize, 4096);

		recordFormat.setSampleRate(sampleRate);

		recordFormat.setChannelConfiguration(channelConfiguration);
		recordFormat.setAudioEncoding(audioEncoding);
		recordFormat.setBufferSize(bufferSize);
		recordFormat.setChannels(channels);
		return recordFormat;
	}

	/**
	 * 
	 * @param bytes
	 * @param outputFile
	 * @param recordFormat
	 * @param totalReadBytes
	 * @throws java.io.IOException
	 */
	public void saveDataToFile(LinkedList<Byte> bytes, OutputStream out,
			RecordFormat recordFormat, int totalReadBytes)
			throws java.io.IOException {

		long totalAudioLen = totalReadBytes;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = recordFormat.getSampleRate();
		long byteRate = longSampleRate * 2 * recordFormat.getChannels();

		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) recordFormat.getChannels();
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * recordFormat.getChannels()); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);

		int i = 0;

		byte[] buffer = new byte[recordFormat.getBufferSize()];
		int total = 0;
		for (byte b : bytes) {
			buffer[i++] = b;
			total++;
			if (total >= totalReadBytes) {
				out.write(buffer, 0, i);
				break;
			}
			if (i >= recordFormat.getBufferSize()) {
				out.write(buffer);
				buffer = new byte[recordFormat.getBufferSize()];
				i = 0;
			}
		}
		out.close();
	}



	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isRecording() {
		return recording;
	}

	public void setRecording(boolean recording) {
		this.recording = recording;
	}

	public URL getRecordUrl() {
		return recordUrl;
	}

	public void setRecordUrl(URL recordUrl) {
		this.recordUrl = recordUrl;
	}

	public SpantusAudioCtxService getAudioCtxService() {
		if (audioCtxService == null) {
			audioCtxService = new SpantusAudioCtxService();
		}
		return audioCtxService;
	}

	public void setAudioCtxService(SpantusAudioCtxService audioCtxService) {
		this.audioCtxService = audioCtxService;
	}

}
