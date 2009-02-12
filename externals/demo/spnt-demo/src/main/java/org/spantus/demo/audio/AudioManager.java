package org.spantus.demo.audio;

import java.net.URL;

public interface AudioManager {
	public void play(URL file);
	public void play(URL file, float starts, float length);
}
