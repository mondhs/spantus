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

import java.text.MessageFormat;

import javax.swing.JOptionPane;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.ui.audio.AudioManagerFactory;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 * Created on Feb 22, 2009
 */
public class SaveSegmentCmd extends AbsrtactCmd{
	
	public static final String segmentSavedPanelMessageHeader = "segmentSavedPanelMessageHeader";
	public static final String segmentSavedPanelMessageBody = "segmentSavedPanelMessageBody";

	@Override
	public String execute(SpantusWorkInfo ctx) {
		String pathToSavePattern = ctx.getProject().getFeatureReader().getWorkConfig().getAudioPathOutput()+
		"/{0}.wav";
		MarkerSet words = 
		ctx.getProject().getCurrentSample().getMarkerSetHolder().getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		StringBuilder sb = new StringBuilder();
		for (Marker marker : words.getMarkers()) {
			String path = MessageFormat.format(pathToSavePattern, marker.getLabel());
			AudioManagerFactory.createAudioManager().save(
					ctx.getProject().getCurrentSample().getCurrentFile(), 
					marker.getStart()/1000f,
					marker.getLength()/1000f,
					path
					);
			sb.append(path).append("\n");
		}
		String messageFormat = getMessage(segmentSavedPanelMessageBody);
		String messageBody = MessageFormat.format(messageFormat, 
				words.getMarkers().size(),
				sb.toString()
				);
		JOptionPane.showMessageDialog(null,messageBody,
							getMessage(segmentSavedPanelMessageHeader),
							JOptionPane.INFORMATION_MESSAGE);
		return null;
	}
}
