package org.spantus.work.ui.container.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.spantus.logger.Logger;
import org.spantus.ui.SwingUtils;
import org.spantus.work.ui.container.I18nTreeNode;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.option.AbstractOptionPanel;
import org.spantus.work.ui.container.option.ExtractorsOptionPanel;
import org.spantus.work.ui.container.option.GeneralOptionPanel;
import org.spantus.work.ui.container.option.RecognitionOptionPnl;
import org.spantus.work.ui.container.option.SaveableOptionPanel;
import org.spantus.work.ui.container.option.WindowOptionPnl;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;
/**
 * 
 * @author Mindaugas Greibus
 * 
 *
 */
public class OptionDialog extends JDialog implements ReloadableComponent, KeyListener{

	private Logger log = Logger.getLogger(OptionDialog.class);
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JTree jTree = null;
	private JSplitPane jSplitPane = null;
	private JScrollPane jScrollPane = null;
	private JPanel optPanel = null;
	private JPanel buttonPanel = null;
	OptionActionListener listener;
	
	Map<optionsPanelEnum, AbstractOptionPanel> saveablePanels;
//	WindowOptionPnl windowPnl;
//	
//	ExtractorsOptionPanel extractorsOptionPnl;
	
	enum optionsPanelEnum{general, parameters, feature, recognition,spantus	};
	enum optionsLabelEnum{spantus, ok, cancel};
	enum optionsCmdEnum{save, discard};

	
	SpantusWorkInfo info;
	
	/**
	 * @param owner
	 */
	public OptionDialog(Frame owner) {
		super(owner,getMessage("option"),true);
		this.setContentPane(getJContentPane());
		this.setSize(900, 600);
		SwingUtils.centerWindow(this);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		this.setModal(true);
		for (AbstractOptionPanel optionPnl : getSaveablePanels().values()) {
			optionPnl.initialize();
		}
		getJScrollPane().setViewportView(setPanel(getSaveablePanels().get(optionsPanelEnum.values()[0])));
	}

	public void reload() {
		for (AbstractOptionPanel optionPnl : getSaveablePanels().values()) {
			optionPnl.reload();
		}
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
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(),BorderLayout.SOUTH);
			
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getJTree() {
		if (jTree == null) {
			I18nTreeNode root = new I18nTreeNode(optionsLabelEnum.spantus);
//			I18nTreeNode general = new I18nTreeNode(optionsPanelEnum.general);
//			I18nTreeNode parameters = new I18nTreeNode(optionsPanelEnum.parameters);
//			I18nTreeNode feature = new I18nTreeNode(optionsPanelEnum.feature);
//                        I18nTreeNode recognition = new I18nTreeNode(optionsPanelEnum.recogntion);
//			root.add(general);
//			root.add(parameters);
//			root.add(feature);
//                        root.add(recognition);
                        for (optionsPanelEnum pnlEnum : getSaveablePanels().keySet()) {
                            I18nTreeNode node = new I18nTreeNode(pnlEnum);
                            root.add(node);
                        }
                        
			jTree = new JTree(root);
			jTree.addTreeSelectionListener(new OptionTreeSelectionListener());
		}
		return jTree;
	}

	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJTree());
			jSplitPane.setRightComponent(getJScrollPane());
		}
		return jSplitPane;
	}

	private JPanel setPanel(AbstractOptionPanel pnl){
		if(optPanel == null){
			optPanel = new JPanel(new BorderLayout());
		}
		optPanel.removeAll();
		optPanel.add(pnl,BorderLayout.NORTH);
		return optPanel;
	}
	
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane(new JPanel());
		}
		return jScrollPane;
	}
	public class OptionTreeSelectionListener implements TreeSelectionListener{
		
		public void valueChanged(TreeSelectionEvent e) {
			Object selection = ((DefaultMutableTreeNode)e.getPath().getLastPathComponent()).getUserObject();
			optionsPanelEnum panelEntity = optionsPanelEnum.valueOf(selection.toString());
			if(getSaveablePanels().get(panelEntity) != null){
				AbstractOptionPanel abstractOptionPanel = getSaveablePanels().get(panelEntity);
				getJScrollPane().setViewportView(setPanel(abstractOptionPanel));
				abstractOptionPanel.onShowEvent();
			}
		}
		
	}
	public Map<optionsPanelEnum, AbstractOptionPanel> getSaveablePanels() {
		if(saveablePanels == null){
			saveablePanels = new LinkedHashMap<optionsPanelEnum, AbstractOptionPanel>();
			WindowOptionPnl parametersPnl = new WindowOptionPnl();
			ExtractorsOptionPanel extractorsOptionPnl = new ExtractorsOptionPanel();
			GeneralOptionPanel generalOptionPnl = new GeneralOptionPanel();
                        RecognitionOptionPnl recognitionOptionPnl = new RecognitionOptionPnl();

			saveablePanels.put(optionsPanelEnum.general, generalOptionPnl);
			saveablePanels.put(optionsPanelEnum.parameters, parametersPnl);
			saveablePanels.put(optionsPanelEnum.feature, extractorsOptionPnl);
                        saveablePanels.put(optionsPanelEnum.recognition, recognitionOptionPnl);
		}
		return saveablePanels;
	}

	private static String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}


	public JPanel getButtonPanel() {
		if(buttonPanel == null){
			buttonPanel = new JPanel(new FlowLayout());
			JButton btn = new JButton(getMessage(optionsLabelEnum.ok.name()));
			btn.addActionListener(getListener());
			btn.setActionCommand(optionsCmdEnum.save.name());
			buttonPanel.add(btn);
			btn = new JButton(getMessage(optionsLabelEnum.cancel.name()));
			btn.addActionListener(getListener());
			btn.setActionCommand(optionsCmdEnum.discard.name());
			buttonPanel.add(btn);
		}
		return buttonPanel;
	}

	public OptionActionListener getListener() {
		if(listener == null){
			listener = new OptionActionListener();
		}
		return listener;
	}

	public void discard(){
		this.dispose();
		
	}
	public void save(){
		for (SaveableOptionPanel saveable : getSaveablePanels().values()) {
			saveable.save();
		}
		this.dispose();
	}

	
	public class OptionActionListener extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 562563817086762095L;

		
		public void actionPerformed(ActionEvent e) {
			optionsCmdEnum cmd = optionsCmdEnum.valueOf(e.getActionCommand());
			switch (cmd) {
			case save:
				save();
				break;
			case discard:
				discard();
				break;
			default:
				break;
			}
		}
		
	}
	
	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
		for (AbstractOptionPanel optionPnl : getSaveablePanels().values()) {
			optionPnl.setInfo(info);
		}
	}

	//Override
	public void keyPressed(KeyEvent e) {
		// Do nothing
	}

	//Override
	public void keyReleased(KeyEvent e) {
		// Do nothing
	}

	//Override
	public void keyTyped(KeyEvent e) {
		int keyChar = e.getKeyChar();
		log.error("[keyTyped] name ; keyChar" + keyChar);			
	}


}
