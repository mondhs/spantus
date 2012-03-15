package org.spantus.work.services.reader.impl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.spantus.core.extractor.IExtractorVector;

public class CsvDaoImpl extends AbstractExternalReaderDao {

	@Override
	public void write(String className, IExtractorVector extractor,
			OutputStream outputStream) {
		PrintWriter out = new PrintWriter(outputStream, true);
		
		int dimention = extractor.getOutputValues().getDimention();
		String separator = "";
		for (int i = 0; i < dimention; i++) {
			out.printf("%s feature%1d", separator, i);
			separator=",";
		}
		out.printf(Locale.ENGLISH, "%s class\n", separator);
		
		for (List<Double> list : extractor.getOutputValues()) {
			String iterSeparator = "";
			for (Double double1 : list) {
				out.printf(Locale.ENGLISH, "%s %#f", iterSeparator, double1);
				iterSeparator = ",";
			}
			out.printf(Locale.ENGLISH, "%s %s\n", iterSeparator, className);
		}
		out.close();
		
	}

	@Override
	protected String getExtentionName() {
		return "csv";
	}

	

}
