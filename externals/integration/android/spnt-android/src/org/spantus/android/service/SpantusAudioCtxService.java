package org.spantus.android.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

public class SpantusAudioCtxService {
	private static final String SPANTUS_SERVER = "spantusServer";
	private static final String PERFORM_SPEECH_UPDATES = "enableSpeechEnviroment";
	private static final String UPLOAD_TO_SERVER = "uploadToServer";
	private static final String SAMPLE_RATE = "sampleRate";
	private static final String MAX_LENGTH = "maxLength";

	public static final Logger LOG = Logger
			.getLogger(SpantusAudioCtxService.class);

	public static SpantusAudioCtxService getInstance() {
		return new SpantusAudioCtxService();
	}

	/**
	 * 
	 * @param spantusServer
	 * @return
	 */
	public SpantusAudioCtx createSpantusAudioParams(
			SharedPreferences sharedPrefs) {

		boolean isPlayabe = sharedPrefs
				.getBoolean(PERFORM_SPEECH_UPDATES, true);
		String spantusServer = sharedPrefs.getString(SPANTUS_SERVER, "");
		boolean isOnline = sharedPrefs.getBoolean(UPLOAD_TO_SERVER, true);
		String sampleRateStr = sharedPrefs.getString(SAMPLE_RATE, "8000");
		Float sampleRate = Float.valueOf(sampleRateStr);
		String maxLengthStr = sharedPrefs.getString(MAX_LENGTH, "10");
		Integer maxLength = Integer.valueOf(maxLengthStr);
		File workingDir = Environment.getExternalStorageDirectory();

		SpantusAudioCtx anAudioParams = new SpantusAudioCtx();
		anAudioParams.setSampleRate(sampleRate);
		anAudioParams.setMaxLengthInSamples(maxLength.intValue()
				* sampleRate.intValue());
		anAudioParams.setIsPlayabe(isPlayabe);
		anAudioParams.setIsOnline(isOnline);
		anAudioParams.setUseSpeechDetector(true);
		anAudioParams.setAllowStopPlaying(false);
		anAudioParams.setSpantusServer(spantusServer);
		anAudioParams.setSpantusServerCorpus(urlParameter(spantusServer,
				"/api/corpora"));
		anAudioParams.setRecordUrl(urlParameter(spantusServer, "/api/record"));
		anAudioParams.setTrainUrl(urlParameter(spantusServer, "/api/recognition/repo"));
		anAudioParams.setRecognizedUrl(urlParameter(spantusServer,
				"/api/recognition/recognize"));
		anAudioParams.setPlayUrl(urlParameter(spantusServer,
				"/api/play?poll=true"));
		anAudioParams.setWorkingDir(workingDir);

		// String audioFormatStr = applet
		// .getParameter(SpantusAudioCtx.RECORD_AUDIO_FORMAT);
		// String sampleRateStr = applet
		// .getParameter(SpantusAudioCtx.RECORD_SAMPLE_RATE);
		// String isLittleEndianStr = applet
		// .getParameter(SpantusAudioCtx.RECORD_IS_LITTLE_ENDIAN);
		// anAudioParams.setRecordFormat(getAudioFormatFromParams(applet,
		// audioFormatStr, sampleRateStr, isLittleEndianStr));
		// anAudioParams.setPlayRecordTone(getBooleanParameter(applet,
		// "playRecordTone", false));
		anAudioParams.setLastTimestamp(0L);
		return anAudioParams;
	}

	public File findFile(SpantusAudioCtx ctx) {
		return findFile(ctx, ctx.getLastFileName());
	}
	
	public File findFile(SpantusAudioCtx ctx, String fileName) {
		File dir = new File(ctx.getWorkingDir(), ctx.getSubDirName());
		File file = new File(dir, fileName);
		LOG.debug("Record to file: {0}", file.getAbsolutePath());
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public URL fileToUrl(File file){
		try {
			return file.toURL();
		} catch (MalformedURLException e) {
			throw new ProcessingException(e);
		}
	}
	public Uri fileToUri(File file){
		if (file == null) {
			return null;
		}
		Uri fileNameUri = Uri.parse("file://" + file);
		return fileNameUri;
	}
	
	public File recreateFile(SpantusAudioCtx ctx) {
		return recreateFile(ctx, ctx.getLastFileName());
	}

	public File recreateFile(SpantusAudioCtx ctx, String fileName) {
		File dir = new File(ctx.getWorkingDir(), ctx.getSubDirName());
		File file = new File(dir, fileName);
		LOG.debug("Record to file: {0}", file.getAbsolutePath());
		if (!dir.exists()) {
			dir.mkdir();
		}
		// Delete any previous recording.
		if (file.exists()) {
			file.delete();
		}
		// Create the new file.
		try {
			file.createNewFile();
			LOG.debug("[recreateFile]Record to file: {0}",
					file.getAbsolutePath());
			return file;
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create "
					+ file.toString());
		}
	}

	private URL urlParameter(String baseUrl, String action) {
		String aBaseUrl = baseUrl.replaceAll("http://", "");
		try {
			URI uri = new URI("http://" + aBaseUrl + action);
			return uri.toURL();
		} catch (URISyntaxException e) {
			LOG.error(e);
		} catch (MalformedURLException e) {
			LOG.error(e);
		}
		return null;
	}

}
