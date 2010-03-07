package org.spantus.externals.recognition.ui;

public enum RecognitionCmdEnum {
	learn, stopLearn, save, play, stop, record;
	
	public static boolean isCmd(String val){
		try{
			valueOf(val);
		}catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
}
