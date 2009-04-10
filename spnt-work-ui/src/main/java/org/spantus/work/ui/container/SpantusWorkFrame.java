package org.spantus.work.ui.container;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.MainHandler;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.container.panel.SampleRepresentationPanel;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.i18n.ImageResourcesEnum;
import org.spantus.work.ui.services.SpantusUIServiceImpl;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;

public class SpantusWorkFrame extends JFrame implements ReloadableComponent{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private SpantusWorkMenuBar jJMenuBar = null;
	private SpantusWorkToolbar jJToolBarBar = null;
	private SpantusWorkInfo info = null;
	private MainHandler handler = null;
	private SampleRepresentationPanel sampleRepresentationPanel;
	private Logger log= Logger.getLogger(SpantusWorkFrame.class);
	
	private SpantusUIServiceImpl spantusUIService;
	private WorkInfoManager workInfoManager;
	
	/**
	 * This is the default constructor
	 */
	public SpantusWorkFrame() {
		super();
		this.setTitle("Spantus");
		this.setIconImage(new ImageIcon(
				this.getClass().getClassLoader().getResource(
						ImageResourcesEnum.spntIcon.getCode()
						)
				).getImage());
		addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		    	  saveEnv();
		      }
		    });
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		getSpantusUIService().setupEnv(getInfo(),this);
		getJJMenuBar().initialize();
		getJJToolBarBar().initialize();
		getSampleRepresentationPanel().initialize();
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane());
		contructTitle();
		log.info("Application started");
	}
	
	public void saveEnv(){
		getSpantusUIService().saveEnv(getInfo(),this);
		getWorkInfoManager().saveWorkInfo(getInfo());
		log.info("Application stoped");
	}
	public void reload() {
		getJJMenuBar().reload();
		getJJToolBarBar().reload();
		contructTitle();
		this.setTitle(contructTitle());
	}
	
	protected String contructTitle(){
		String version = getMessage("spantus.work.ui.version");
		String projectType = getMessage("spantus.work.ui.project.type." + getInfo().getProject().getCurrentType());
		String fileName = "";
		if(getInfo().getProject().getCurrentSample().getCurrentFile() != null){
			fileName = getInfo().getProject().getCurrentSample().getCurrentFile().getFile();
		}
		String title = MessageFormat.format(getMessage("spantus.work.ui.title.format"), 
				version,
				projectType,
				fileName);
		return title;
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
			jContentPane.add(getSampleRepresentationPanel(),BorderLayout.CENTER);
			new DropTarget(jContentPane, new WavDropTargetListener(getHandler(),getInfo()));
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private SpantusWorkMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			SpantusWorkMenuBar spantusWorkMenuBar = new SpantusWorkMenuBar();
			spantusWorkMenuBar.setInfo(getInfo());
			spantusWorkMenuBar.setHandler(getHandler());
			jJMenuBar = spantusWorkMenuBar;
		}
		return jJMenuBar;
	}
	
	
	public SpantusWorkToolbar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			jJToolBarBar = new SpantusWorkToolbar();
//			demoToolBar.setActionListener(getListener());
			jJToolBarBar.setInfo(getInfo());
			jJToolBarBar.setHandler(getHandler());
			
		}
		return jJToolBarBar;
	}

	

	public SpantusWorkInfo getInfo() {
		if(info == null){
			info = getWorkInfoManager().openWorkInfo();
			I18nFactory.createI18n(info);
		}
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	public SpantusWorkCommand getHandler() {
		if(handler == null){
			handler = new MainHandler();
			handler.initialize(this);
		}
		return handler;
	}
	

	public SampleRepresentationPanel getSampleRepresentationPanel() {
		if (sampleRepresentationPanel == null) {
			sampleRepresentationPanel = new SampleRepresentationPanel();
			sampleRepresentationPanel.setInfo(getInfo());
			sampleRepresentationPanel.setHandler(getHandler());
		}
		return sampleRepresentationPanel;
	}

	public void setSampleRepresentationPanel(
			SampleRepresentationPanel sampleRepresentationPanel) {
		this.sampleRepresentationPanel = sampleRepresentationPanel;
	}
	public String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}

	public SpantusUIServiceImpl getSpantusUIService() {
		if(spantusUIService == null){
			spantusUIService = new SpantusUIServiceImpl();
		}
		return spantusUIService;
	}

	

	public WorkInfoManager getWorkInfoManager() {
		if(workInfoManager == null){
			workInfoManager = WorkUIServiceFactory.createInfoManager();
		}
		return workInfoManager;
	}
	
	
}
