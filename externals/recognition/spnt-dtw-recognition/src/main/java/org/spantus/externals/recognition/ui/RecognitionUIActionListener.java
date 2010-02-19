package org.spantus.externals.recognition.ui;

public interface RecognitionUIActionListener {
	public void start();
	public void stop();
	public void changeLearningStatus(boolean status);
}
