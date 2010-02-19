package org.spantus.work.ui.container;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
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
public class SpantusWorkToolbar extends JToolBar implements ReloadableComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 767468009839505233L;
	
		

	Logger log = Logger.getLogger(getClass());
	
	public static final String RELOAD = "reload";
//	String[] mode = new String[]{"Simple", "Full"};
	
	SpantusWorkInfo info;
	
	private JButton openBtn = null;

	private JButton playBtn = null;
	
	private JButton preferencesBtn = null;
	
	private JButton recordBtn = null;

	private JButton stopBtn = null;
	
	private JButton zoomInBtn = null;
	
	private JButton zoomOutBtn = null;
	
	private JButton refreshBtn = null;
	
	private JTextField experimentIdTxt = null;
	
	private SpantusWorkCommand handler;
	
	private ToolbarListener toolbarActionListener;
	
	private Map<String, JComponent> toolBarComponents;

	public SpantusWorkToolbar() {
		super();
	}
	
	public void initialize() {
//        this.add(getOpenBtn());
//      this.add(getPreferencesBtn());
//		this.add(getPlayBtn());
//        this.add(getStopBtn());
//        this.add(getRecordBtn());
//        this.add(getMode());
		initialize(getInfo().getProject().getType());
	}
	
	protected void initialize(String projectType) {
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 3, 2);
		this.setLayout(layout);

		this.add(getOpenBtn());
		this.add(new JToolBar.Separator());
		this.add(getExperimentIdTxt());
		this.add(new JToolBar.Separator());
		this.add(getPlayBtn());
        this.add(getStopBtn());
        this.add(getRecordBtn());
        
		this.add(new JToolBar.Separator());
		this.add(getZoomInBtn());
		this.add(getZoomOutBtn());

		
//		this.add(new JToolBar.Separator());
		this.add(new JToolBar.Separator());
		this.add(getPreferencesBtn());
		this.add(getRefreshBtn());
		
	}	
	
	public void reload() {
//		removeAll();
//		initialize(getInfo().getProject().getCurrentType());
		
		String projectType = getInfo().getProject().getType();
		boolean isPlayable = false;
		ProjectTypeEnum type = ProjectTypeEnum.valueOf(projectType);
		switch (type) {
		case feature:
			isPlayable = true;
			break;
		case segmenation:
			isPlayable = true;
			break;
		case recordSegmentation:
			break;
		default:
			break;
		}
		getPlayBtn().setEnabled(isPlayable);
		getOpenBtn().setEnabled(isPlayable);
		getExperimentIdTxt().setText(getInfo().getProject().getExperimentId());
	}
	
	public ToolbarListener getToolbarActionListener() {
		if(toolbarActionListener == null){
			toolbarActionListener = new ToolbarListener();
		}
		return toolbarActionListener;
	}
	public ToolbarListener getToolbarActionListener(char accelerator) {
		return new ToolbarListener(accelerator);
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	public SpantusWorkInfo getInfo() {
		if(info == null){
			info = new SpantusWorkInfo();
		}
		return info;
	}

	public JButton getOpenBtn() {
		if (openBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.open.getCode());
			openBtn = createButton(icon, GlobalCommands.file.open.name());
			getToolBarComponents().put(GlobalCommands.file.open.name(), openBtn);
		}
		return openBtn;
	}

	public JButton getPlayBtn() {
		if (playBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.play.getCode());
			playBtn = createButton(icon, GlobalCommands.sample.play.name());
			getToolBarComponents().put(GlobalCommands.sample.play.name(), playBtn);
		}
		
		return playBtn;
	}
	public JButton getPreferencesBtn() {
		if (preferencesBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.preferences.getCode());
			preferencesBtn = createButton(icon, GlobalCommands.tool.option.name());
			getToolBarComponents().put(GlobalCommands.tool.option.name(), preferencesBtn);
		}
		return preferencesBtn;
	}
	public JButton getRecordBtn() {
		if (recordBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.record.getCode());
			recordBtn = createButton(icon, GlobalCommands.sample.record.name());
			getToolBarComponents().put(GlobalCommands.sample.record.name(), recordBtn);
		}
		return recordBtn;
	}
	public JButton getStopBtn() {
		if (stopBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.stop.getCode());
			stopBtn = createButton(icon, GlobalCommands.sample.stop.name());
			getToolBarComponents().put(GlobalCommands.sample.stop.name(), stopBtn);
		}
		return stopBtn;
	}

	public JButton getZoomInBtn() {
		if (zoomInBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.zoomin.getCode());
			zoomInBtn = createButton(icon, GlobalCommands.sample.zoomin.name());
			getToolBarComponents().put(GlobalCommands.sample.zoomin.name(), zoomInBtn);
		}
		return zoomInBtn;
	}

	public JButton getZoomOutBtn() {
		if (zoomOutBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.zoomout.getCode());
			zoomOutBtn = createButton(icon, GlobalCommands.sample.zoomout.name());
			getToolBarComponents().put(GlobalCommands.sample.zoomout.name(), zoomOutBtn);
		}
		return zoomOutBtn;
	}
	
	public JButton getRefreshBtn() {
		if (refreshBtn == null) {
			ImageIcon icon = createIcon(ImageResourcesEnum.refresh.getCode());
			refreshBtn = createButton(icon, GlobalCommands.file.currentSampleChanged.name(),RELOAD);
			getToolBarComponents().put(RELOAD, refreshBtn);
		}
		return refreshBtn;
	}

	
	public JTextField getExperimentIdTxt() {
		if(experimentIdTxt == null){
			experimentIdTxt = new JTextField(getInfo().getProject().getExperimentId());
			experimentIdTxt.setColumns(30);
			experimentIdTxt.setFocusable(false);
			experimentIdTxt.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e) {
					experimentIdTxt.setFocusable(true);
					experimentIdTxt.requestFocus();
					super.mouseEntered(e);
				}
				@Override
				public void mouseExited(MouseEvent e) {
					experimentIdTxt.setFocusable(false);
					super.mouseExited(e);
				}

				
			});
			experimentIdTxt.addFocusListener( new FocusAdapter() {
	            public void focusLost(FocusEvent evt) {
	            	getInfo().getProject().setExperimentId(((JTextField)evt.getComponent()).getText().trim());
	            }
	        });
		}
		experimentIdTxt.setText(getInfo().getProject().getExperimentId());
		return experimentIdTxt;
	}
	
	
