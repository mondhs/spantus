package org.spantus.externals.recognition.services.impl;

import java.io.File;
import java.text.MessageFormat;

import org.spantus.core.marker.Marker;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;

public class CorpusEntryExtractorSpntMapImpl extends
		CorpusEntryExtractorTextGridMapImpl {
	public String createLabel(File filePath, Marker marker, int result) {
		String markersPath = FileUtils.replaceExtention(filePath,".mspnt.xml");
		String text = createLabelFromTextGrid(new File(getMarkerDir(), markersPath), marker);
		if(!StringUtils.hasText(text)){
			return MessageFormat.format("{0}-{1}-{2}", marker.getLabel().trim(),
					filePath.getName(), (result + 1)).toString();
		}
		return text;
	}
}
