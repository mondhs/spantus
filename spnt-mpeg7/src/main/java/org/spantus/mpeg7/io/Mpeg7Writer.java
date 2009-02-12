package org.spantus.mpeg7.io;

import java.io.File;
import java.net.URL;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.exception.ProcessingException;
import org.w3c.dom.Document;

public interface Mpeg7Writer {
	public void write(URL inUrl, File outFile,IExtractorConfig conf) throws ProcessingException;
	public void write(Document mpeg7doc, File outFile) throws ProcessingException;
}
