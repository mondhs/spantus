/**
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
package org.spantus.demo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.spantus.demo.audio.AudioManagerFactory;
import org.spantus.demo.cmd.RecordCmd;
import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.services.ReadersEnum;
import org.spantus.demo.ui.RecordToolbar;
import org.spantus.demo.ui.SpantusAbout;
import org.spantus.demo.ui.chart.SampleChart;
import org.spantus.extractor.impl.ExtractorEnum;

public class SpantusRecordApplet extends JApplet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
    private JToolBar jJToolBarBar = null;
    private SampleChart sampleChart = null;
    private AppletEventListener listener = null;
    private SpantusAbout about = null;
    private DemoAppletInfo info;
    private RecordCmd recordCmd;


	public enum RecordAppletGlobalCommands {
        record, play, stop, about
    };

    public void init() {
        this.setSize(600, 400);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJJToolBarBar(), BorderLayout.NORTH);
            jContentPane.add(getSampleChart(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jJToolBarBar	
     * 	
     * @return javax.swing.JToolBar	
     */
    private JToolBar getJJToolBarBar() {
        if (jJToolBarBar == null) {
            RecordToolbar toolBar = new RecordToolbar();
            toolBar.setActionListener(getListener());
            toolBar.setInfo(getInfo());
            jJToolBarBar = toolBar;

        }
        return jJToolBarBar;
    }

    private SampleChart getSampleChart() {
        if (sampleChart == null) {
            sampleChart = new SampleChart();
            sampleChart.setInfo(getInfo());
        }
        return sampleChart;
    }

    public DemoAppletInfo getInfo() {
        if (info == null) {
            info = new DemoAppletInfo();
            info.getCurrentReader().setReader(ReadersEnum.common);
            info.getCurrentReader().getExtractors().add(ExtractorEnum.WAVFORM_EXTRACTOR.name());
        }
        return info;
    }

    private AppletEventListener getListener() {
        if (listener == null) {
            listener = new AppletEventListener();
        }
        return listener;
    }

    private SpantusAbout getAboutPnl() {
        if (about == null) {
            about = new SpantusAbout((Frame) SwingUtilities.getAncestorOfClass(Frame.class, this));
        }
        return about;
    }
    public RecordCmd getRecordCmd() {
    	if(recordCmd == null){
    		recordCmd = new RecordCmd(getSampleChart(), getInfo());
    	}
		return recordCmd;
	}

    class AppletEventListener implements ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent e) {
            RecordAppletGlobalCommands cmd = RecordAppletGlobalCommands.valueOf(e.getActionCommand());
            switch (cmd) {
                case about:
                    getAboutPnl().setVisible(true);
                    break;
                case record:
                	getRecordCmd().execute();
                    break;
                case stop:
                	getInfo().setRecording(Boolean.FALSE);
                    break;
                case play:
                    if (getInfo().isSampleLoaded() && getInfo().getCurrentSample().isSamplePlayable()) {
                        AudioManagerFactory.createAudioManager().play(getInfo().getCurrentSample().getUrl(),
                                getInfo().getFrom(),
                                getInfo().getLength());
                    }
                    break;
                default:
                    throw new RuntimeException("Not implemented: " + cmd);
            }

        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SpantusDemoApplet t = new SpantusDemoApplet();
        frame.add(t);
        t.init();                // simulate browser call(1)    
        frame.setSize(400, 600);           // Set the size of the frame
        frame.setVisible(true);           // Show the frame
    }
}
