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
package org.spantus.work.ui.container;

import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.spantus.event.BasicSpantusEventMulticaster;
import org.spantus.event.SpantusEventMulticaster;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.CommandBuilderService;
import org.spantus.work.ui.cmd.CommandBuilderServiceImpl;
import org.spantus.work.ui.cmd.CommandExecutionFacade;
import org.spantus.work.ui.cmd.CommandExecutionFacadeImpl;
import org.spantus.work.ui.cmd.SpantusWorkUIListener;
import org.spantus.work.ui.container.panel.AbstractSpantusContentPane;
import org.spantus.work.ui.container.panel.RecognitionContentPane;
import org.spantus.work.ui.container.panel.SampleRepresentationPanel;
import org.spantus.work.ui.container.panel.SegmentationContentPane;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.i18n.ImageResourcesEnum;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;
import org.spantus.work.ui.services.impl.SpantusUIServiceImpl;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class SpantusWorkFrame extends JFrame implements ReloadableComponent{

	private static final long serialVersionUID = 1L;
	private SpantusWorkMenuBar jJMenuBar = null;
	private SpantusWorkInfo info = null;
	private Logger log= Logger.getLogger(SpantusWorkFrame.class);
	 
	private SpantusUIServiceImpl spantusUIService;
	private WorkInfoManager workInfoManager;
	private JProgressBar recordMonitor;
	private AbstractSpantusContentPane spantusContentPane;
	
	private SpantusEventMulticaster eventMulticaster;
	private CommandExecutionFacade executionFacade;
//        private SpantusEventListener eventListener;

	
	/**
	 * This is the default constructor
	 */
	public SpantusWorkFrame() {
		super();
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

		CommandExecutionFacadeImpl executionFacadeImpl = new CommandExecutionFacadeImpl(this);
		this.executionFacade = executionFacadeImpl;

        SpantusWorkUIListener uiEventListener = new SpantusWorkUIListener();
		uiEventListener.setInfo(getInfo());
        CommandBuilderService builder = new CommandBuilderServiceImpl();
        uiEventListener.getCmds().putAll(
                builder.createSystem(executionFacade)
        		);
        uiEventListener.getCmds().putAll(
                builder.create(executionFacade)
        		);
                eventMulticaster = new BasicSpantusEventMulticaster();
		eventMulticaster.addListener(uiEventListener);

		this.setTitle(contructTitle());


		getJJMenuBar().initialize();
		setRecordMonitor(new JProgressBar(0, 16));
		
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
		getContentPane().removeAll();
		//update listeners
		getEventMulticaster().removeAllListeners();
        SpantusWorkUIListener uiEventListener = new SpantusWorkUIListener();
		uiEventListener.setInfo(getInfo());
        CommandBuilderService builder = new CommandBuilderServiceImpl();
        uiEventListener.getCmds().putAll(
                builder.createSystem(executionFacade)
        		);
		eventMulticaster.addListener(uiEventListener);

		
		if(ProjectTypeEnum.recognition.name().equals(
				getInfo().getProject().getType())){
			spantusContentPane = new RecognitionContentPane(getEventMulticaster()); 
			setContentPane(spantusContentPane);
                        spantusContentPane.setInfo(getInfo());
		}else{
			SegmentationContentPane pane = new SegmentationContentPane(getEventMulticaster());
			spantusContentPane = pane;
			pane.setInfo(getInfo());
			pane.setEventMulticaster(getEventMulticaster());
//			getJJMenuBar().initialize();
//			getToolBar().initialize();
//                 getToolBar().add(getRecordMonitor());

			uiEventListener.getCmds().putAll(
	                builder.create(executionFacade)
	        		);

			setContentPane(spantusContentPane);
		}
		new DropTarget(getContentPane(), new WavDropTargetListener(getExecutionFacade(),getInfo()));
		spantusContentPane.initialize();
		contructTitle();
		validate();
		log.info("New project {0}", getInfo().getProject().getType());
	}
	/**
	 * 
	 */
	public void reload() {
		getJJMenuBar().reload();
		spantusContentPane.reload();
		log.info("reload");
		this.setTitle(contructTitle());
	}
	
	protected String contructTitle(){
		String version = getInfo().getEnv().getSpantusVersion();
		
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
	
	
	/**
	 * This method initializes jContentPane for segmentation 
	 * 
	 * @return javax.swing.JPanel
	 */
//	private JPanel getSegmentationContentPane() {
//		if (jContentPane == null) {
//			jContentPane = new JPanel();
//			jContentPane.setLayout(new BorderLayout());
//			jContentPane.add(getToolBar(), BorderLayout.NORTH);
//			jContentPane.add(getSampleRepresentationPanel(),BorderLayout.CENTER);
//		}
//		return jContentPane;
//	}
//	private JPanel getRecognitionContentPane() {
//		if (jContentPane == null) {
//			jContentPane = new RecognitionContentPane();
//		}
//		return jContentPane;
//	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private SpantusWorkMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			SpantusWorkMenuBar spantusWorkMenuBar = new SpantusWorkMenuBar();
			spantusWorkMenuBar.setInfo(getInfo());
			spantusWorkMenuBar.setEventMulticaster(eventMulticaster);
			jJMenuBar = spantusWorkMenuBar;
		}
		return jJMenuBar;
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

	public SampleRepresentationPanel getSampleRepresentationPanel() {
		if (spantusContentPane instanceof SegmentationContentPane) {
			return ((SegmentationContentPane)spantusContentPane).getSampleRepresentationPanel();
		}
		return null;
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

	public JProgressBar getRecordMonitor() {
		return recordMonitor;
	}

	public void setRecordMonitor(JProgressBar recordMonitor) {
		this.recordMonitor = recordMonitor;
	}

	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}

	public void setEventMulticaster(SpantusEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
	}

	public CommandExecutionFacade getExecutionFacade() {
		return executionFacade;
	}
	
	
}
