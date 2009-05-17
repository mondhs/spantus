package org.spnt.recognition.dtw.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RecognitionAppFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JPanel jContentPane;
	RecognitionToolBar toolBar;

	public RecognitionAppFrame() {
		super();
		this.setTitle("Spantus Speech Recognition");
	}
	
	public void initialize() {
		this.setContentPane(getJContentPane());	
		getToolBar().initialize();
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getToolBar(), BorderLayout.NORTH);
//			jContentPane.add(getSampleRepresentationPanel(),BorderLayout.CENTER);
//			new DropTarget(jContentPane, new WavDropTargetListener(getHandler(),getInfo()));
		}
		return jContentPane;
	}
	
	public RecognitionToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new RecognitionToolBar();
//			jJToolBarBar.setInfo(getInfo());
//			jJToolBarBar.setHandler(getHandler());
			
		}
		return toolBar;
	}
}
