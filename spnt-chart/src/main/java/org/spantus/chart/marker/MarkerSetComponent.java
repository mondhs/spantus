package org.spantus.chart.marker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.service.MarkerServiceFactory;
import org.spantus.logger.Logger;

public class MarkerSetComponent extends JComponent implements MouseListener,
		MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Cursor CURSOR_DRAG_EAST = new Cursor(
			Cursor.E_RESIZE_CURSOR);
	private static final Cursor CURSOR_DRAG_WEST = new Cursor(
			Cursor.W_RESIZE_CURSOR);
	public static final Cursor CURSOR_DEFAULT = new Cursor(
			Cursor.DEFAULT_CURSOR);
	public static final Cursor CURSOR_MOVE = new Cursor(Cursor.MOVE_CURSOR);

	public static final int DRAG_BORDER_SIZE = 5;
	
	MarkerGraphCtx ctx;

	public final Color MARK_COLOR = Color.RED;


	enum DragStatusEnum {
		move, left, right, none
	}

	DragStatusEnum dragStatus = DragStatusEnum.none;
	MarkerSet markerSet;
//	List<MarkerComponent> markerComponents;
	Logger log = Logger.getLogger(getClass());
	Integer lastMouseX;
	MarkerComponent currentMarkerComponent;

	public MarkerSet getMarkerSet() {
		return markerSet;
	}

	public void setMarkerSet(MarkerSet markerSet) {
		this.markerSet = markerSet;
	}

	public void initialize() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		setBackground(Color.white);

		for (Marker marker : getMarkerSet().getMarkers()) {
			MarkerComponent component = new MarkerComponent();
			component.setCtx(getCtx());
			component.setMarker(marker);
			component.addKeyListener(getKeyListeners()[0]);
			add(component);
		}
	}
	
	public void addMarkerToView(Point p, int length){
		MarkerServiceFactory.createMarkerService().addMarker(markerSet, 
				BigDecimal.valueOf(p.x), BigDecimal.valueOf(100));
	}
	
	public void repaintIfDirty(){
		Set<MarkerComponent> removed = new HashSet<MarkerComponent>();
		Set<Marker> created = new HashSet<Marker>();
		
		List<MarkerComponent> markerComponents = getMarkerComponents();
		
		for (MarkerComponent markerComponent : markerComponents) {
			if(!getMarkerSet().getMarkers().contains(markerComponent.getMarker())){
				removed.add(markerComponent);
			}
		}
		for (Marker marker : getMarkerSet().getMarkers()) {
			boolean exist = false;
			for (MarkerComponent markerComponent : markerComponents) {
				if(marker.equals(markerComponent.getMarker())){
					exist = true;
					break;
				}
			}
			if(!exist){
				created.add(marker);
			}
		}
		for (MarkerComponent marker : removed) {
			remove(marker);
			log.debug("removed:" + marker.getMarker().getLabel());

		}
		for (Marker marker : created) {
			MarkerComponent markerComponent = new MarkerComponent();
			markerComponent.setMarker(marker);
			add(markerComponent);
			log.debug("created:" + marker.getLabel());
			sortMarkers();
			changeSize(getSize());

		}
	}
	
	public void changeSize(Dimension size){
		setSize(size);
		for (MarkerComponent component : getMarkerComponents()) {
			component.setCtx(getCtx());
			component.changeSize(new Dimension(getSize()));
		}
	}
	
	public List<MarkerComponent> getMarkerComponents() {
		List<MarkerComponent> markerComponents = new ArrayList<MarkerComponent>();
		for (int i = 0; i < getComponentCount(); i++) {
			markerComponents.add((MarkerComponent)getComponent(i));
		}
		return markerComponents;
	}


	public void mouseClicked(MouseEvent e) {
		Component component = findComponentAt(e.getPoint());
		if (component instanceof MarkerComponent) {
			((MarkerComponent) component).requestFocus();
		}
		repaintMarkers();
	}

	public void mouseEntered(MouseEvent e) {
		Component component = findComponentAt(e.getPoint());
		if (component instanceof MarkerComponent) {
			component.setCursor(CURSOR_DEFAULT);
		}

	}

	public void mouseExited(MouseEvent e) {
		Component component = findComponentAt(e.getPoint());
		if (component instanceof MarkerComponent) {
			dragStatus = DragStatusEnum.none;
		}
	}

	public void mousePressed(MouseEvent e) {
		Component component = findComponentAt(e.getPoint());
		if (component instanceof MarkerComponent) {
			currentMarkerComponent = ((MarkerComponent) component);
			currentMarkerComponent.requestFocus();
			updateCursor(e.getPoint(),currentMarkerComponent);
			updateDragState(e.getPoint());
		}
		repaintMarkers();
	}

	public void mouseReleased(MouseEvent e) {
		currentMarkerComponent = null;
		lastMouseX = null;
	}

	public void mouseDragged(MouseEvent e) {
		if (currentMarkerComponent != null) {
			MarkerComponent markerComponent = currentMarkerComponent;
			if (lastMouseX == null) {
				lastMouseX = e.getPoint().x;
			}
			int delta = (e.getPoint().x - lastMouseX);
			lastMouseX = e.getPoint().x;
			switch (dragStatus) {
			case move:
				update(markerComponent, markerComponent.getStartX() + delta, markerComponent.getEndX() + delta);
				break;
			case left:
				update(markerComponent, markerComponent.getStartX() + delta, markerComponent.getEndX());
				break;
			case right:
				update(markerComponent, markerComponent.getStartX(), markerComponent.getEndX() + delta);
				break;
			default:
				break;
			}
			this.repaint();
//			log.debug("Dragged: " + dragStatus + delta);
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		Component component = findComponentAt(e.getPoint());
		if (component instanceof MarkerComponent) {
			updateCursor(e.getPoint(), (MarkerComponent) component);
		}else{
			updateCursor(e.getPoint(), null	);
		}
	}
	/**
	 * 
	 */
	protected void repaintMarkers(){
		for (MarkerComponent _marker : getMarkerComponents()) {
//			log.error("[repaintMarkers]" + _marker.getMarker());
			_marker.repaint();
		}

	}

	protected void updateDragState(Point p){
		MarkerComponent markerComponent = currentMarkerComponent;
		int dToStart = p.x - markerComponent.getLocation().x;
		int dToEnd = (markerComponent.getLocation().x + markerComponent.getSize().width)- p.x;
		if (dToStart < DRAG_BORDER_SIZE) {
			dragStatus = DragStatusEnum.left;
		} else if (dToEnd < DRAG_BORDER_SIZE) {
			dragStatus = DragStatusEnum.right;
		} else {
			dragStatus = DragStatusEnum.move;
		}
		log.debug(MessageFormat.format(" Mouse moved name:{3} status:{0}; dToStart:{1}; dToEnd{2}", dragStatus.name(), dToStart, dToEnd, markerComponent.getName()));
		
	}

	
	protected void updateCursor(Point p, MarkerComponent markerComponent){
		if (markerComponent != null) {
			int dToStart = p.x - markerComponent.getLocation().x;
			int dToEnd = (markerComponent.getLocation().x + markerComponent.getSize().width)- p.x;
			if (dToStart < DRAG_BORDER_SIZE) {
				setCursor(CURSOR_DRAG_WEST);
			} else if (dToEnd < DRAG_BORDER_SIZE) {
				setCursor(CURSOR_DRAG_EAST);
			} else {
				setCursor(CURSOR_MOVE);
			}
		} else {
			setCursor(CURSOR_DEFAULT);
		}
	}
	
	protected void update(MarkerComponent marker, int newStartX, int newEndX) {
		if (validate(marker, newStartX, newEndX)) {
			marker.setStartX(newStartX);
			marker.setEndX(newEndX);
		}
	}

	protected boolean validate(MarkerComponent marker, int newStartX,
			int newEndX) {
		List<MarkerComponent> markerComponentList = getMarkerComponents();
		
		if (newEndX - newStartX < DRAG_BORDER_SIZE * 2) {
			log.debug("element too small" + (newEndX - newStartX));
			return false;
		}
		for (MarkerComponent markerI : markerComponentList) {
			if (markerI.equals(marker)) {
				break;
			}
		}
		MarkerComponent next = nextMarkers(marker.getMarker());
		MarkerComponent previous = previousMarkers(marker.getMarker());
		if(previous != null){
			if(previous.getEndX()> newStartX){
				log.debug("last element overlaps with previous element " + newStartX);
				return false;
			}
		}
		if(next != null){
			if( newEndX > next.getStartX()){
				log.debug(MessageFormat.format("first element {0}[{1}] overlaps with next element {2}[{3}]" , 
						marker.getName(),newEndX, next.getName(), next.getStartX()));
				return false;
			}
		}

		return true;
	}

	public void sortMarkers(){
		Comparator<Marker> comparator = new Comparator<Marker>(){
			public int compare(Marker o1, Marker o2) {
				return o1.getStart().compareTo(o2.getStart());
			}
		};
		log.debug("[sortMarkers]before: " + getMarkerSet().getMarkers());
		Collections.sort(getMarkerSet().getMarkers(), comparator);
		log.debug("[sortMarkers]after: " + getMarkerSet().getMarkers());
		
	}
	public MarkerComponent nextMarkers(Marker marker){
		MarkerComponent next = null;
		Marker nextMarker = null;
		for (Iterator<Marker> iterator = getMarkerSet().getMarkers().iterator(); iterator.hasNext();) {
			Marker iMarker = iterator.next();
			if(iMarker.equals(marker) && iterator.hasNext()){
				nextMarker = iterator.next();
			}
		}
		if(nextMarker == null){
			return null;
		}
		for (int i = 0; i < getComponentCount(); i++) {
			MarkerComponent markerComponent = (MarkerComponent)getComponent(i);
			if(markerComponent.getMarker().equals(nextMarker)){
				next = markerComponent;
			}
		}
		return next;
	}
	/**
	 * 
	 * @param marker
	 * @return
	 */
	public MarkerComponent previousMarkers(Marker marker){
		MarkerComponent previous = null;
		Marker previousMarker = null;
		boolean found = false;
		for (Marker iMarker : getMarkerSet().getMarkers()) {
			if(iMarker.equals(marker)){
				found = true;
				break;
			}
			previousMarker = iMarker;
		}
		if(!found){
			return null;
		}
		for (int i = 0; i < getComponentCount(); i++) {
			MarkerComponent markerComponent = (MarkerComponent)getComponent(i);
			if(markerComponent.getMarker().equals(previousMarker)){
				previous = markerComponent;
			}
		}
		return previous;

	}
	
	public MarkerGraphCtx getCtx() {
		return ctx;
	}

	public void setCtx(MarkerGraphCtx ctx) {
		this.ctx = ctx;
	}

}
