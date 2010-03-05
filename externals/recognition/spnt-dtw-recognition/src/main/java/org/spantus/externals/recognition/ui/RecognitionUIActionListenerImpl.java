package org.spantus.externals.recognition.ui;

import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventListener;

public class RecognitionUIActionListenerImpl implements
		RecognitionUIActionListener, SpantusEventListener {

	RecognitionPanel recognitionAppFrame;
	
	public RecognitionUIActionListenerImpl(
			RecognitionPanel recognitionAppFrame) {
		super();
		this.recognitionAppFrame = recognitionAppFrame;
	}

	public void start() {
//		recognitionAppFrame.getMainContentPane().add(
//				recognitionAppFrame.getRecognitionPanel().getRecognitionPlot(),BorderLayout.CENTER);
		recognitionAppFrame.getRecognitionPlot().startRecognition();
	}

	public void stop() {
		recognitionAppFrame.getRecognitionPlot().stopRecognition();
		recognitionAppFrame.remove(recognitionAppFrame.getRecognitionPlot());
		recognitionAppFrame.setRecognitionPlot(null);
	}

	public void changeLearningStatus(boolean status) {
		recognitionAppFrame.getRecognitionPlot().setLearnMode(status);
	}

	public void onEvent(SpantusEvent event) {
		
		RecognitionCmdEnum recCmd = RecognitionCmdEnum.valueOf(event.getCmd());
		switch (recCmd) {
		case record:
			this.start();
			break;
		case stop:
			this.stop();
			break;
//		case train:
//			learnMode = ((JCheckBox)e.getSource()).isSelected();
//			getRecognitionUIActionListener().changeLearningStatus(learnMode);
//			break;
//		case admin:
//			getRecognitionUIActionListener().stop();
//			
//			JDialog adminDialog = new JDialog(
//					(Frame)getParent().getParent().getParent().getParent());
//			adminDialog.setContentPane(new AdminPanel());
//			adminDialog.setSize(640,480);
//			adminDialog.setModal(true);
//			adminDialog.setVisible(true);
//			
//			break;
		default:
			break;
		}
//		
	}

}
