package org.spantus.android.segmentor.services;

import java.util.HashMap;
import java.util.Map;

import org.spantus.android.segmentor.record.entity.ExtractorReaderCtx;
import org.spantus.android.segmentor.record.entity.RecordFormat;
import org.spantus.android.segmentor.record.entity.SpantusAudioCtx;
import org.spantus.android.segmentor.services.impl.AndroidExtractorConfigUtil;
import org.spantus.android.segmentor.services.impl.ExtractMarkerOnlineSegmentatorListener;
import org.spantus.android.segmentor.services.impl.RecordServiceImpl;
import org.spantus.core.extractor.DefaultExtractorInputReader;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.io.BaseWraperExtractorReader;
import org.spantus.core.threshold.AbstractThreshold;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.utils.ExtractorParamUtils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public final class AndroidExtractorsFactory {
	public static final Integer DEFAULT_WINDOW_LENGHT = 10;
	public static final Integer DEFAULT_WINDOW_OVERLAP = 33;
	public static final int SAMPLE_RATE_IN_HZ = 8000;
	public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;;
	public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	public static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
	private static final Logger LOG = Logger
			.getLogger(AndroidExtractorsFactory.class);
	private static final int SAMPLE_SIZE_IN_BITS = 16;

	private static AndroidExtractorsFactory androidExtractorsFactory;
	private static RecordService recordService;
	private static SpantusAudioCtx ctx; 
	
	private AndroidExtractorsFactory() {
		androidExtractorsFactory = new AndroidExtractorsFactory();
	}
	
	
	
	public static AndroidExtractorsFactory getFactory(){
		return androidExtractorsFactory;
	}
	
	public RecordService createRecordService(){
		if(AndroidExtractorsFactory.recordService == null){
			RecordServiceImpl recordServiceImpl = new RecordServiceImpl();
			recordService = recordServiceImpl;
		}
		return recordService;
	}
	
	public SpantusAudioCtx getSpantusAudioCtx(){
		if(ctx == null){
		 ctx = new SpantusAudioCtx();
		}
		return ctx;
	}

	public IExtractorInputReader createReader(SignalFormat format) {
		DefaultExtractorInputReader reader = new DefaultExtractorInputReader();
		reader.setConfig(createConfig(format));
		return reader;
	}

	/**
	 * 
	 * @return
	 */
	public ExtractorReaderCtx createDefaultReader() {
		IExtractorConfig extractorConfig = AndroidExtractorConfigUtil
				.defaultConfig(8000.0, DEFAULT_WINDOW_LENGHT,
						DEFAULT_WINDOW_OVERLAP);
		Map<String, ExtractorParam> params = new HashMap<String, ExtractorParam>();
		ExtractorReaderCtx readerCtx = createReader(extractorConfig, params,
				ExtractorEnum.ENERGY_EXTRACTOR,
				ExtractorEnum.LOUDNESS_EXTRACTOR,
				ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR,
				ExtractorEnum.WAVFORM_EXTRACTOR, ExtractorEnum.MFCC_EXTRACTOR);
		return readerCtx;
	}

	public ExtractorReaderCtx createReader(
			IExtractorConfig extractorConfig,
			Map<String, ExtractorParam> params, ExtractorEnum... extractors) {
		IExtractorInputReader reader = new DefaultExtractorInputReader();
		ExtractMarkerOnlineSegmentatorListener segmentatorListener = new ExtractMarkerOnlineSegmentatorListener();
		ExtractorReaderCtx readerCtx = new ExtractorReaderCtx(reader,
				segmentatorListener);

		reader.setConfig(extractorConfig);

		for (ExtractorEnum extractor : extractors) {
			IClassifier aClassifier = ExtractorUtils.registerThreshold(reader,
					extractor, null, ClassifierEnum.offline);
			if (aClassifier != null) {
				aClassifier.addClassificationListener(segmentatorListener);

				ExtractorParam param = ExtractorParamUtils.getSafeParam(params,
						extractor.name());
				Number threasholdCoef = ExtractorParamUtils.<Double> getValue(
						param,
						ExtractorParamUtils.commonParam.threasholdCoef.name(),
						1.1D);
				if (aClassifier instanceof AbstractThreshold) {
					((AbstractThreshold) aClassifier).setCoef(threasholdCoef
							.doubleValue());
				}
			}
		}

		return readerCtx;
	}

	public RecordFormat createRecordFormat() {
		RecordFormat recordFormat = new RecordFormat();

		int sampleRate = SAMPLE_RATE_IN_HZ;
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
		recordFormat.setSampleSizeInBits(SAMPLE_SIZE_IN_BITS);
		recordFormat.setChannelConfiguration(channelConfiguration);
		recordFormat.setAudioEncoding(audioEncoding);
		recordFormat.setBufferSize(bufferSize);
		recordFormat.setChannels(channels);
		return recordFormat;
	}

	public AudioRecord createAudioRecord(RecordFormat recordFormat) {
		AudioRecord audioRecord = new AudioRecord(AUDIO_SOURCE,
				recordFormat.getSampleRate(),
				recordFormat.getChannelConfiguration(),
				recordFormat.getAudioEncoding(), recordFormat.getBufferSize());
		return audioRecord;
	}
	
	public BaseWraperExtractorReader createBaseWraperExtractorReader(ExtractorReaderCtx readerCtx, int size){
		return new BaseWraperExtractorReader(readerCtx.getReader(), size);
	}

	public static IExtractorConfig createConfig(SignalFormat format) {
		return AndroidExtractorConfigUtil.defaultConfig(format.getSampleRate());
	}

}
