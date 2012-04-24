package org.spantus.android;

import java.io.InputStream;


public interface SpntAppletEventListener {
	
	public void setConnectionStatus(boolean status);
	public void play(InputStream inputStream);
	public void showStatus(String msg);
	public void finish();
	public void updateMeterChangedValue();
	public void resetMeterChangedValue();
	public void runOnUiThread(Runnable runnable);

}
