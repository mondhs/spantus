package org.spantus.work.ui.container;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.i18n.I18nFactory;
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
	
	public static final String FILE_FIND_ICON = "org/spantus/work/ui/icon/filefind.png";
	public static final String PLAY_ICON = "org/spantus/work/ui/icon/media-playback-start.png";
	public static final String STOP_ICON = "org/spantus/work/ui/icon/media-playback-stop.png";
	public static final String RECORD_ICON = "org/spantus/work/ui/icon/media-playback-record.png";
	public static final String PREFERENCES_ICON = "org/spantus/work/ui/icon/gtk-preferences.png";

	public static final String ZOOMIN_ICON = "org/spantus/work/ui/icon/gtk-zoom-in.png";
	public static final String ZOOMOUT_ICON = "org/spantus/work/ui/icon/gtk-zoom-out.png";
	

	Logger log = Logger.getLogger(getClass());
	
	String[] mode = new String[]{"Simple", "Full"};
	
	SpantusWorkInfo info;
	
	private JButton openBtn = null;

	private JButton playBtn = null;
	
	private JButton preferences = null;
	
	private JButton recordBtn = null;

	private JButton stopBtn = null;
	
	private JButton zoomInBtn = null;
	
	private JButton zoomOutBtn = null;
	
	private JTextField experimentIdTxt = null;
	
	private SpantusWorkCommand handler;
	
	private ToolbarListener toolbarActionListener;

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
		initialize(getInfo().getProject().getCurrentType());
	}
	
	protected void initialize(String projectType) {
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 3, 2);
		this.setLayout(layout);

		boolean isPlayable = false;

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
		
		this.add(new JToolBar.Separator());
		this.add(new JToolBar.Separator());
		this.add(getPreferencesBtn());
		
	}	
	
	public void reload() {
		removeAll();
		initialize(getInfo().getProject().getCurrentType());
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
			ImageIcon icon = createIcon(FILE_FIND_ICON);
			openBtn = createButton(icon, GlobalCommands.file.open.name());
		}
		return openBtn;
	}

	public JButton getPlayBtn() {
		if (playBtn == null) {
			ImageIcon icon = createIcon(PLAY_ICON);
			playBtn = createButton(icon, GlobalCommands.sample.play.name());
		}
		
		return playBtn;
	}
	public JButton getPreferencesBtn() {
		if (preferences == null) {
			ImageIcon icon = createIcon(PREFERENCES_ICON);
			preferences = createButton(icon, GlobalCommands.tool.option.name());
		}
		return preferences;
	}
	public JButton getRecordBtn() {
		if (recordBtn == null) {
			ImageIcon icon = createIcon(RECORD_ICON);
			recordBtn = createButton(icon, GlobalCommands.sample.record.name());
		}
		return recordBtn;
	}
	public JButton getStopBtn() {
		if (stopBtn == null) {
			ImageIcon icon = createIcon(STOP_ICON);
			stopBtn = createButton(icon, GlobalCommands.sample.stop.name());
		}
		return stopBtn;
	}

	public JButton getZoomInBtn() {
		if (zoomInBtn == null) {
			ImageIcon icon = createIcon(ZOOMIN_ICON);
			zoomInBtn = createButton(icon, GlobalCommands.sample.zoomin.name());
		}
		return zoomInBtn;
	}

	public JButton getZoomOutBtn() {
		if (zoomOutBtn == null) {
			ImageIcon icon = createIcon(ZOOMOUT_ICON);
			zoomOutBtn = createButton(icon, GlobalCommands.sample.zoomout.name());
		}
		return zoomOutBtn;
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
		return new ImageIcon(
				getClass().getClassLoader().getResource(name));
	}

	
	protected JButton createButton(ImageIcon icon, String cmd){
		JButton btn = new JButton(icon);
		btn.setActionCommand(cmd);
		btn.setBorder(BorderFactory.createCompoundBorder()); 
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setFocusable(false);
		btn.setActionCommand(cmd);
		btn.setToolTipText(getResource(cmd));
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
	            	getInfo().getProject().setExperimentId(((JTextField)evt.getComponent()).getText());
	            }
	        });
		}
		experimentIdTxt.setText(getInfo().getProject().getExperimentId());
		return experimentIdTxt;
	}
	

}
