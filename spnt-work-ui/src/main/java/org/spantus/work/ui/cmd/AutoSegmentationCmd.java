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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.logger.Logger;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.SegmentFactory;
import org.spantus.segment.SegmentatorParam;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.services.MarkerLabeling;
import org.spantus.work.ui.services.impl.MarkerLabelingProxyImpl;

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

	private MarkerLabeling markerLabeling;

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
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader()
				.getWorkConfig();
		
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
						.getMarkerSets()
						.put(markerSet.getMarkerSetType(), markerSet);
				classifiers.add((IClassifier) extractor);
			}
		}

		if (classifiers != null && classifiers.size() > 0) {
			SegmentatorParam param = createSegmentatorParam(config);
			// create special segmentator
			ISegmentatorService segmentator = SegmentFactory
					.createSegmentator(ctx.getProject().getFeatureReader()
							.getWorkConfig().getSegmentationServiceType());
			MarkerSetHolder markerSetHolder = segmentator.extractSegments(
					classifiers, param);
			ctx.getProject().getSample().setMarkerSetHolder(markerSetHolder);
			
			
			getMarkerLabeling().update(ctx.getProject(), getExecutionFacade());
			getMarkerLabeling().label(
						ctx.getProject().getSample().getMarkerSetHolder(),
						ctx, getReader());

		}
		if (markerSet == null) {
			log.debug("Auto segmentaiton was not processed as there is no data.");
			return null;
		}

		inform(ctx.getProject().getSample().getMarkerSetHolder(), ctx);

		return GlobalCommands.sample.reloadSampleChart.name();
	}

	/**
	 * 
	 * @param value
	 * @param ctx
	 */
	protected void inform(MarkerSetHolder markerSetHolder, SpantusWorkInfo ctx) {
		MarkerSet markerSet = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		// if word level does not exist, check for phone level
		if (markerSet == null) {
			markerSet = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.phone.name());
		}
		String messageFormat = getMessage(segmentAutoPanelMessageBody);
		String messageBody = MessageFormat.format(messageFormat, markerSet
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



	public MarkerLabeling getMarkerLabeling() {
		if(markerLabeling == null){
			markerLabeling = new MarkerLabelingProxyImpl(getExecutionFacade());
		}
		return markerLabeling;
	}

	public void setMarkerLabeling(MarkerLabeling markerLabeling) {
		this.markerLabeling = markerLabeling;
	}

}
