package org.spantus.android.segmentor.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spantus.android.segmentor.record.entity.ExtractorReaderCtx;
import org.spantus.android.segmentor.record.entity.RecordFormat;
import org.spantus.android.segmentor.record.entity.RecordState;
import org.spantus.android.segmentor.record.entity.SpantusAudioCtx;
import org.spantus.android.segmentor.services.AndroidExtractorsFactory;
import org.spantus.android.segmentor.services.RecordService;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.io.BaseWraperExtractorReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.service.ExtractorInputReaderService;
import org.spantus.core.service.impl.ExtractorInputReaderServiceImpl;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentFactory.SegmentatorServiceEnum;
import org.spantus.segment.SegmentatorParam;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

import android.media.AudioRecord;

public class RecordServiceImpl implements RecordService {

	private static final Logger LOG = Logger
			.getLogger(RecordServiceImpl.class);

	private ISegmentatorService segmentator;

	private ExtractorInputReaderService extractorInputReaderService;

	public ExtractorReaderCtx createExtractorReaderCtx() {
		IExtractorConfig extractorConfig = new DefaultExtractorConfig();
		Map<String, ExtractorParam> params = new HashMap<String, ExtractorParam>();
		ExtractorReaderCtx readerCtx = AndroidExtractorsFactory.getFactory().createReader(
				extractorConfig, params, ExtractorEnum.ENERGY_EXTRACTOR,
				ExtractorEnum.WAVFORM_EXTRACTOR
				);
		return readerCtx;
	}
	
	@Override
	public void stopRequest(SpantusAudioCtx ctx) {
		ctx.setRecordState(RecordState.STOP_REQUEST);
	}

	/**
	 * 
	 * @param ctx
	 */
	@Override
	public void record(SpantusAudioCtx ctx,
			BaseWraperExtractorReader wrappedReader) {
		beforeRecord(ctx);

		RecordFormat recordFormat = AndroidExtractorsFactory.getFactory().createRecordFormat();
		wrappedReader.setSampleSizeInBits(recordFormat.getSampleSizeInBits());
		try {
			AudioRecord audioRecord = AndroidExtractorsFactory.getFactory().createAudioRecord(recordFormat);
			byte[] buffer = new byte[recordFormat.getBufferSize()];
			ctx.setSrartedOn(System.currentTimeMillis());
			
			audioRecord.startRecording();
			while (RecordState.RECORDING.equals(ctx.getRecordState())) {
				int bufferReadResult = audioRecord.read(buffer, 0,
						recordFormat.getBufferSize());
				if (bufferReadResult < 0) {
					throw new IllegalArgumentException("Nothing to read");
				}
				putValues(buffer, wrappedReader);
				ctx.setSamplesProcessed(ctx.getSamplesProcessed()+bufferReadResult);
			}
			flush(wrappedReader);
			audioRecord.stop();
			audioRecord.release();


		} catch (Exception ex) {
			LOG.error(ex);
		}
		afterRecord(ctx);
	}




	

	protected void afterRecord(SpantusAudioCtx ctx) {
		ctx.setRecordState(RecordState.STOPED);

		
	}

	protected void beforeRecord(SpantusAudioCtx ctx) {
		ctx.setRecordState(RecordState.RECORDING);
	}

	/**
	 * 
	 * @param readerCtx
	 * @return
	 */
	public List<SignalSegment> extractSegments(ExtractorReaderCtx readerCtx) {
		List<SignalSegment> segments = new LinkedList<SignalSegment>();
		Set<IClassifier> classifiers = getExtractorInputReaderService()
				.extractClassifiers(readerCtx.getReader());
		MarkerSetHolder markerSetHolder = getSegmentator().extractSegments(
				classifiers, createSegmentatorParam());
		MarkerSet markerSet = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		if (markerSet == null) {
			markerSet = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.phone.name());
		}
		if(markerSet == null){
			throw new ProcessingException("no segment layer was found");
		}
		List<Marker> markers = markerSet.getMarkers();

		for (Marker marker : markers) {
			LOG.debug("[extractSegments]marker {0}", marker);
			Map<String, IValues> featureData = getExtractorInputReaderService()
					.findAllVectorValuesForMarker(readerCtx.getReader(), marker);
			SignalSegment segment = createSignalSegment(marker, featureData);
			segments.add(segment);

		}
		// MarkerSet markerSet =
		// readerCtx.getSegmentatorListener().getMarkSet();

