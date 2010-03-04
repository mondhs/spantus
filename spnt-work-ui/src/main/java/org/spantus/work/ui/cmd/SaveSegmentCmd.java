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

import java.io.File;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.wav.AudioManagerFactory;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 * Created on Feb 22, 2009
 */
public class SaveSegmentCmd extends AbsrtactCmd{
	
	

	public static final String segmentSavedPanelMessageHeader = "segmentSavedPanelMessageHeader";
	public static final String segmentSavedPanelMessageBody = "segmentSavedPanelMessageBody";
	protected Logger log = Logger.getLogger(getClass());
	
	
	public SaveSegmentCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}
	
	@Override
	public String execute(SpantusWorkInfo ctx) {
		String pathToSaveFormat = ctx.getProject().getFeatureReader().getWorkConfig().getAudioPathOutput()+
		"/{0}_{1}.wav";
		MarkerSet words = 
		ctx.getProject().getSample().getMarkerSetHolder().getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
//		StringBuilder sb = new StringBuilder();
		for (Marker marker : words.getMarkers()) {
			String path = MessageFormat.format(pathToSaveFormat,ctx.getProject().getExperimentId(), marker.getLabel());
			AudioManagerFactory.createAudioManager().save(
					ctx.getProject().getSample().getCurrentFile(), 
					marker.getStart()/1000f,
					marker.getLength()/1000f,
					path
					);
//			sb.append(path).append("\n");
		}
		String pathToSave = new File(ctx.getProject().getFeatureReader().getWorkConfig().getAudioPathOutput()).getAbsolutePath();
		pathToSave += "/"+ctx.getProject().getExperimentId() + "_*.wav";
		showMessage(words, pathToSave, ctx);
		
		return null;
	}
	
	protected void showMessage(MarkerSet words, String pathToSave, SpantusWorkInfo ctx){
		String messageFormat = getMessage(segmentSavedPanelMessageBody);
		String messageBody = MessageFormat.format(messageFormat, 
				words.getMarkers().size(),
				pathToSave
				);
		log.info(messageBody);
		if(Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())){
			JOptionPane.showMessageDialog(null,messageBody,
							getMessage(segmentSavedPanelMessageHeader),
							JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
}
