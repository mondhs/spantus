package org.spantus.work.ui.container.panel;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.spantus.event.SpantusEventMulticaster;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public abstract  class AbstractSpantusContentPane extends JPanel implements ReloadableComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract JToolBar getToolBar();
	public SpantusEventMulticaster eventMulticaster;
	private SpantusWorkInfo info;
        
	public AbstractSpantusContentPane(SpantusEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
	}
	
	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}

        public void setInfo(SpantusWorkInfo info) {
            this.info = info;
        }
        
        public SpantusWorkInfo getInfo(){
            return info;
        }

}