		return segments;
	}

	public SignalSegment createSignalSegment(Marker marker, Map<String, IValues> featureData) {
		SignalSegment segment = new SignalSegment();
		segment.setMarker(marker);
		for (Entry<String, IValues> entry : featureData.entrySet()) {
			if (entry.getValue() instanceof FrameValues) {
				segment.getFeatureFrameValuesMap().put(
						entry.getKey(),
						new FrameValuesHolder((FrameValues) entry
								.getValue()));
			} else if (entry.getValue() instanceof FrameVectorValues) {
				segment.getFeatureFrameVectorValuesMap().put(
						entry.getKey(),
						new FrameVectorValuesHolder(
								(FrameVectorValues) entry.getValue()));
			} else {
				throw new IllegalArgumentException("Not implemented");
			}
		}
		return segment;
	}

	
	@Override
	public void flush(BaseWraperExtractorReader wrappedReader) {
		wrappedReader.pushValues();
	}
	/**
	 * 
	 * @param config
	 * @return
	 */
	public SegmentatorParam createSegmentatorParam() {
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(91L);
		param.setMinSpace(61L);
		param.setExpandEnd(61L);
		param.setExpandStart(61L);
		return param;
	}

	/**
	 * 
	 * @param buffer
	 * @param wrappedReader
	 */
	@Override
	public void putValues(byte[] buffer, BaseWraperExtractorReader wrappedReader) {
		for (Byte value : buffer) {
			wrappedReader.put(value);
		}
	}




	public ExtractorInputReaderService getExtractorInputReaderService() {
		if (extractorInputReaderService == null) {
			extractorInputReaderService = new ExtractorInputReaderServiceImpl();
		}
		return extractorInputReaderService;
	}

	public void setExtractorInputReaderService(
			ExtractorInputReaderService extractorInputReaderService) {
		this.extractorInputReaderService = extractorInputReaderService;
	}




	public ISegmentatorService getSegmentator() {
		if (segmentator == null) {
			segmentator = SegmentFactory
					.createSegmentator(SegmentatorServiceEnum.online.name());
		}
		return segmentator;
	}

	public void setSegmentator(ISegmentatorService segmentator) {
		this.segmentator = segmentator;
	}

	public void readFile(URL inputFile, RecordService aRecordService,
			BaseWraperExtractorReader wrappedReader) throws IOException {
		int mFrameBytes;
		// int mFileSize;
		int mSampleRate;
		int mChannels;
		// int mOffset = 0;

		InputStream stream = inputFile.openStream();
		byte[] header = new byte[12];
		stream.read(header, 0, 12);
		// mOffset += 12;
		if (header[0] != 'R' || header[1] != 'I' || header[2] != 'F'
				|| header[3] != 'F' || header[8] != 'W' || header[9] != 'A'
				|| header[10] != 'V' || header[11] != 'E') {
			throw new java.io.IOException("Not a WAV file");
		}

		mChannels = 0;
		mSampleRate = 0;
		// while (mOffset + 8 <= mFileSize) {
		while (true) {
			byte[] chunkHeader = new byte[8];
			int read = stream.read(chunkHeader, 0, 8);
			if (read < 0) {
				break;
			}
			// mOffset += 8;

			int chunkLen = ((0xff & chunkHeader[7]) << 24)
					| ((0xff & chunkHeader[6]) << 16)
					| ((0xff & chunkHeader[5]) << 8)
					| ((0xff & chunkHeader[4]));

			if (chunkHeader[0] == 'f' && chunkHeader[1] == 'm'
					&& chunkHeader[2] == 't' && chunkHeader[3] == ' ') {
				if (chunkLen < 16 || chunkLen > 1024) {
					throw new java.io.IOException("WAV file has bad fmt chunk");
				}

				byte[] fmt = new byte[chunkLen];
				stream.read(fmt, 0, chunkLen);
				// mOffset += chunkLen;

				int format = ((0xff & fmt[1]) << 8) | ((0xff & fmt[0]));
				mChannels = ((0xff & fmt[3]) << 8) | ((0xff & fmt[2]));
				mSampleRate = ((0xff & fmt[7]) << 24) | ((0xff & fmt[6]) << 16)
						| ((0xff & fmt[5]) << 8) | ((0xff & fmt[4]));

				if (format != 1) {
					throw new java.io.IOException(
							"Unsupported WAV file encoding");
				}

			} else if (chunkHeader[0] == 'd' && chunkHeader[1] == 'a'
					&& chunkHeader[2] == 't' && chunkHeader[3] == 'a') {
				if (mChannels == 0 || mSampleRate == 0) {
					throw new java.io.IOException(
							"Bad WAV file: data chunk before fmt chunk");
				}

				int frameSamples = (mSampleRate * mChannels) / 50;
				mFrameBytes = frameSamples * 2;

				// mNumFrames = (chunkLen + (mFrameBytes - 1)) / mFrameBytes;

				byte[] buffer = new byte[mFrameBytes];

				int i = 0;
				while (i < chunkLen) {
					int oneFrameBytes = mFrameBytes;
					if (i + oneFrameBytes > chunkLen) {
						i = chunkLen - oneFrameBytes;
					}

					stream.read(buffer, 0, oneFrameBytes);
					// /read bytes to reader
					aRecordService.putValues(buffer, wrappedReader);

					// mOffset += oneFrameBytes;
					i += oneFrameBytes;

				}

			} else {
				stream.skip(chunkLen);
				// mOffset += chunkLen;
			}
		}
		aRecordService.flush(wrappedReader);
	}




}
