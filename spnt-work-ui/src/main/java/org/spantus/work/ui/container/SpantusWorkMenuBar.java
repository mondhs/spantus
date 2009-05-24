package org.spantus.work.ui.container;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;
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
public class SpantusWorkMenuBar extends JMenuBar implements ReloadableComponent{
	Logger log = Logger.getLogger(getClass());
	
	Map<String, JMenuItem> menuItems = new HashMap<String, JMenuItem>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -262432478200012478L;
	
	private SpantusWorkCommand handler;
	
	enum menuLabels {
		file, tool, help
	};

//	enum fileMenuLabels {
//		open, newProject, openProject, saveProject, export
//		
//	};

	enum toolMenuLabels {
		segmentation, saveSegments, option
	};
	enum helpMenuLabels {
		about
	};


	enum commandEnum {
		open, option, about
	}

	private JMenu fileMenu = null;
	private JMenu toolMenu = null;
	private JMenu helpMenu = null;
	private MenuListener listener = null;
	private SpantusWorkInfo info = null;

	public MenuListener getListener() {
		if (listener == null) {
			listener = new MenuListener();
		}
		return listener;
	}

	public SpantusWorkMenuBar() {
	}

	public void initialize() {
		initialize(getInfo().getProject().getCurrentType());
	}
	public void reload() {
		initialize(getInfo().getProject().getCurrentType());
	}
	protected void initialize(String projectType) {
		getFileMenu();
		getToolMenu();
		getHelpMenu();
		for (Entry<String, JMenuItem> menuItem : menuItems.entrySet()) {
			menuItem.getValue().setText(getResource(menuItem.getKey()));
//			log.error(getResource(menuItem.getKey()));
		}
	}	

	
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			JMenu menu = new JMenu();
			menu.setText(getResource(menuLabels.file.name()));
			menuItems.put(menuLabels.file.name(), menu);
			JMenuItem m = createMenuItem(
					GlobalCommands.file.open,
					GlobalCommands.file.open.name(),
					KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)); 
			menu.add(m);
			menu.addSeparator();
			menu.add(createMenuItem(GlobalCommands.file.newProject,
					KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK|Event.SHIFT_MASK)
					));
			menu.add(createMenuItem(GlobalCommands.file.openProject,
					KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK|Event.SHIFT_MASK)));
			menu.add(createMenuItem(GlobalCommands.file.saveProject,
					KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK|Event.SHIFT_MASK)));
			menu.addSeparator();
			menu.add(createMenuItem(GlobalCommands.file.exportFile,
					KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK|Event.SHIFT_MASK)));
			menu.add(createMenuItem(GlobalCommands.file.importFile,
					KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK|Event.SHIFT_MASK)));
			
			menu.addSeparator();
			
			menu.add(createMenuItem(GlobalCommands.file.exit,
					KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK)));
			fileMenu = menu;
			this.add(fileMenu);
		}
		return fileMenu;
	}

	private JMenu getToolMenu() {
		if (toolMenu == null) {
			JMenu menu = new JMenu();
			menu.add(createMenuItem(
					GlobalCommands.file.currentSampleChanged,
					SpantusWorkToolbar.RELOAD,
					KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK)));
			menu.add(createMenuItem(
					GlobalCommands.tool.autoSegmentation,
					KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK)));
			menu.setText(getResource(menuLabels.tool.name()));
			menuItems.put(menuLabels.tool.name(), menu);
			menu.add(createMenuItem(GlobalCommands.tool.saveSegments,
					KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)));
			
			menu.addSeparator();
			
			menu.add(createMenuItem(GlobalCommands.tool.option,
					KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK)));
			
			toolMenu = menu;
			this.add(toolMenu);

		}
		return toolMenu;
	}
	
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText(getResource(menuLabels.help.name()));
			menuItems.put(menuLabels.help.name(), helpMenu);
			helpMenu.add(createMenuItem(
						GlobalCommands.help.userGuide,
						KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)));
			helpMenu.addSeparator();
			helpMenu.add(createMenuItem(helpMenuLabels.about));
			
			this.add(helpMenu);
		}
		return helpMenu;
	}
	
	public JMenuItem createMenuItem(Enum<?> action) {
		return createMenuItem(action, action.name());
	}
	public JMenuItem createMenuItem(Enum<?> action, String message) {
		JMenuItem item = new JMenuItem(getResource(message));
		item.addActionListener(getListener());
		item.setActionCommand(action.name());
		menuItems.put(message, item);
		return item;
	}
	public JMenuItem createMenuItem(Enum<?> action, KeyStroke stroke) {
		return createMenuItem(action,action.name(), stroke);
	}
	public JMenuItem createMenuItem(Enum<?> action, String message, KeyStroke stroke) {
		JMenuItem item = createMenuItem(action, message);
		item.setAccelerator(stroke);
		return item;
	}

	public String getResource(String key) {
		return I18nFactory.createI18n().getMessage(key);
	}

	public class MenuListener implements ActionListener {

		
		public void actionPerformed(ActionEvent e) {
			log.debug(("Selected: " + e.getActionCommand()));
			getHandler().execute(e.getActionCommand(), getInfo());
		}

	}

	public SpantusWorkInfo getInfo() {
		if (info == null) {
			info = new SpantusWorkInfo();
		}
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	public SpantusWorkCommand getHandler() {
		return handler;
	}

	public void setHandler(SpantusWorkCommand handler) {
		this.handler = handler;
	}



}
