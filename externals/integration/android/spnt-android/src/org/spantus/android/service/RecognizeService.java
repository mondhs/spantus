package org.spantus.android.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.spantus.android.audio.RecordServiceReader;
import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.dao.SignalSegmentDao;
import org.spantus.logger.Logger;

public class RecognizeService {
	private static final Logger LOG = Logger
			.getLogger(RecordServiceReader.class);
	
	private RecognitionResultDetailsJsonDao recognitionResultDetailsJsonDao;

	private SignalSegmentAndroidJsonDao segmentDao;

	private SpantusAudioCtx ctx;
	
	public RecognizeService(SpantusAudioCtx ctx) {
		this.ctx = ctx;
	}
	

	
	public List<RecognitionResultDetails> recognize( SignalSegment segment) throws IOException, URISyntaxException, JSONException {
		HttpURLConnection conn = getRecognizedUrl(ctx);
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		
		getSegmentDao().write(segment, byteOutputStream);
		
		conn.setFixedLengthStreamingMode(byteOutputStream.size());
		OutputStream outputStream = conn.getOutputStream();
		outputStream.write(byteOutputStream.toByteArray());
		
		InputStream inputStream = conn.getInputStream();
		List<RecognitionResultDetails> result = getRecognitionResultDetailsJsonDao().read(inputStream);
		return result;
		
	}
	
	
	private HttpURLConnection getRecognizedUrl(SpantusAudioCtx ctx) throws URISyntaxException {
		try {
			
//			URI uri = new URI("http://spantus.cloudfoundry.com/api/recognition/recognize");
//			URL url = new URL("http://spantus.cloudfoundry.com/api/recognition/recognize");
			URL url = ctx.getRecognizedUrl();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// conn.setChunkedStreamingMode(2048);
			conn.setRequestMethod("PUT");
			// conn.connect();
			return conn;
		} catch (MalformedURLException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}


	public RecognitionResultDetailsJsonDao getRecognitionResultDetailsJsonDao() {
		if(recognitionResultDetailsJsonDao == null){
			recognitionResultDetailsJsonDao = new RecognitionResultDetailsJsonDao();
		}
		return recognitionResultDetailsJsonDao;
	}


	public void setRecognitionResultDetailsJsonDao(
			RecognitionResultDetailsJsonDao recognitionResultDetailsJsonDao) {
		this.recognitionResultDetailsJsonDao = recognitionResultDetailsJsonDao;
	}


	public SignalSegmentDao getSegmentDao() {
		if (segmentDao == null) {
			segmentDao = new SignalSegmentAndroidJsonDao();
		}
		return segmentDao;
	}


	public void setSegmentDao(SignalSegmentAndroidJsonDao segmentDao) {
		this.segmentDao = segmentDao;
	}

}
