package org.spnt.recognition.dtw.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.spantus.event.BasicSpantusEventMulticaster;
import org.spantus.event.SpantusEventMulticaster;
import org.spantus.externals.recognition.ui.RecognitionPanel;
import org.spantus.externals.recognition.ui.RecognitionToolBar;

public class RecognitionAppFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel jContentPane;
	private RecognitionToolBar toolBar;
	private RecognitionPanel recognitionPanel; 
	private SpantusEventMulticaster eventMulticaster;
	

	public RecognitionAppFrame() {
		super();
		this.setTitle("Spantus Speech Recognition");
	}
	
	public void initialize() {
                eventMulticaster = new BasicSpantusEventMulticaster();
		this.setContentPane(getMainContentPane());	
		getToolBar().initialize();
		getRecognitionPanel().initialize();
	}
	
	public JPanel getMainContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getToolBar(), BorderLayout.NORTH);
			jContentPane.add(getRecognitionPanel(),BorderLayout.CENTER);
		}
		return jContentPane;
	}
	

	public RecognitionToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new RecognitionToolBar(getEventMulticaster());
		}
		return toolBar;
	}



	public RecognitionPanel getRecognitionPanel() {
		if(recognitionPanel == null){
			recognitionPanel = new RecognitionPanel(getEventMulticaster()); 
		}
		return recognitionPanel;
	}

	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}

	public void setEventMulticaster(SpantusEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
	}

	
}
