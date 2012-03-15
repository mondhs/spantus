package org.spantus.work.services.reader.impl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.spantus.core.extractor.IExtractorVector;

public class WekaArffDaoImpl extends AbstractExternalReaderDao {

	/**
	 * 
	 */
	@Override
	public void write(String className, IExtractorVector extractor,
			OutputStream outputStream) {
		PrintWriter out = new PrintWriter(outputStream, true);
		String name = extractor.getName();
		out.printf("%% %1$2s\n\n", name);
		out.printf("\n@RELATION %1$2s\n", name);
		int dimention = extractor.getOutputValues().getDimention();
		for (int i = 0; i < dimention; i++) {
			out.printf("@ATTRIBUTE feature%1d  NUMERIC\n", i);
		}
		out.printf("@ATTRIBUTE class   {%s}", className);
		out.printf("\n@Data\n");
		for (List<Double> list : extractor.getOutputValues()) {
			String separator = "";
			for (Double double1 : list) {
				out.printf(Locale.ENGLISH, "%s %#f", separator, double1);
				separator = ",";
			}
			out.printf(Locale.ENGLISH, "%s %s\n", separator, className);
		}
		out.close();
	}
	/**
	 * 
	 */
	@Override
	protected String getExtentionName() {
		return "arff";
	}

}
