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
package org.spantus.demo.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.spantus.demo.SpantusRecordApplet.RecordAppletGlobalCommands;
import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.i18n.I18nFactory;
import org.spantus.utils.Assert;

public class RecordToolbar extends JToolBar {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton recordBtn = null;
    private JButton stopBtn = null;
    private JButton playBtn = null;
    private JButton aboutBtn = null;
    private ActionListener actionListener;
    private ActionListener toolbarActionListener;
    private DemoAppletInfo info;

    public RecordToolbar() {
        initialize();
    }

    private void initialize() {
        this.add(getRecordBtn());
        this.add(getStopBtn());
        this.add(getPlayBtn());
        this.add(getAboutBtn());
    }

    private JButton getRecordBtn() {
        if (recordBtn == null) {
            recordBtn = createButton(RecordAppletGlobalCommands.record);
//            recordBtn.setEnabled(getSelected());
        }
        return recordBtn;
    }

    private JButton getAboutBtn() {
        if (aboutBtn == null) {
            aboutBtn = createButton(RecordAppletGlobalCommands.about);
        }
        return aboutBtn;
    }
     private JButton getPlayBtn() {
        if (playBtn == null) {
            playBtn = createButton(RecordAppletGlobalCommands.play);
        }
        return playBtn;
    }
     private JButton getStopBtn() {
        if (stopBtn == null) {
            stopBtn = createButton(RecordAppletGlobalCommands.stop);
        }
        return stopBtn;
    }

    

    public DemoAppletInfo getInfo() {
        return info;
    }

    public void setInfo(DemoAppletInfo info) {
        this.info = info;
    }

    public ActionListener getActionListener() {
        Assert.isTrue(actionListener != null);
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    private ActionListener getToolbarActionListener() {
        if (toolbarActionListener == null) {
            toolbarActionListener = new RecordToolbarActionListener();
        }
        return toolbarActionListener;
    }
    protected JButton createButton(RecordAppletGlobalCommands cmd) {
        JButton btn = new JButton();
        btn.setText(I18nFactory.createI18n().getMessage(
                cmd.name()));
        btn.setActionCommand(cmd.name());
        btn.addActionListener(getToolbarActionListener());
        return btn;
    }

    class RecordToolbarActionListener implements ActionListener {
        //This should be overriden by container
        public void actionPerformed(ActionEvent e) {
//			RecordAppletGlobalCommands cmd = RecordAppletGlobalCommands.valueOf(e.getActionCommand());
//			switch (cmd) {
//			case load:
//				e.setSource(getSampleCmb().getSelectedItem());
//				break;
//			case options:
//				e.setSource(getSampleCmb().getSelectedItem());
//				break;
//				
//			default:
//				break;
//			}
            getActionListener().actionPerformed(e);

        }
    }
}