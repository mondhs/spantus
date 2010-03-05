package org.spnt.recognition.dtw.ui;

import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventListener;
import org.spantus.externals.recognition.ui.RecognitionCmdEnum;
import org.spantus.externals.recognition.ui.RecognitionUIActionListener;

public class RecognitionUIActionListenerImpl implements
		RecognitionUIActionListener, SpantusEventListener {

	RecognitionAppFrame recognitionAppFrame;
	
	public RecognitionUIActionListenerImpl(
			RecognitionAppFrame recognitionAppFrame) {
		super();
		this.recognitionAppFrame = recognitionAppFrame;
	}

	public void start() {
//		recognitionAppFrame.getMainContentPane().add(
//				recognitionAppFrame.getRecognitionPanel().getRecognitionPlot(),BorderLayout.CENTER);
		recognitionAppFrame.getRecognitionPanel().getRecognitionPlot().startRecognition();
	}

	public void stop() {
		recognitionAppFrame.getRecognitionPanel().getRecognitionPlot().stopRecognition();
		recognitionAppFrame.getRecognitionPanel().remove(recognitionAppFrame.getRecognitionPanel().getRecognitionPlot());
		recognitionAppFrame.getRecognitionPanel().setRecognitionPlot(null);
	}

	public void changeLearningStatus(boolean status) {
		recognitionAppFrame.getRecognitionPanel().getRecognitionPlot().setLearnMode(status);
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
