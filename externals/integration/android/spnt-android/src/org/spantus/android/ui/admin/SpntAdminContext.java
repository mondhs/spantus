package org.spantus.android.ui.admin;

import java.io.Serializable;

import org.spantus.android.dto.CorpusItem;

public class SpntAdminContext implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8640745978282946081L;

	private String spantusServerApi = "http://spantus.cloudfoundry.com/api/corpora";

	private CorpusItem corpusItem;
	
	private String deletedKey;
	
	private boolean plaingInd;

	public boolean isPlaingInd() {
		return plaingInd;
	}
	public void setPlaingInd(boolean plaingInd) {
		this.plaingInd = plaingInd;
	}
	public String getSpantusServerApi() {
		return spantusServerApi;
	}
	public void setSpantusServerApi(String spantusServerApi) {
		this.spantusServerApi = spantusServerApi;
	}
	public void setCorpusItem(CorpusItem corpusItem) {
		this.corpusItem = corpusItem;
		
	}
	public CorpusItem getCorpusItem() {
		return corpusItem;
	}
	public String getDeletedKey() {
		return deletedKey;
	}
	public void setDeletedKey(String deletedKey) {
		this.deletedKey = deletedKey;
	}
}
