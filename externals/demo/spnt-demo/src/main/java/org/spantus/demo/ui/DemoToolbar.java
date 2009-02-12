/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import org.spantus.demo.cmd.DemoGlobalCommands;
import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.dto.SampleDto;
import org.spantus.demo.i18n.I18nFactory;
import org.spantus.demo.services.ServiceFactory;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class DemoToolbar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox sampleCmb = null;
	private JButton loadBtn = null;
//	private JButton playBtn = null;
	private JButton aboutBtn = null;
//	private JButton optionsBtn = null;
	private ActionListener actionListener;
	private ActionListener toolbarActionListener;
	private DemoAppletInfo info;
	
	public DemoAppletInfo getInfo() {
		return info;
	}

	public void setInfo(DemoAppletInfo info) {
		this.info = info;
	}

	/**
	 * This method initializes 
	 * 
	 */
	public DemoToolbar() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.add(getSampleCmb());
        this.add(getLoadBtn());
//        this.add(getPlayBtn());
//        this.add(getOptionsBtn());
        this.add(getAboutBtn());
	}
	private boolean getSelected(){
		return getSampleCmb().getSelectedItem() != null;
	}
	/**
	 * This method initializes sampleCmb	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getSampleCmb() {
		if (sampleCmb == null) {
			sampleCmb = new JComboBox();
			List<SampleDto> samples = ServiceFactory.createSamplesService().getSamples();
			sampleCmb.setModel(new SampleComboBoxModel(samples));
			sampleCmb.setRenderer(new SampleComboBoxRenderer());
			sampleCmb.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {
					if(getSelected()){
						getInfo().setCurrentSample((SampleDto)getSampleCmb().getSelectedItem());
//						getPlayBtn().setEnabled(getInfo().getCurrentSample().isSamplePlayable());
					}
//					else{
//						getPlayBtn().setEnabled(false);	
//					}

					getLoadBtn().setEnabled(getSelected());
					
				}});
		}
		return sampleCmb;
	}

	/**
	 * This method initializes loadBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoadBtn() {
		if (loadBtn == null) {
			loadBtn = new JButton();
			loadBtn.setText(I18nFactory.createI18n().getMessage(
					DemoGlobalCommands.load.name()));
			loadBtn.setActionCommand(DemoGlobalCommands.options.name());//before load show option popup
			loadBtn.addActionListener(getToolbarActionListener());
			loadBtn.setEnabled(getSelected());
		}
		return loadBtn;
	}

	/**
	 * This method initializes playBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
//	private JButton getPlayBtn() {
//		if (playBtn == null) {
//			playBtn = new JButton();
//			playBtn.setText(I18nFactory.createI18n().getMessage(
//					DemoGlobalCommands.play.name()));
//			playBtn.setActionCommand(DemoGlobalCommands.play.name());
//			playBtn.addActionListener(getToolbarActionListener());
//			playBtn.setEnabled(getSelected());
//
//		}
//		return playBtn;
//	}

	/**
	 * This method initializes aboutBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAboutBtn() {
		if (aboutBtn == null) {
			aboutBtn = new JButton();
			aboutBtn.setText(I18nFactory.createI18n().getMessage(
					DemoGlobalCommands.about.name()));
			aboutBtn.setActionCommand(DemoGlobalCommands.about.name());
			aboutBtn.addActionListener(getToolbarActionListener());
		}
		return aboutBtn;
	}
	/*
	private JButton getOptionsBtn() {
		if (optionsBtn == null) {
			optionsBtn = new JButton();
			optionsBtn.setText(I18nFactory.createI18n().getMessage(
					DemoGlobalCommands.options.name()));
			optionsBtn.setActionCommand(DemoGlobalCommands.options.name());
			optionsBtn.addActionListener(getToolbarActionListener());
		}
		return optionsBtn;
	}
	*/
	private ActionListener getToolbarActionListener() {
		if(toolbarActionListener == null){
			toolbarActionListener = new DemoToolbarActionListener();
		}
		return toolbarActionListener;
	}

	public ActionListener getActionListener() {
		if(actionListener == null){
			//this shoulbe not called. normali action listener should be set by cotnainer
			actionListener = new DemoToolbarActionListener();
		}
		return actionListener;
	}
	

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}
	
	class DemoToolbarActionListener implements ActionListener{
		//This should be overriden by container
		public void actionPerformed(ActionEvent e) {
			DemoGlobalCommands cmd = DemoGlobalCommands.valueOf(e.getActionCommand());
			switch (cmd) {
			case load:
				e.setSource(getSampleCmb().getSelectedItem());
				break;
			case options:
				e.setSource(getSampleCmb().getSelectedItem());
				break;
				
			default:
				break;
			}
			getActionListener().actionPerformed(e);
			
		}
		
	}
}
