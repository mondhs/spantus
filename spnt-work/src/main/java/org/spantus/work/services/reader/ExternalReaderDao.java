package org.spantus.work.services.reader;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;

public interface ExternalReaderDao {
	public List<File> write(IExtractorInputReader reader, File file);
	public void write(String className, IExtractorVector extractor, OutputStream outputStream);

}
