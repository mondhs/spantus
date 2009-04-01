package org.spantus.work.ui.container;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.spantus.work.ui.cmd.AboutCmd;
import org.spantus.work.ui.cmd.AutoSegmentationCmd;
import org.spantus.work.ui.cmd.CurrentSampleChangedCmd;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.MainHandler;
import org.spantus.work.ui.cmd.OpenCmd;
import org.spantus.work.ui.cmd.OptionCmd;
import org.spantus.work.ui.cmd.PlayCmd;
import org.spantus.work.ui.cmd.RecordCmd;
import org.spantus.work.ui.cmd.ReloadResourcesCmd;
import org.spantus.work.ui.cmd.ReloadSampleChartCmd;
import org.spantus.work.ui.cmd.SaveSegmentCmd;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.cmd.StopCmd;
import org.spantus.work.ui.cmd.ZoomInCmd;
import org.spantus.work.ui.cmd.ZoomOutCmd;
import org.spantus.work.ui.cmd.file.CurrentProjectChangedCmd;
import org.spantus.work.ui.cmd.file.ExportCmd;
import org.spantus.work.ui.cmd.file.ImportCmd;
import org.spantus.work.ui.cmd.file.NewProjectCmd;
import org.spantus.work.ui.cmd.file.OpenProjectCmd;
import org.spantus.work.ui.cmd.file.SaveProjectCmd;
import org.spantus.work.ui.container.panel.SampleRepresentationPanel;
import org.spantus.work.ui.dto.EnviromentRepresentation;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;
import org.spantus.work.ui.services.WorkUIServiceFactory;

public class SpantusWorkFrame extends JFrame implements ReloadableComponent{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private SpantusWorkMenuBar jJMenuBar = null;
	private SpantusWorkToolbar jJToolBarBar = null;
	private SpantusWorkInfo info = null;
	private MainHandler handler = null;
	private SampleRepresentationPanel sampleRepresentationPanel;

	/**
	 * This is the default constructor
	 */
	public SpantusWorkFrame() {
		super();
		this.setTitle("Spantus 0.0.1");
		this.setIconImage(new ImageIcon(
				this.getClass().getClassLoader().getResource("org/spantus/work/ui/img/icon.gif")
				).getImage());
		addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		    	  getInfo().setEnv(new EnviromentRepresentation());
		    	  getInfo().getEnv().setClientWindow(e.getComponent().getSize());
		    	  getInfo().getEnv().setLocation(e.getComponent().getLocation());

		    	  WorkUIServiceFactory.createInfoManager().saveWorkInfo(getInfo());
		      }
		    });
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		getJJMenuBar().initialize();
		getJJToolBarBar().initialize();
		getSampleRepresentationPanel().initialize();
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane());
		setWindowSize(getInfo().getEnv());
		contructTitle();
	}
	public void reload() {
		getJJMenuBar().reload();
		getJJToolBarBar().reload();
		contructTitle();
	}
	
	protected void contructTitle(){
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
		
		this.setTitle(title);

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

	public  void setWindowSize(EnviromentRepresentation env) {
		if(env == null){
			SpantusWorkSwingUtils.fullWindow(this);
		}else{
	        this.setSize(env.getClientWindow().width, env.getClientWindow().height);
	        this.setLocation(env.getLocation());			
		}
      
       
    }

	public SpantusWorkInfo getInfo() {
		if(info == null){
			info = WorkUIServiceFactory.createInfoManager().openWorkInfo();
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
			createFileCmd();
			createSampleCmd();
			handler
				.getCmds()
				.put(GlobalCommands.help.about.name(), new AboutCmd(this));

			CurrentSampleChangedCmd currentSampleChanged = new CurrentSampleChangedCmd(
					getSampleRepresentationPanel(),
					getSampleRepresentationPanel(),
					getHandler()
					);
			handler
			.getCmds()
			.put(GlobalCommands.file.currentSampleChanged.name(), currentSampleChanged);
			
			
			handler
			.getCmds()
			.put(GlobalCommands.tool.option.name(), 
					new OptionCmd(this));

			handler
			.getCmds()
			.put(GlobalCommands.tool.reloadResources.name(), 
					new ReloadResourcesCmd(this, currentSampleChanged));
			
			handler
			.getCmds()
			.put(GlobalCommands.tool.saveSegments.name(), 
					new SaveSegmentCmd());

			
			
			handler
			.getCmds()
			.put(GlobalCommands.tool.autoSegmentation.name(), 
					new AutoSegmentationCmd(getSampleRepresentationPanel().getSampleChart()));

		}
		return handler;
	}
	private void createSampleCmd() {

		handler
		.getCmds()
		.put(GlobalCommands.sample.record.name(), 
				new RecordCmd(getSampleRepresentationPanel()));
		handler
		.getCmds()
		.put(GlobalCommands.sample.play.name(), 
				new PlayCmd());

		handler
		.getCmds()
		.put(GlobalCommands.sample.stop.name(), 
				new StopCmd());
		
		handler
		.getCmds()
		.put(GlobalCommands.sample.zoomin.name(), 
				new ZoomInCmd(getSampleRepresentationPanel().getSampleChart()));
		handler
		.getCmds()
		.put(GlobalCommands.sample.zoomout.name(), 
				new ZoomOutCmd(getSampleRepresentationPanel().getSampleChart()));
		
		handler
		.getCmds()
		.put(GlobalCommands.sample.reloadSampleChart.name(), 
				new ReloadSampleChartCmd(getSampleRepresentationPanel().getSampleChart()));

	}
	private void createFileCmd() {
		handler.getCmds().put(GlobalCommands.file.open.name(), new OpenCmd());
		handler.getCmds().put(GlobalCommands.file.newProject.name(),
				new NewProjectCmd(this));
		handler.getCmds().put(GlobalCommands.file.openProject.name(),
				new OpenProjectCmd(this));
		handler.getCmds().put(GlobalCommands.file.saveProject.name(),
				new SaveProjectCmd(this));

		handler.getCmds().put(GlobalCommands.file.currentProjectChanged.name(),
				new CurrentProjectChangedCmd(this));
		handler.getCmds().put(GlobalCommands.file.exportFile.name(),
				new ExportCmd(this, getSampleRepresentationPanel().getSampleChart()));
		handler.getCmds().put(GlobalCommands.file.importFile.name(),
				new ImportCmd(this, getSampleRepresentationPanel().getSampleChart()));


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

}
