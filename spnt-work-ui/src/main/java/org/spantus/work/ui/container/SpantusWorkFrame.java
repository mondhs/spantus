package org.spantus.work.ui.container;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.spantus.externals.recognition.ui.AdminPanel;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.MainHandler;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.container.panel.SampleRepresentationPanel;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.i18n.ImageResourcesEnum;
import org.spantus.work.ui.services.SpantusUIServiceImpl;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;

public class SpantusWorkFrame extends JFrame implements ReloadableComponent{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private SpantusWorkMenuBar jJMenuBar = null;
	private SpantusWorkToolbar spantusToolBar = null;
	private SpantusWorkInfo info = null;
	private MainHandler handler = null;
	private SampleRepresentationPanel sampleRepresentationPanel;
	private Logger log= Logger.getLogger(SpantusWorkFrame.class);
	
	private SpantusUIServiceImpl spantusUIService;
	private WorkInfoManager workInfoManager;
	private String version = null;

	
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
//		this.setContentPane(getJContentPane());
		getJJMenuBar().initialize();
//		getToolBar().initialize();
//		getSampleRepresentationPanel().initialize();
		this.setJMenuBar(getJJMenuBar());
		newProject();
		log.info("Application started");
	}
	
	public void saveEnv(){
		getSpantusUIService().saveEnv(getInfo(),this);
		getWorkInfoManager().saveWorkInfo(getInfo());
		log.info("Application stoped");
	}
	/**
	 * 
	 */
	public void newProject() {
//		setJMenuBar(null);
		getContentPane().removeAll();
		if(jContentPane!=null){
			getJContentPane().removeAll();
			jContentPane=null;
		}
		sampleRepresentationPanel = null;
		if(ProjectTypeEnum.recognition.name().equals(
				getInfo().getProject().getType())){
			setContentPane(getRecognitionContentPane());
		}else{
			setContentPane(getJContentPane());
//			getJJMenuBar().initialize();
			getToolBar().initialize();
			getSampleRepresentationPanel().initialize();
			this.setJMenuBar(getJJMenuBar());
			this.setContentPane(getJContentPane());
		}
		contructTitle();
		repaint();
		log.info("New project {0}", getInfo().getProject().getType());
	}
	/**
	 * 
	 */
	public void reload() {
		getJJMenuBar().reload();
		getToolBar().reload();
		getSampleRepresentationPanel().reload();
		contructTitle();
		log.info("reload");
		this.setTitle(contructTitle());
	}
	
	protected String contructTitle(){
		String version = getVersion();
		
		String projectType = getMessage("spantus.work.ui.project.type." + getInfo().getProject().getType());
		String fileName = "";
		if(getInfo().getProject().getSample().getCurrentFile() != null){
			fileName = getInfo().getProject().getSample().getCurrentFile().getFile();
		}
		String title = MessageFormat.format(getMessage("spantus.work.ui.title.format"), 
				version,
				projectType,
				fileName);
		return title;
	}
	
	protected String getVersion(){
		if(version == null){
			log.error("version not set. trying read from eclipse");
			try {
				Properties prop = new Properties();
				InputStream is = new FileInputStream(new File("./target/maven-archiver/pom.properties"));
				prop.load(is);
				version = prop.getProperty("version");
			} catch (IOException e) {
				log.debug("version for eclipse not found",e);
			}catch (NullPointerException e) {
				log.debug("version for eclipse not found",e);
			}
			if(version == null){
				log.error("version not set. trying read from jar");
				try {
					Properties prop = new Properties();
					InputStream is = this.getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.spantus/spnt-work-ui/pom.properties");
					prop.load(is);
					version = prop.getProperty("version");
				} catch (IOException e) {
					log.debug("version for jad not found",e);
				}catch (NullPointerException e) {
					log.debug("version for jar not found",e);
				}

			}
			if(version == null){
				log.error("version not set. trying read from properties");
				version = getMessage("spantus.work.ui.version");
			}

		}
		return version;
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
			jContentPane.add(getToolBar(), BorderLayout.NORTH);
			jContentPane.add(getSampleRepresentationPanel(),BorderLayout.CENTER);
			new DropTarget(jContentPane, new WavDropTargetListener(getHandler(),getInfo()));
		}
		return jContentPane;
	}
	private JPanel getRecognitionContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
//			jContentPane.add(getToolBar(), BorderLayout.NORTH);
			AdminPanel adminPanel = new AdminPanel();
			jContentPane.add(adminPanel,BorderLayout.CENTER);
//			new DropTarget(jContentPane, new WavDropTargetListener(getHandler(),getInfo()));
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
	
	
	public SpantusWorkToolbar getToolBar() {
		if (spantusToolBar == null) {
			spantusToolBar = new SpantusWorkToolbar();
			spantusToolBar.setInfo(getInfo());
			spantusToolBar.setHandler(getHandler());
		}
		return spantusToolBar;
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
