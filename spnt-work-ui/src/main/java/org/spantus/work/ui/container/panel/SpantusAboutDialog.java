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
package org.spantus.work.ui.container.panel;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.i18n.HtmlResourcesEnum;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.i18n.ImageResourcesEnum;
/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Jun 11, 2008
 *
 */
public class SpantusAboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JButton jButton = null;
	private JScrollPane jScrollPane = null;
	private JEditorPane jEditorPane = null;
	private JPanel jPanel1 = null;

	/**
	 * @param owner
	 */
	public SpantusAboutDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		this.setSize(SpantusWorkSwingUtils.currentWindowSize(0.5, 0.25));
		SpantusWorkSwingUtils.centerWindow(this);
		setTitle(I18nFactory.createI18n().getMessage("about"));
		this.setContentPane(getJContentPane());
	}

	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = super.createRootPane();
		rootPane.registerKeyboardAction(new AboutActionListener(), stroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
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
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel = new JLabel();
			jLabel.setIcon(new ImageIcon(getClass().getResource(
					ImageResourcesEnum.smallLogo.getCode())));
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(jLabel, BorderLayout.WEST);
			jPanel.add(getJPanel1(), BorderLayout.SOUTH);
			jPanel.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Ok");
			jButton.addActionListener(new AboutActionListener());
		}
		return jButton;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane.setViewportView(getJEditorPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jEditorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	protected JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setEditable(false);
			jEditorPane.setContentType("text/html");
			jEditorPane.setText(I18nFactory.createI18n().getMessage(
					HtmlResourcesEnum.appletAboutHtml.name()));
			jEditorPane.setCaretPosition(0);
		}
		return jEditorPane;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			jPanel1 = new JPanel();
			jPanel1.setLayout(flowLayout);
			jPanel1.setComponentOrientation(ComponentOrientation.UNKNOWN);
			jPanel1.add(getJButton(), null);
		}
		return jPanel1;
	}
	
	class AboutActionListener implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			dispose();
		}
	}

	protected JLabel getjLabel() {
		return jLabel;
	}

}
