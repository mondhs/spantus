package org.spantus.work.ui.container;

import javax.swing.tree.DefaultMutableTreeNode;

import org.spantus.work.ui.i18n.I18nFactory;

public class I18nTreeNode extends DefaultMutableTreeNode {
	
	public I18nTreeNode(Object userObject) {
		super(userObject, true);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String toString() {
		if (userObject == null) {
			return null;
		} else {
			return getMessage(userObject.toString());
		}
	}
	public String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}
}
