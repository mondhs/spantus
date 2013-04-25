package org.spantus.android.segmentor.record.entity;

public class SpantusAudioCtx {

	private RecordState recordState = RecordState.STOPED;
	private Long srartedOn;
	private Long samplesProcessed;
	private ExtractorReaderCtx readerCtx;

	public Long getSrartedOn() {
		return srartedOn;
	}

	public void setSrartedOn(Long srartedOn) {
		this.srartedOn = srartedOn;
	}

	public Long getSamplesProcessed() {
		return samplesProcessed;
	}

	public void setSamplesProcessed(Long samplesProcessed) {
		this.samplesProcessed = samplesProcessed;
	}

	public RecordState getRecordState() {
		return recordState;
	}

	public void setRecordState(RecordState recordState) {
		synchronized (recordState) {
			this.recordState = recordState;
		}
	}

	@Override
	public String toString() {
		return "SpntAudioContext [recordState=" + recordState + "]";
	}

	public ExtractorReaderCtx getReaderCtx() {
		return readerCtx;
	}

	public void setReaderCtx(ExtractorReaderCtx readerCtx) {
		this.readerCtx = readerCtx;
	}



}
