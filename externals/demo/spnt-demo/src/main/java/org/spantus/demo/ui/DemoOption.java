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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.i18n.I18nFactory;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.19
 *
 */
public class DemoOption extends JDialog {

	enum DemoOptionActionEnum{ ok, cancel, selectAll, deselectAll };
	
	Logger log = Logger.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private DemoOptionReaderPnl extractorChoise = null;
	private JPanel buttonPnl = null;
	private JButton okBtn = null;
	private JButton cancelBtn = null;
	private ActionListener actionListener;
	private IDemoAppletListener appletListener;


	public IDemoAppletListener getAppletListener() {
		return appletListener;
	}

	public void setAppletListener(IDemoAppletListener appletListener) {
		this.appletListener = appletListener;
	}

	public DemoAppletInfo getInfo() {
		return getExtractorChoise().getInfo();
	}

	public void setInfo(DemoAppletInfo info) {
		getExtractorChoise().setInfo(info);
	}

	/**
	 * @param owner
	 */
	public DemoOption(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		DemoSwingUtils.centerWindow(this);
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
			jContentPane.add(getButtonPnl(), BorderLayout.SOUTH);
			jContentPane.add(getExtractorChoise(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	
	/**
	 * This method initializes extractorChoise	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private DemoOptionReaderPnl getExtractorChoise() {
		if (extractorChoise == null) {
			extractorChoise = new DemoOptionReaderPnl();
		}
		return extractorChoise;
	}

	/**
	 * This method initializes buttonPnl	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPnl() {
		if (buttonPnl == null) {
			buttonPnl = new JPanel();
			buttonPnl.setLayout(new BoxLayout(getButtonPnl(), BoxLayout.X_AXIS));
			buttonPnl.add(getOkBtn(), null);
			buttonPnl.add(getCancelBtn(), null);
		}
		return buttonPnl;
	}

	/**
	 * This method initializes okBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkBtn() {
		if (okBtn == null) {
			okBtn = new JButton();
			okBtn.setActionCommand(DemoOptionActionEnum.ok.name());
			okBtn.setText(I18nFactory.createI18n().getMessage(
					DemoOptionActionEnum.ok.name()));
			okBtn.addActionListener(getActionListener());
		}
		return okBtn;
	}

	/**
	 * This method initializes cancelBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelBtn() {
		if (cancelBtn == null) {
			cancelBtn = new JButton();
			cancelBtn.setText(I18nFactory.createI18n().getMessage(
					DemoOptionActionEnum.cancel.name()));
			cancelBtn.setActionCommand(DemoOptionActionEnum.cancel.name());
			cancelBtn.addActionListener(getActionListener());

		}
		return cancelBtn;
	}

	public ActionListener getActionListener() {
		if(actionListener == null){
			actionListener = new DemoOptionActionListener();
		}
		return actionListener;
	}
	
	
	class DemoOptionActionListener implements ActionListener{

		
		public void actionPerformed(ActionEvent e) {
			DemoOptionActionEnum cmd = DemoOptionActionEnum.valueOf(e.getActionCommand());
			switch (cmd) {
			case ok:
				getExtractorChoise().read();
				setVisible(false);
				if(getAppletListener() != null){
					getAppletListener().preferencesChanged(getInfo());
				}
				break;
			case cancel:
				setVisible(false);
				break;
			default:
				break;
			}
			
		}
		
	}

	
	
	public void setVisible(boolean b) {
		super.setVisible(b);
		if(b=true){
			getExtractorChoise().onShow();
		}
	}
}
