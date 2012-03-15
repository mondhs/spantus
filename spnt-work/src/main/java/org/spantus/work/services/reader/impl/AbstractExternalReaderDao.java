package org.spantus.work.services.reader.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.reader.ExternalReaderDao;

public abstract class AbstractExternalReaderDao implements ExternalReaderDao {
	public static final Logger LOG = Logger
			.getLogger(AbstractExternalReaderDao.class);

	/**
	 * @return 
	 * 
	 */
	@Override
	public List<File> write(IExtractorInputReader reader, File file) {
		List<File> files = new ArrayList<File>(reader.getExtractorRegister3D().size());
		try {
			for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
				String classFileName = FileUtils.stripExtention(file);
				classFileName = classFileName.replaceAll(
						"-" + extractor.getName(), "");
				String className = constructClassName(classFileName);
				File aFile = newFile(file.getParentFile(), className,
						extractor.getName());
				FileOutputStream fileOut = new FileOutputStream(aFile);
				write(className, extractor, fileOut);
				files.add(aFile);
			}
		} catch (IOException e) {
			LOG.error("Cannot write", e);
			throw new ProcessingException(e);
		}
		return files;
	}

	/**
	 * 
	 * @param parentFile
	 * @param className
	 * @param name
	 * @return
	 */
	protected File newFile(File parentFile, String className, String name) {
		File aFile = new File(parentFile, className + "-" + name + "."
				+ getExtentionName());
		return aFile;
	}

	protected abstract String getExtentionName();

	/**
	 * 
	 * @param classFileName
	 * @return
	 */
	private String constructClassName(String classFileName) {
		String className = classFileName;
		// String[] names = classFileName.split("-");
		// if(names.length > 0){
		// className = names[0];
		// }
		return className;
	}
}
