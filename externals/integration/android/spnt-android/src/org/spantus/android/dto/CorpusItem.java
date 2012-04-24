package org.spantus.android.dto;

import java.io.Serializable;
import java.util.Date;

public class CorpusItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4774528822366768216L;
	private String id;
	private String fileName;
	private Date created;
	private String description;
	private Long fileSize;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Long getFileSize() {
		return fileSize;
	}
}
