package org.spantus.server.servlet.service;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.spantus.server.dto.CorporaEntry;
import org.spantus.server.dto.CorporaEntryList;

import com.mongodb.gridfs.GridFSDBFile;

public interface SpntEchoRepository {

	public abstract CorporaEntryList findAll();
	/**
	 * 
	 * @param lastEvent
	 * @return
	 * @throws FileNotFoundException
	 */
	public abstract InputStream findLastEventAudio(Long lastEvent)
			throws FileNotFoundException;

	/**
	 * 
	 * @return
	 */
	public abstract Long findLastEvent();
	/**
	 * 
	 * @param audioIn
	 */
	public abstract CorporaEntry store(AudioInputStream audioIn);
	/**
	 * 
	 * @param audioIn
	 */
	public abstract CorporaEntry store(InputStream audioIn);
	/**
	 * 
	 * @param inputStream
	 * @param audioFormat
	 * @param requiredAudioFormat
	 * @return
	 */
	public abstract CorporaEntry store(InputStream inputStream, AudioFormat audioFormat, AudioFormat requiredAudioFormat);
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	public abstract CorporaEntry findById(String id);
	public abstract GridFSDBFile findOutputById(String id);
	public abstract CorporaEntry delete(String id);
	public abstract CorporaEntry update(String id, String description);


}