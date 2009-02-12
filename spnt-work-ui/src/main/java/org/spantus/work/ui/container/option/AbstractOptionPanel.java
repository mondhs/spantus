package org.spantus.work.ui.container.option;

import javax.swing.JPanel;

import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.i18n.I18n;
import org.spantus.work.ui.i18n.I18nFactory;

public abstract class AbstractOptionPanel extends JPanel implements
		SaveableOptionPanel, ReloadableComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498485975926807701L;

	protected String getMessage(String key){
		return getI18n().getMessage(key);
	}
	protected I18n getI18n(){
		return I18nFactory.createI18n();
	}
}
