package org.spantus.work.ui.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.AutoSegmentationCmd;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.MarkerLabeling;

public class MarkerLabelingBaseImpl implements MarkerLabeling {
	
	private static Logger log = Logger.getLogger(AutoSegmentationCmd.class);

	public MarkerSetHolder label(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx, IExtractorInputReader reader) {
		String textFilePath = getDescriptionFileName(ctx.getProject().getSample().getCurrentFile());
		if(textFilePath == null){
			return null;
		}
		return putLabels(markerSetHolder, textFilePath);
	}

	/**
	 * 
	 * @param wavFile
	 * @return
	 */
	protected String getDescriptionFileName(URL wavFile) {
		String txtFile = wavFile.getFile();
		if (new File(txtFile + ".txt").isFile()) {
			return txtFile + ".txt";
		}
		Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
		Matcher matcher = pattern.matcher(txtFile);
		if (matcher.matches()) {
			txtFile = matcher.replaceAll("$1" + ".txt");
			if (new File(txtFile).isFile()) {
				return txtFile;
			}
		}
		return null;

	}

	
	/**
	 * 
	 * @param ctx
	 */
	public MarkerSetHolder putLabels(MarkerSetHolder markerSetHolder, String textFilePath) {
		
		List<String> words = null;
		if (textFilePath == null) {
			log.debug("marker description file not found for {0}"
					, textFilePath);
			return null;
		}

		MarkerSet markerSet = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		if(markerSet == null){
			return null;
		}
		List<Marker> markers = markerSet.getMarkers();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(textFilePath));
			String str = null;
			words = new ArrayList<String>();
			// Assume it is multi line format
			while ((str = reader.readLine()) != null) {
				words.add(str);
			}
			reader.close();
			// if only single one line is selected, assume it is single line
			// format
			if (words.size() == 1 && markers.size() > 1) {
				String wordsString = words.get(0);
				String[] strs = wordsString.split("\\s");
				words.clear();
				for (String strsVal : strs) {
					words.add(strsVal);
				}
			}
		} catch (IOException e) {
			log.error(e);
		}
		int i = 0;

		if (words == null) {
			for (Marker marker : markers) {
				marker.setLabel(Integer.valueOf(i + 1).toString());
				i++;
			}
		} else {
			for (Marker marker : markers) {
				if (i >= words.size())
					break;
				marker.setLabel(words.get(i));
				i++;
			}
		}
		return markerSetHolder;
	}
}
