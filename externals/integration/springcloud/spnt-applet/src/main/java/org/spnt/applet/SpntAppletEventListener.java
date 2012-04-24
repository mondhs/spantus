package org.spnt.applet;

import javax.sound.sampled.AudioInputStream;

public interface SpntAppletEventListener {
	
	public void setConnectionStatus(boolean status);
	public void play(AudioInputStream ais);
	public void play(String filename);
	public void showStatus(String msg);
	public void finish();
	public void updateMeterChangedValue();
	public void resetMeterChangedValue();

}
