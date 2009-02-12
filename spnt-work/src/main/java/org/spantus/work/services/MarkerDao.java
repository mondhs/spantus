package org.spantus.work.services;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.spantus.core.marker.MarkerSetHolder;

public interface MarkerDao {
	public void write(MarkerSetHolder holder, File file);
	public void write(MarkerSetHolder holder, OutputStream outputStream);
	public MarkerSetHolder read(File file);
	public MarkerSetHolder read(InputStream inputStream);
}
