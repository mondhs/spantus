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
//	private RecognitionMonitorPlot recognitionPlot;
	private RecognitionPanel recognitionPanel; 
//	private RecognitionUIActionListener actionListener;
	private SpantusEventMulticaster eventMulticaster;
	

	public RecognitionAppFrame() {
		super();
		this.setTitle("Spantus Speech Recognition");
	}
	
	public void initialize() {
        eventMulticaster = new BasicSpantusEventMulticaster();
        RecognitionUIActionListenerImpl actionListener = new RecognitionUIActionListenerImpl(this);
        eventMulticaster.addListener(actionListener);
        
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
//			new DropTarget(jContentPane, new WavDropTargetListener(getHandler(),getInfo()));
		}
		return jContentPane;
	}
	
//	public RecognitionMonitorPlot getRecognitionPlot(){
//		if (recognitionPlot == null) {
//			recognitionPlot = new RecognitionMonitorPlot();
//			recognitionPlot.setLearnMode(getToolBar().isLearnMode());
//		}
//		return recognitionPlot;
//	}
	public RecognitionToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new RecognitionToolBar(getEventMulticaster());
//			toolBar.setRecognitionUIActionListener(getRecognitionUIActionListener());
//			jJToolBarBar.setInfo(getInfo());
//			jJToolBarBar.setHandler(getHandler());
			
		}
		return toolBar;
	}

//	public RecognitionUIActionListener getRecognitionUIActionListener() {
//		if(actionListener == null){
//		
//		}
//		return actionListener;
//	}
	
//	public void setLearnMode(Boolean learnMode) {
//		if(recognitionPlot != null){
//			getRecognitionPlot().setLearnMode(learnMode);
//		}
//	}

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
