package org.spantus.exp.recognition.domain;

import java.util.Calendar;
import java.util.Date;

import org.spantus.core.domain.Entity;

public class QSegmentExp extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Date timeStamp;
	
	private String wavFilePath;

	private String markerLabel;

	private String corpusEntryName;

	private String manualName;

	private Long proceessTime;
	private Long length;
	private Long start;
	private Float loudness;
	private Float spectralFlux;
	private Float plp;
	private Float lpc;
	private Float mfcc;
	private Float signalEntropy;
	
	private String loudnessLabel;
	private String spectralFluxLabel;
	private String plpLabel;
	private String lpcLabel;
	private String mfccLabel;
	private String signalEntropyLabel;

	public QSegmentExp(String wavFilePath, Long start, Long length, String markerLabel,
			String corpusEntryName, String manualName, Long proceessTime,
			String loudnessLabel, Float loudness, 
			String spectralFluxLabel, Float spectralFlux,
			String plpLabel, Float plp,
			String lpcLabel, Float lpc,
			String mfccLabel, Float mfcc,
			String signalEntropyLabel, Float signalEntropy) {
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
		this.start = start;
		this.timeStamp = Calendar.getInstance().getTime();
		this.loudnessLabel=loudnessLabel;
		this.spectralFluxLabel=spectralFluxLabel;
		this.plpLabel=plpLabel;
		this.lpcLabel=lpcLabel;
		this.mfccLabel=mfccLabel;
		this.signalEntropyLabel=signalEntropyLabel;
	}
	
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
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}

	public String getLoudnessLabel() {
		return loudnessLabel;
	}

	public void setLoudnessLabel(String loudnessLabel) {
		this.loudnessLabel = loudnessLabel;
	}

	public String getSpectralFluxLabel() {
		return spectralFluxLabel;
	}

	public void setSpectralFluxLabel(String spectralFluxLabel) {
		this.spectralFluxLabel = spectralFluxLabel;
	}

	public String getPlpLabel() {
		return plpLabel;
	}

	public void setPlpLabel(String plpLabel) {
		this.plpLabel = plpLabel;
	}

	public String getLpcLabel() {
		return lpcLabel;
	}

	public void setLpcLabel(String lpcLabel) {
		this.lpcLabel = lpcLabel;
	}

	public String getMfccLabel() {
		return mfccLabel;
	}

	public void setMfccLabel(String mfccLabel) {
		this.mfccLabel = mfccLabel;
	}

	public String getSignalEntropyLabel() {
		return signalEntropyLabel;
	}

	public void setSignalEntropyLabel(String signalEntropyLabel) {
		this.signalEntropyLabel = signalEntropyLabel;
	}

}
