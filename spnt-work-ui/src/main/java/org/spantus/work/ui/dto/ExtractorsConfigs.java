package org.spantus.work.ui.dto;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class ExtractorsConfigs {

	Map<String, ExtractorParam> extractorParmas;

	protected Map<String, ExtractorParam> getExtractorParmas() {
		if (extractorParmas == null) {
			extractorParmas = new HashMap<String, ExtractorParam>();
		}
		return extractorParmas;
	}

}
