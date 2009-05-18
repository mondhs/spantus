package org.spnt.recognition.dtw.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.spantus.work.ui.AbstractSegmentPlot;
import org.spnt.recognition.dtw.exec.RecognitionMonitorPlot;
import org.spnt.recognition.segment.RecordRecognitionSegmentatorOnline;

public class RecognitionAppFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel jContentPane;
	private RecognitionToolBar toolBar;
	private RecognitionMonitorPlot recognitionPlot;
	private RecognitionUIActionListener actionListener;
	

	public RecognitionAppFrame() {
		super();
		this.setTitle("Spantus Speech Recognition");
	}
	
	public void initialize() {
		this.setContentPane(getMainContentPane());	
		getToolBar().initialize();
	}
	
	public JPanel getMainContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getToolBar(), BorderLayout.NORTH);
//			jContentPane.add(getRecognitionPlot(),BorderLayout.CENTER);
//			new DropTarget(jContentPane, new WavDropTargetListener(getHandler(),getInfo()));
		}
		return jContentPane;
	}
	
	public RecognitionMonitorPlot getRecognitionPlot(){
		if (recognitionPlot == null) {
			recognitionPlot = new RecognitionMonitorPlot();
			recognitionPlot.setLearnMode(getToolBar().isLearnMode());
		}
		return recognitionPlot;
	}
	public void setRecognitionPlot(RecognitionMonitorPlot recognitionPlot) {
		this.recognitionPlot = recognitionPlot;
	}
	public RecognitionToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new RecognitionToolBar();
			toolBar.setRecognitionUIActionListener(getRecognitionUIActionListener());
//			jJToolBarBar.setInfo(getInfo());
//			jJToolBarBar.setHandler(getHandler());
			
		}
		return toolBar;
	}

	public RecognitionUIActionListener getRecognitionUIActionListener() {
		if(actionListener == null){
			actionListener = new RecognitionUIActionListenerImpl(this);
		}
		return actionListener;
	}
	
	public void setLearnMode(Boolean learnMode) {
		if(recognitionPlot != null){
			getRecognitionPlot().setLearnMode(learnMode);
		}
	}
	
}
