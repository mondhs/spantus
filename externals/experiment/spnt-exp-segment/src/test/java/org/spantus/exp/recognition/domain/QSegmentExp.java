package org.spantus.exp.recognition.domain;

import org.spantus.core.domain.Entity;

public class QSegmentExp extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public QSegmentExp(String wavFilePath, Long length, String markerLabel,
			String corpusEntryName, String manualName, Long proceessTime,
			Float loudness, Float spectralFlux, Float plp, Float lpc,
			Float mfcc, Float signalEntropy) {
		super();
		this.wavFilePath = wavFilePath;
		this.markerLabel = markerLabel;
		this.corpusEntryName = corpusEntryName;
		this.manualName = manualName;
		this.proceessTime = proceessTime;
		this.loudness = loudness;
		this.spectralFlux = spectralFlux;
		this.plp = plp;
		this.lpc = lpc;
		this.mfcc = mfcc;
		this.signalEntropy = signalEntropy;
		this.length = length;
	}
	private String wavFilePath;

	private String markerLabel;

	private String corpusEntryName;

	private String manualName;

	private Long proceessTime;
	private Long length;
	private Float loudness;
	private Float spectralFlux;
	private Float plp;
	private Float lpc;
	private Float mfcc;
	private Float signalEntropy;

	public String getWavFilePath() {
		return wavFilePath;
	}
	public void setWavFilePath(String wavFilePath) {
		this.wavFilePath = wavFilePath;
	}
	public String getMarkerLabel() {
		return markerLabel;
	}
	public void setMarkerLabel(String markerLabel) {
		this.markerLabel = markerLabel;
	}
	public String getCorpusEntryName() {
		return corpusEntryName;
	}
	public void setCorpusEntryName(String corpusEntryName) {
		this.corpusEntryName = corpusEntryName;
	}
	public String getManualName() {
		return manualName;
	}
	public void setManualName(String manualName) {
		this.manualName = manualName;
	}
	public Long getProceessTime() {
		return proceessTime;
	}
	public void setProceessTime(Long proceessTime) {
		this.proceessTime = proceessTime;
	}
	public Float getLoudness() {
		return loudness;
	}
	public void setLoudness(Float loudness) {
		this.loudness = loudness;
	}
	public Float getSpectralFlux() {
		return spectralFlux;
	}
	public void setSpectralFlux(Float spectralFlux) {
		this.spectralFlux = spectralFlux;
	}
	public Float getPlp() {
		return plp;
	}
	public void setPlp(Float plp) {
		this.plp = plp;
	}
	public Float getLpc() {
		return lpc;
	}
	public void setLpc(Float lpc) {
		this.lpc = lpc;
	}
	public Float getMfcc() {
		return mfcc;
	}
	public void setMfcc(Float mfcc) {
		this.mfcc = mfcc;
	}
	public Float getSignalEntropy() {
		return signalEntropy;
	}
	public void setSignalEntropy(Float signalEntropy) {
		this.signalEntropy = signalEntropy;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}

}
