package org.spantus.externals.recognition.ui;

public enum RecognitionCmdEnum {
	learn, stopLearn, save, play, stop, record;
	
	public static boolean isCmd(String val){
		valueOf(val);
		return true;
	}
}
