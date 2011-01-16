/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
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
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.spantus.core.IValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentatorParam;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.services.MatchingServiceImpl;

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
	protected static Logger log = Logger.getLogger(AutoSegmentationCmd.class);

        private MatchingServiceImpl matchingService;
        private ExtractorReaderService extractorReaderService;


	public AutoSegmentationCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.tool.autoSegmentation);
	}
	/**
	 * 
	 */
	public String execute(SpantusWorkInfo ctx) {
                getMatchingService().update(ctx); 
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader()
				.getWorkConfig();
                boolean autoRecognize = (Boolean.TRUE.equals(ctx.getEnv().getAutoRecognition()));
		IExtractorInputReader reader = getReader();
		if (reader == null) {
			log.info("Nothing to segment");
		}
		reader.getExtractorRegister();
		MarkerSet markerSet = null;
		Set<IClassifier> classifiers = new HashSet<IClassifier>();
		for (IExtractor extractor : reader.getExtractorRegister()) {
			if (extractor instanceof IClassifier) {
				markerSet = ((IClassifier) extractor).getMarkSet();
				ctx.getProject().getSample().getMarkerSetHolder()	
						.getMarkerSets().put(markerSet.getMarkerSetType(),
								markerSet);
				classifiers.add((IClassifier)extractor);
//				markerSet = ((IClassifier)extractor).getMarkSet();
//				markerSet.setMarkerSetType(MarkerSetHolderEnum.word.name());
//				ctx.getProject().getCurrentSample().getMarkerSetHolder().getMarkerSets().put(markerSet.getMarkerSetType(), markerSet);
			}
		}

		if (classifiers != null && classifiers.size() > 0) {
			SegmentatorParam param = createSegmentatorParam(config);
			//create special segmentator
			ISegmentatorService segmentator = SegmentFactory.createSegmentator(
					ctx.getProject().getFeatureReader().getWorkConfig().getSegmentationServiceType());
			MarkerSetHolder markerSetHolder = segmentator.extractSegments(classifiers, param);
			ctx.getProject().getSample().setMarkerSetHolder(markerSetHolder);
			markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
			//if word level does not exist, check for phone level
			if(markerSet == null){
				markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
			}
                        //chek if needed try read from file or recognize segments
                        if(autoRecognize){
                            putLabelsRecognized(ctx);
                        }else{
                            putLabels(ctx);
                        }

		}
		if (markerSet == null) {
			log.debug("Auto segmentaiton was not processed as there is no data.");
			return null;
		}

		inform(markerSet, ctx);

		return GlobalCommands.sample.reloadSampleChart.name();
	}

	/**
	 * 
	 * @param value
	 * @param ctx
	 */
	protected void inform(MarkerSet value, SpantusWorkInfo ctx) {
		String messageFormat = getMessage(segmentAutoPanelMessageBody);
		String messageBody = MessageFormat.format(messageFormat, value
				.getMarkers().size());

		log.info(messageBody);

		if (Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())) {
			JOptionPane.showMessageDialog(null, messageBody,
					getMessage(segmentAutoPanelMessageHeader),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * 
	 * @param config
	 * @return
	 */
	protected SegmentatorParam createSegmentatorParam(
			WorkUIExtractorConfig config) {
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinLength(config.getSegmentationMinLength().longValue());
		param.setMinSpace(config.getSegmentationMinSpace().longValue());
		param.setExpandEnd(config.getSegmentationExpandEnd().longValue());
		param.setExpandStart(config.getSegmentationExpandStart().longValue());
		return param;
	}

	/**
	 * 
	 * @param wavFile
	 * @return
	 */
	protected String getDescriptionFileName(String wavFile) {
		String txtFile = wavFile;
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

        public void putLabelsRecognized(SpantusWorkInfo ctx) {
            MarkerSet markerSet = ctx.getProject().getSample()
		.getMarkerSetHolder().getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
            if(markerSet == null){
            	markerSet = ctx.getProject().getSample()
        		.getMarkerSetHolder().getMarkerSets().get(
        				MarkerSetHolderEnum.phone.name());

            }
            for (Marker marker : markerSet.getMarkers()) {
            	//work around for non-phoneme markers 
            	if(marker.getLength()<20){
            		continue;
            	}
                Map<String, IValues> fvv = getExtractorReaderService().
                        findAllVectorValuesForMarker(getReader(), marker);
                RecognitionResult result = getMatchingService().match(fvv);
                if(result!=null){
                    marker.setLabel(result.getInfo().getName());
                }
            }
        }

	/**
	 * 
	 * @param ctx
	 */
	public void putLabels(SpantusWorkInfo ctx) {
		String filePath = getDescriptionFileName(ctx.getProject()
				.getSample().getCurrentFile().getFile());
		List<String> words = null;
		if (filePath == null) {
			log.debug("marker description file not found for "
					+ ctx.getProject().getSample().getCurrentFile()
							.getFile());
			return;
		}

		MarkerSet markerSet = ctx.getProject().getSample()
		.getMarkerSetHolder().getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		if(markerSet == null){
			return;
		}
		List<Marker> markers = markerSet.getMarkers();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String str = null;
			words = new ArrayList<String>();
			// Assume it is multi line format
			while ((str = reader.readLine()) != null) {
				words.add(str);
			}
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
		return;
	}
        public MatchingServiceImpl getMatchingService() {
            if (matchingService == null) {
                matchingService = MatchingServiceImpl.getInstance();
            }
            return matchingService;
        }

        public void setMatchingService(MatchingServiceImpl matchingService) {
            this.matchingService = matchingService;
        }
        public ExtractorReaderService getExtractorReaderService() {
            if (extractorReaderService == null) {
                extractorReaderService = WorkServiceFactory.createExtractorReaderService();
            }
            return extractorReaderService;
        }

        public void setExtractorReaderService(ExtractorReaderService extractorReaderService) {
            this.extractorReaderService = extractorReaderService;
        }


}
