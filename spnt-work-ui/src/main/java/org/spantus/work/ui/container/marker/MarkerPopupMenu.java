package org.spantus.work.ui.container.marker;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.spantus.chart.marker.MarkerComponentUtil;
import org.spantus.chart.marker.MarkerSetComponent;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;
import org.spantus.ui.ModifyObjectPopup;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

public class MarkerPopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long defaultSegmentLength = 80L;

	Logger log = Logger.getLogger(getClass());
	ActionListener listener;
	SpantusWorkCommand handler;
	SpantusWorkInfo info;

	enum menuItemsEnum {
		add, remove, edit, play
	}

	public MarkerPopupMenu() {
		super();
	}

	public void initialize() {
		JMenuItem menuItem = createMenuItemp(menuItemsEnum.add.name());
		add(menuItem);
		menuItem = createMenuItemp(menuItemsEnum.remove.name());
		add(menuItem);
		menuItem = createMenuItemp(menuItemsEnum.edit.name());
		add(menuItem);
		menuItem = createMenuItemp(menuItemsEnum.play.name());
		add(menuItem);

	}

	public ActionListener getListener() {
		if (listener == null) {
			listener = new MenuListener();
		}
		return listener;
	}

	public JMenuItem createMenuItemp(String key) {
		JMenuItem item = new JMenuItem(getMessage(key));
		item.addActionListener(getListener());
		item.setActionCommand(key);
		return item;
	}

	protected String getMessage(String key) {
		return I18nFactory.createI18n().getMessage(key);
	}

	public void add(JComponent source) {
		Component invoker = getInvoker(source);
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerPopupMenuShower ml = getShower(source);
			Point p = ml.getCurrentPoint();
			Long start = MarkerComponentUtil.screenToTime(
					_markerSetComponent.getCtx(), p.x);
			Marker marker = new Marker();
			marker.setLabel(""
					+ _markerSetComponent.getMarkerSet().getMarkers().size());
			marker.setStart(start);
			marker.setLength(defaultSegmentLength);
			_markerSetComponent.getMarkerSet().getMarkers().add(marker);
			_markerSetComponent.repaint();

		}
		log.debug("Add to " + invoker);
	}

	public void remove(JComponent source) {
		Component invoker = getInvoker(source);
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerPopupMenuShower ml = getShower(source);
			Marker _marker = ml.getCurrentMarker().getMarker();
			_markerSetComponent.getMarkerSet().getMarkers().remove(_marker);
			log.debug("mark as removed: " + _marker);
			_markerSetComponent.repaint();

		}
	}

	public void edit(JComponent source) {
		Component invoker = getInvoker(source);	
		
		
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerPopupMenuShower ml = getShower(source);
			Marker _marker = ml.getCurrentMarker().getMarker();
			Long start = _marker.getStart();
			Long length = _marker.getLength();
			ModifyObjectPopup modifyObjectPopup = new ModifyObjectPopup();
			Set<String> includeFields = new HashSet<String>();
			includeFields.addAll(Arrays.asList(new String[]{"start","length", "label"}));
			modifyObjectPopup.setIncludeFields(includeFields);
			modifyObjectPopup.modifyObject(null, "Modify", _marker);
			
			if(!_marker.getStart().equals(start) || !_marker.getLength().equals(length) ){
				ml.getCurrentMarker().resetScreenCoord();
			}
			log.debug("modified: " + _marker);
			_markerSetComponent.repaint();
		}
	}

	public void play(JComponent source) {
		Component invoker = getInvoker(source);
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerPopupMenuShower ml = getShower(source);
			Marker _marker = ml.getCurrentMarker().getMarker();
			getInfo().getProject().setFrom(_marker.getStart().floatValue()/1000);
			getInfo().getProject().setLength(_marker.getLength().floatValue()/1000);
			getHandler().execute(GlobalCommands.sample.play.name(), getInfo());
			log.debug("palyed: " + _marker);
			_markerSetComponent.repaint();
		}

		
	}

	public class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch (menuItemsEnum.valueOf(cmd)) {
			case add:
				add((JComponent) e.getSource());
				break;
			case remove:
				remove((JComponent) e.getSource());
				break;
			case edit:
				edit((JComponent) e.getSource());
				break;
			case play:
				play((JComponent) e.getSource());
				break;
			default:
				break;
			}
			((JMenuItem)e.getSource()).setSelected(false);
			log.debug(("Selected: " + e.getActionCommand()));
		}
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	protected MarkerPopupMenuShower getShower(JComponent source) {
		Container markerGraph = getInvoker(source).getParent();
		MouseListener ml = markerGraph.getMouseListeners()[0];
		if (ml instanceof MarkerPopupMenuShower) {
			return (MarkerPopupMenuShower) ml;
		}
		return null;
	}

	protected Component getInvoker(JComponent source) {
		if(source instanceof MarkerSetComponent){
			return source; 
		}

		JPopupMenu parent = (JPopupMenu) source.getParent();
		return parent.getInvoker();
	}
	
	public SpantusWorkCommand getHandler() {
		return handler;
	}

	public void setHandler(SpantusWorkCommand handler) {
		this.handler = handler;
	}

	public SpantusWorkInfo getInfo() {
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

}
