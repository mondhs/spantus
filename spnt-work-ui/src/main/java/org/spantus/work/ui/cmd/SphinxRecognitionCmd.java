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

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.exception.ProcessingException;
import org.spantus.externals.recognition.sphinx.SphinxRecognitionServiceImpl;
import org.spantus.logger.Logger;
import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 *
 *
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 *
 * Created Aug 26, 2008
 *
 */
public class SphinxRecognitionCmd extends AbsrtactCmd {

    public static final String segmentAutoPanelMessageHeader = "segmentAutoPanelMessageHeader";
    public static final String segmentAutoPanelMessageBody = "segmentAutoPanelMessageBody";
    private static final Logger log = Logger.getLogger(SphinxRecognitionCmd.class);

    private SphinxRecognitionServiceImpl recognitionServiceImpl;
    
    public SphinxRecognitionCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }

    public Set<String> getExpectedActions() {
        return createExpectedActions(GlobalCommands.tool.sphinxRecognition);
    }

    /**
     *
     */
    public String execute(SpantusWorkInfo ctx) {
        URL currentFile = ctx.getProject().getSample().getCurrentFile();
        
        AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(currentFile);
			MarkerSetHolder markerSetHolder = getRecognitionServiceImpl().recognize(ais, currentFile.getFile());
			ctx.getProject().getSample().setMarkerSetHolder(markerSetHolder);
			inform(ctx.getProject().getSample().getMarkerSetHolder(), ctx);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
        
        

        

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
        String messageBody = MessageFormat.format(messageFormat, markerSet.getMarkers().size());

        log.info(messageBody);

        if (Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())) {
            JOptionPane.showMessageDialog(null, messageBody,
                    getMessage(segmentAutoPanelMessageHeader),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

	public SphinxRecognitionServiceImpl getRecognitionServiceImpl() {
		if(recognitionServiceImpl == null){
			recognitionServiceImpl = new SphinxRecognitionServiceImpl();
		}
		return recognitionServiceImpl;
	}




}
