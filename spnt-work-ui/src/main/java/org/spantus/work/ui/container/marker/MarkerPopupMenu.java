package org.spantus.work.ui.container.marker;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.spantus.chart.marker.MarkerComponent;
import org.spantus.chart.marker.MarkerComponentServiceImpl;
import org.spantus.chart.marker.MarkerSetComponent;
import org.spantus.core.marker.Marker;
import org.spantus.event.SpantusEvent;
import org.spantus.event.SpantusEventMulticaster;
import org.spantus.logger.Logger;
import org.spantus.ui.ModifyObjectPopup;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.dto.SelectionDto;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

public class MarkerPopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long defaultSegmentLength = 80L;
	private MarkerComponentServiceImpl markerComponentService;

	private Logger log = Logger.getLogger(getClass());
	private ActionListener listener;
	private SpantusWorkInfo info;

	Map<menuItemsEnum, JComponent> cmpMap;
	SpantusEventMulticaster eventMulticaster;
	
	enum menuItemsEnum {
		play, edit, add, remove
	}

	public MarkerPopupMenu(SpantusEventMulticaster eventMulticaster) {
		super();
		this.eventMulticaster = eventMulticaster;
	}

	public void initialize() {
		cmpMap = new HashMap<menuItemsEnum, JComponent>();
		cmpMap.put(menuItemsEnum.add, createMenuItemp(menuItemsEnum.add.name()));
		cmpMap.put(menuItemsEnum.remove, createMenuItemp(menuItemsEnum.remove.name()));
		cmpMap.put(menuItemsEnum.edit, createMenuItemp(menuItemsEnum.edit.name()));
		cmpMap.put(menuItemsEnum.play, createMenuItemp(menuItemsEnum.play.name()));
		for (menuItemsEnum menuName : menuItemsEnum.values()) {
			add(cmpMap.get(menuName));	
		}
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

	public void addMarker(JComponent source) {
		Component invoker = getInvoker(source);
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerComponentEventHandler ml = getShower(source);
			Point p = ml.getCurrentPoint();
			getMarkerComponentService().addMarker(_markerSetComponent, p, getDefaultSegmentLength());
			
		}
		log.debug("Add to " + invoker);
	}
	

	public void remove(JComponent source) {
		Component invoker = getInvoker(source);
		if (invoker instanceof MarkerSetComponent) {
//			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerComponentEventHandler ml = getShower(source);
			ml.removeMarker(ml.getCurrentMarker());
//			Marker marker = getMarkerComponentService().remove(_markerSetComponent, ml.getCurrentMarker());
		}
	}
	/**
	 * 
	 * @param source
	 */
	public void editMarker(JComponent source) {
		Component invoker = getInvoker(source);	
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerComponentEventHandler ml = getShower(source);
			editMarker(ml.getCurrentMarker(), markerSetComponent);
		}
	}
	/**
	 * 
	 * @param markerComponent
	 * @param markerSetComponent
	 */
	public void editMarker(MarkerComponent markerComponent, MarkerSetComponent markerSetComponent) {
		edit(markerComponent);
		markerSetComponent.repaint();
	}
	/**
	 * 
	 * @param markerComponent
	 */
	private void edit(MarkerComponent markerComponent) {
		Marker _marker = markerComponent.getMarker();
		Long start = _marker.getStart();
		Long length = _marker.getLength();
		ModifyObjectPopup modifyObjectPopup = new ModifyObjectPopup();
		Set<String> includeFields = new HashSet<String>();
		includeFields.addAll(Arrays.asList(new String[]{"start","length", "label"}));
		modifyObjectPopup.setIncludeFields(includeFields);
		modifyObjectPopup.modifyObject(null, "Modify", _marker);
		
		if(!_marker.getStart().equals(start) || !_marker.getLength().equals(length) ){
			markerComponent.resetScreenCoord();
		}
		log.debug("modified: " + _marker);
		
	}

	public void play(JComponent source) {
		Component invoker = getInvoker(source);
		if (invoker instanceof MarkerSetComponent) {
			MarkerSetComponent _markerSetComponent = ((MarkerSetComponent) invoker);
			MarkerComponentEventHandler ml = getShower(source);
			Marker _marker = ml.getCurrentMarker().getMarker();
			SelectionDto selectionDto = new SelectionDto(
					_marker.getStart().floatValue()/1000,
					_marker.getLength().floatValue()/1000);
			getEventMulticaster().multicastEvent(SpantusEvent.createEvent(
					this, GlobalCommands.sample.play.name()
					,selectionDto));
			log.debug("palyed: " + _marker);
			_markerSetComponent.repaint();
		}

		
	}
	
	protected String getResource(String str){
		return I18nFactory.createI18n().getMessage(str);
	}

	public class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch (menuItemsEnum.valueOf(cmd)) {
			case add:
				addMarker((JComponent) e.getSource());
				break;
			case remove:
				remove((JComponent) e.getSource());
				break;
			case edit:
				editMarker((JComponent) e.getSource());
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
	protected MarkerComponentEventHandler getShower(JComponent source) {
		Container markerGraph = getInvoker(source).getParent();
		MouseListener ml = markerGraph.getMouseListeners()[0];
		if (ml instanceof MarkerComponentEventHandler) {
			return (MarkerComponentEventHandler) ml;
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
	

	public SpantusWorkInfo getInfo() {
		return info;
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	public MarkerComponentServiceImpl getMarkerComponentService() {
		if(markerComponentService == null){
			markerComponentService = new MarkerComponentServiceImpl();
		}
		return markerComponentService;
	}

	public Long getDefaultSegmentLength() {
		return defaultSegmentLength;
	}

	public void setDefaultSegmentLength(Long defaultSegmentLength) {
		this.defaultSegmentLength = defaultSegmentLength;
	}

	public SpantusEventMulticaster getEventMulticaster() {
		return eventMulticaster;
	}

}
