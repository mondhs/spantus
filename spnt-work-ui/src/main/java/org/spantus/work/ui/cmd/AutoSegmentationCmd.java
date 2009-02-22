/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.work.ui.cmd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IThreshold;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created Aug 26, 2008
 * 
 */
public class AutoSegmentationCmd extends AbsrtactCmd {

	private ISegmentatorService segmentator;
	private SampleChart sampleChart;

	protected Logger log = Logger.getLogger(getClass());
	
	public AutoSegmentationCmd(SampleChart sampleChart) {
		segmentator = SegmentFactory.createSegmentator();
		this.sampleChart = sampleChart;

	}

	public String execute(SpantusWorkInfo ctx) {
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader().getWorkConfig();
		IExtractorInputReader reader = sampleChart.getReader();
		if(reader == null){
			log.info("Nothing to segment");
		}
		reader.getExtractorRegister();
		Set<IThreshold> threasholds = new HashSet<IThreshold>();
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if (extractor instanceof IThreshold) {
				threasholds.add((IThreshold) extractor);
			}
		}
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(config.getSegmentationMinLength().longValue());
		param.setMinSpace(config.getSegmentationMinSpace().longValue());
		param.setExpandEnd(config.getSegmentationExpandEnd().longValue());
		param.setExpandStart(config.getSegmentationExpandStart().longValue());
		MarkerSet value = segmentator.extractSegments(threasholds, param);
		ctx.getProject().getCurrentSample().getMarkerSetHolder()
				.getMarkerSets().put(MarkerSetHolderEnum.word.name(), value);
		putLabels(ctx);
		return GlobalCommands.sample.reloadSampleChart.name();
	}
	
	public void putLabels(SpantusWorkInfo ctx){
		String filePath = ctx.getProject().getCurrentSample().getCurrentFile().getFile();
		filePath += ".txt";
		List<String> words = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String str = null;
			words = new ArrayList<String>();
			while((str = reader.readLine()) != null){
				words.add(str);
			}
			
		} catch (FileNotFoundException e) {
			log.debug("marker description file not found: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(words == null) return;
		List<Marker> markers = ctx.getProject().getCurrentSample().getMarkerSetHolder().getMarkerSets().get(
				MarkerSetHolderEnum.word.name()).getMarkers();
		int i = 0;
		for (Marker marker : markers) {
			if(i>=words.size()) break;
			marker.setLabel(words.get(i));
			i++;
		}

	}
}
