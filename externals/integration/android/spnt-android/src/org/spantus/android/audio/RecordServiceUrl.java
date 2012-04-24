package org.spantus.android.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.logger.Logger;

import android.media.AudioRecord;

	
public class RecordServiceUrl  extends RecordService {
	private static final Logger LOG = Logger.getLogger(RecordServiceUrl.class);
	
	public void recordToUrl(SpantusAudioCtx ctx) {
		LOG.debug("[recordToUrl]++++");
		beforeRecord(ctx);
		RecordFormat recordFormat = newRecordFormat(ctx);
		AudioRecord audioRecord = newAudioRecord(recordFormat);

		byte[] buffer = new byte[recordFormat.getBufferSize()];

		audioRecord.startRecording();
		LinkedList<Byte> bytes = new LinkedList<Byte>();
		int totalSize = 0;
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
				LOG.debug("[recordToUrl]time out");
				break;
			}
			if (isCancel()) {
				LOG.debug("[recordToUrl]canceled");
				break;
			}
		}
		audioRecord.stop();
		audioRecord.release();
		afterRecordDone(bytes, totalSize, recordFormat);
		afterRecord(ctx);
		LOG.debug("[recordToUrl]---");
	}
	
	private void afterRecordDone(LinkedList<Byte> bytes, int totalSize,
			RecordFormat recordFormat) {
		try {
			LOG.debug("[recordToUrl]flushing");
			HttpURLConnection conn = getRecordUrl(recordFormat);
			LOG.debug("[recordToUrl]Recording Format {0}", recordFormat);
			flushAudio(conn, bytes, totalSize, recordFormat);
		} catch (IOException e) {
			LOG.error(e);
		}
	}


	private void flushAudio(HttpURLConnection conn, LinkedList<Byte> bytes,
			int totalSize, RecordFormat recordFormat) throws IOException {
		LOG.debug("[flushAudio]++++");
		conn.setFixedLengthStreamingMode(totalSize + 44);
		OutputStream out = conn.getOutputStream();

		saveDataToFile(bytes, out, recordFormat, totalSize);

		out.flush();
		conn.connect();
		out.close();
		LOG.debug("[flushAudio]----");
	}
	
	private HttpURLConnection getRecordUrl(RecordFormat recordFormat) {
		try {
			URL url = getRecordUrl();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type",
					getContentType(recordFormat));
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// conn.setChunkedStreamingMode(2048);
			conn.setRequestMethod("POST");
			// conn.connect();
			return conn;
		} catch (MalformedURLException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param format
	 * @return
	 */
	private String getContentType(RecordFormat recordFormat) {
		// String encoding = null;
		// if (format.getEncoding() == AudioFormat.Encoding.ULAW) {
		// encoding = "MULAW";
		// } else if (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
		// encoding = "L16";
		// }

		return "AUDIO/L16; CHANNELS=1; RATE=" + recordFormat.getSampleRate()
				+ "; BIG=false";
	}

	
}
