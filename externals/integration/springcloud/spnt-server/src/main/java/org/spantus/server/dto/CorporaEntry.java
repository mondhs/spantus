package org.spantus.server.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mysema.query.annotations.QueryEntity;

@Document
@QueryEntity
@XmlRootElement(name = "CorporaEntry")
public class CorporaEntry {

	@Id
	private ObjectId objectId;
	private Long timeStamp;
	private String description;
	private String fileName;
	private Date created;
	private Long fileSize;
	private Float lengthInSec;
	private Float sampleRate;
	private Integer channels;
	private Integer sampleSizeInBits;

	public CorporaEntry() {
		super();
		this.objectId = new ObjectId();
	}

	public CorporaEntry(Long timeStamp, String fileName) {
		this();
		this.timeStamp = timeStamp;
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String get_id() {
		return getObjectId().toString();
	}
	
	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || !getClass().equals(obj.getClass())) {
			return false;
		}

		CorporaEntry that = (CorporaEntry) obj;

		return objectId == null ? false : this.objectId.equals(that.objectId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return 17 + (objectId == null ? 0 : objectId.hashCode());
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStap) {
		this.timeStamp = timeStap;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLengthInSec(Float lengthInSec) {
		this.lengthInSec=lengthInSec;
	}

	public Float getLengthInSec() {
		return lengthInSec;
	}

	public void setSampleRate(Float sampleRate) {
		this.sampleRate = sampleRate;
	}

	public Float getSampleRate() {
		return sampleRate;
	}

	public void setChannels(Integer channels) {
		this.channels = channels;
	}

	public void setSampleSizeInBits(Integer sampleSizeInBits) {
		this.sampleSizeInBits = sampleSizeInBits;
	}

	public Integer getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public Integer getChannels() {
		return channels;
	}


}
