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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

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

	public static final String segmentAutoPanelMessageHeader = "segmentAutoPanelMessageHeader";
	public static final String segmentAutoPanelMessageBody = "segmentAutoPanelMessageBody";
	
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
		if(threasholds == null || threasholds.size() == 0){
			log.debug("Auto segmentaiton was not processed as there is no data.");
			return null;
		}
		MarkerSet value = segmentator.extractSegments(threasholds, param);
		ctx.getProject().getCurrentSample().getMarkerSetHolder()
				.getMarkerSets().put(MarkerSetHolderEnum.word.name(), value);
		putLabels(ctx);
		
		String messageFormat = getMessage(segmentAutoPanelMessageBody);
		String messageBody = MessageFormat.format(messageFormat, 
				value.getMarkers().size()
				);
		
		log.info(messageBody);
		
		if(Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())){
			JOptionPane.showMessageDialog(null,messageBody,
					getMessage(segmentAutoPanelMessageHeader),
					JOptionPane.INFORMATION_MESSAGE);	
		}
		
		
		return GlobalCommands.sample.reloadSampleChart.name();
	}
	
	protected String getDescriptionFileName(String wavFile){
		String txtFile = wavFile;
		if(new File(txtFile + ".txt").isFile()){
			return txtFile + ".txt";
		}
		Pattern pattern = Pattern.compile("(.*)(\\.)(.*)");
		Matcher matcher = pattern.matcher(txtFile);
		if(matcher.matches()){
			txtFile = matcher.replaceAll("$1"+".txt");
			if(new File(txtFile).isFile()){
				return txtFile;
			}
		}
		return null;
		
	}
	
	public void putLabels(SpantusWorkInfo ctx){
		String filePath = getDescriptionFileName(
				ctx.getProject().getCurrentSample().getCurrentFile().getFile()
				);
		List<String> words = null;
		if(filePath == null){
			log.debug("marker description file not found for " 
					+ ctx.getProject().getCurrentSample().getCurrentFile().getFile());
			return;
		}

		List<Marker> markers = ctx.getProject().getCurrentSample().getMarkerSetHolder().getMarkerSets().get(
				MarkerSetHolderEnum.word.name()).getMarkers();

		try{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String str = null;
			words = new ArrayList<String>();
			//Assume it is multi line format
			while((str = reader.readLine()) != null){
				words.add(str);
			}
			//if only single one line is selected, assume it is single line format
			if(words.size() == 1 && markers.size()>1){
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
		
		if(words == null){
			for (Marker marker : markers) {
				marker.setLabel(Integer.valueOf(i+1).toString());
				i++;
			}
		}else{
			for (Marker marker : markers) {
				if(i>=words.size()) break;
				marker.setLabel(words.get(i));
				i++;
			}
		}
		return;
	}
}
