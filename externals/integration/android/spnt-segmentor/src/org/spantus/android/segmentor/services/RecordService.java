package org.spantus.android.segmentor.services;

import org.spantus.android.segmentor.record.entity.SpantusAudioCtx;
import org.spantus.core.io.BaseWraperExtractorReader;

public interface RecordService {

	void putValues(byte[] buffer, BaseWraperExtractorReader wrappedReader);

	void flush(BaseWraperExtractorReader wrappedReader);

	void record(SpantusAudioCtx ctx, BaseWraperExtractorReader wrappedReader);

	void stopRequest(SpantusAudioCtx ctx);

}
