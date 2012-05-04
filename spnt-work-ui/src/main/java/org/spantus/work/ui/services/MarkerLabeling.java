package org.spantus.work.ui.services;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;

public interface MarkerLabeling {
	/**
	 * Labeling given marker set holders
	 * @param markerSetHolder
	 * @param wavFile
	 * @return
	 */
	public MarkerSetHolder label(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx, IExtractorInputReader reader);
	public void update(SpantusWorkProjectInfo project, ProcessedFrameLinstener listener);
}
