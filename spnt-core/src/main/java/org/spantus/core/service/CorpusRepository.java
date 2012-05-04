package org.spantus.core.service;

import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.SignalSegment;

public interface CorpusRepository {
	public Collection<SignalSegment> findAllEntries();
	
	public Long count();

	public SignalSegment save(SignalSegment entry);

	public SignalSegment update(SignalSegment entry);

	public SignalSegment update(SignalSegment corpusEntry,
			AudioInputStream audioStream);

	public SignalSegment delete(String id);

	public String findAudioFileById(String id);

	public void flush();

	/**
	 * 
	 * @param label
	 * @param featureDataMap
	 * @return
	 */
	public SignalSegment create(String label,
			Map<String, IValues> featureDataMap);
}