//	public JComboBox getMode() {
//		JComboBox cb = new JComboBox(mode);
//		ActionListener lst = new ActionListener() {
//		      public void actionPerformed(ActionEvent e) {
//		    	  String changedMode = "changedMode";
//		    	  getHandler().execute(e.getActionCommand(), getInfo());
//		      }
//		};
//		cb.addActionListener(lst);
//		return cb;
//	}

	protected ImageIcon createIcon(String name){
		ImageIcon ii = new ImageIcon(
				getClass().getClassLoader().getResource(name));
		return new ImageIcon(ii.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	}

	protected JButton createButton(ImageIcon icon, String cmd){
		return createButton(icon, cmd, cmd);
	}
	
	protected JButton createButton(ImageIcon icon, String cmd, String name){
		JButton btn = new JButton(icon);
		btn.setActionCommand(cmd);
		btn.setBorder(BorderFactory.createCompoundBorder()); 
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setFocusable(false);
		btn.setActionCommand(cmd);
		btn.setToolTipText(getResource(name));
		btn.addActionListener(getToolbarActionListener());
		return btn;
	}
	public SpantusWorkCommand getHandler() {
		return handler;
	}

	public void setHandler(SpantusWorkCommand handler) {
		this.handler = handler;
	}
	
	public class ToolbarListener extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 859207181531470590L;

		public ToolbarListener() {
		}
		public ToolbarListener(char accelerator) {
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(' '));
		}

		
		public void actionPerformed(ActionEvent e) {
			log.debug(("Selected: " + e.getActionCommand()));
			getHandler().execute(e.getActionCommand(), getInfo());
		}
	}

	public String getResource(String key) {
		return I18nFactory.createI18n().getMessage(key);
	}

	

	public Map<String, JComponent> getToolBarComponents() {
		if(toolBarComponents == null){
			toolBarComponents = new HashMap<String, JComponent>();
		}
		return toolBarComponents;
	}
	

}
