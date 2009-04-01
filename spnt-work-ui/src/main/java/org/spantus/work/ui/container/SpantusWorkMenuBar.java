package org.spantus.work.ui.container;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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
		autoSegmentation, option, saveSegments
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
		this.remove(getFileMenu());
		this.remove(getToolMenu());
		this.remove(getHelpMenu());

		fileMenu = null;
		toolMenu = null;
		helpMenu = null;
		initialize(getInfo().getProject().getCurrentType());
	}
	protected void initialize(String projectType) {
		this.add(getFileMenu());
		this.add(getToolMenu());
		this.add(getHelpMenu());

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
			JMenuItem m = createMenuItemp(GlobalCommands.file.open.name(), KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)); 
			menu.add(m);
			menu.addSeparator();
			menu.add(createMenuItemp(GlobalCommands.file.newProject.name()));
			menu.add(createMenuItemp(GlobalCommands.file.openProject.name()));
			menu.add(createMenuItemp(GlobalCommands.file.saveProject.name()));
			menu.addSeparator();
			menu.add(createMenuItemp(GlobalCommands.file.exportFile.name()));
			menu.add(createMenuItemp(GlobalCommands.file.importFile.name()));
			fileMenu = menu;
		}
		return fileMenu;
	}

	private JMenu getToolMenu() {
		if (toolMenu == null) {
			JMenu menu = new JMenu();
			menu.add(createMenuItemp(GlobalCommands.tool.autoSegmentation.name(), KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK)));
			menu.setText(getResource(menuLabels.tool.name()));

			menu.add(createMenuItemp(GlobalCommands.tool.saveSegments.name()));
			
			menu.addSeparator();
			
			JMenuItem m = createMenuItemp(toolMenuLabels.option.name()); 
			m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
			menu.add(m);
			
			toolMenu = menu;

		}
		return toolMenu;
	}
	
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText(getResource(menuLabels.help.name()));
			helpMenu.add(createMenuItemp(helpMenuLabels.about.name()));
		}
		return helpMenu;
	}
	
	public JMenuItem createMenuItemp(String key) {
		JMenuItem item = new JMenuItem(getResource(key));
		item.addActionListener(getListener());
		item.setActionCommand(key);
		return item;
	}
	public JMenuItem createMenuItemp(String key, KeyStroke stroke) {
		JMenuItem item = createMenuItemp(key);
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
