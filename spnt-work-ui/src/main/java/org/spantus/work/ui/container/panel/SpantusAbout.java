package org.spantus.work.ui.container.panel;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.i18n.I18nResourcesEnum;
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
public class SpantusAbout extends JDialog {

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
	public SpantusAbout(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(503, 215);
		SpantusWorkSwingUtils.centerWindow(this);
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
					I18nResourcesEnum.smallLogo.getCode())));
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
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
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
	private JEditorPane getJEditorPane() {
		if (jEditorPane == null) {
			jEditorPane = new JEditorPane();
			jEditorPane.setEditable(false);
			jEditorPane.setContentType("text/html");
			jEditorPane.setText(I18nFactory.createI18n().getMessage(
					I18nResourcesEnum.appletAboutHtml.getCode()));
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


}
