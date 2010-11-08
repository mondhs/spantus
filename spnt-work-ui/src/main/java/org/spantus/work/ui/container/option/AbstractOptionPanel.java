package org.spantus.work.ui.container.option;

import javax.swing.JPanel;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.core.beans.I18n;
import org.spantus.work.ui.i18n.I18nFactory;

public abstract class AbstractOptionPanel extends JPanel implements
		SaveableOptionPanel, ReloadableComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498485975926807701L;
	
	SpantusWorkInfo info;

	protected String getMessage(String key){
		return getI18n().getMessage(key);
	}
	protected String getMessage(Enum<?> key){
		if(key == null) return null;
		return getI18n().getMessage(key.name());
	}

	protected I18n getI18n(){
		return I18nFactory.createI18n();
	}
	public abstract void onShowEvent();
	
	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	public SpantusWorkInfo getInfo() {
		return info;
	}
	
	public boolean  isAdvanced(){
		return Boolean.TRUE.equals(getInfo().getEnv().getAdvancedMode());
	}
}
