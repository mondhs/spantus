package org.spantus.work.ui.container.panel;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.spantus.work.ui.container.ReloadableComponent;

public abstract  class AbstractSpantusContentPane extends JPanel implements ReloadableComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract JToolBar getToolBar();

}
