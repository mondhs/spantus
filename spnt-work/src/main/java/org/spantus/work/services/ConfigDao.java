package org.spantus.work.services;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorConfig;

public interface ConfigDao {
	public void write(IExtractorConfig config, File file);
	public void write(IExtractorConfig config, OutputStream outputStream);
	public IExtractorConfig read(File file);
	public IExtractorConfig read(InputStream inputStream);
	public IExtractorConfig read(File file, AudioFormat format);

}
