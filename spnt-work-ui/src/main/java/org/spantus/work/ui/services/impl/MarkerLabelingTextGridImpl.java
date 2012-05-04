package org.spantus.work.ui.services.impl;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorTextGridMapImpl;
import org.spantus.utils.StringUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.services.MarkerLabeling;

public class MarkerLabelingTextGridImpl implements MarkerLabeling{

	private static final String TEXT_GRID = ".TextGrid";
	CorpusEntryExtractorTextGridMapImpl corpusEntryExtractorTextGridMapImpl;
	
	public MarkerSetHolder label(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx, IExtractorInputReader reader) {
		File textFilePath = getDescriptionFileName(ctx.getProject().getSample().getCurrentFile());
		if(textFilePath == null){
			return null;
		}
		putLabels(textFilePath, markerSetHolder);
		return null;
	}
	
	/**
	 * 
	 * @param textFilePath
	 * @param markerSetHolder
	 * @return
	 */
	public MarkerSetHolder putLabels(File textFilePath, MarkerSetHolder markerSetHolder) {

		for (MarkerSet markerSet : markerSetHolder.getMarkerSets().values()) {
			for (Marker marker : markerSet.getMarkers()) {
				String label = getCorpusEntryExtractorTextGridMapImpl()
					.createLabelFromTextGrid(textFilePath, marker);
				if(StringUtils.hasText(label)){
					marker.setLabel(label);
				}
			}
		}
		return markerSetHolder;
	}
	
	/**
	 * 
	 * @param wavFile
	 * @return
	 */
	protected File getDescriptionFileName(URL wavFile) {
		String txtFileStr = wavFile.getFile();
		File txtFile = new File(txtFileStr + TEXT_GRID);
		if (txtFile.isFile()) {
			return txtFile;
		}
		Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
		Matcher matcher = pattern.matcher(txtFileStr);
		if (matcher.matches()) {
			txtFileStr = matcher.replaceAll("$1" + TEXT_GRID);
			txtFile = new File(txtFileStr);
			if (txtFile.isFile()) {
				return txtFile;
			}
		}
		return null;

	}


	public CorpusEntryExtractorTextGridMapImpl getCorpusEntryExtractorTextGridMapImpl() {
		if(corpusEntryExtractorTextGridMapImpl == null){
			corpusEntryExtractorTextGridMapImpl = new CorpusEntryExtractorTextGridMapImpl();
		}
		return corpusEntryExtractorTextGridMapImpl;
	}


	public void setCorpusEntryExtractorTextGridMapImpl(
			CorpusEntryExtractorTextGridMapImpl corpusEntryExtractorTextGridMapImpl) {
		this.corpusEntryExtractorTextGridMapImpl = corpusEntryExtractorTextGridMapImpl;
	}

	@Override
	public void update(SpantusWorkProjectInfo project,
			ProcessedFrameLinstener listener) {
		// TODO Auto-generated method stub
		
	}

}
