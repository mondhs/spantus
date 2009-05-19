package org.spnt.recognition.dtw.ui;

public interface RecognitionUIActionListener {
	public void start();
	public void stop();
	public void changeLearningStatus(boolean status);
}
