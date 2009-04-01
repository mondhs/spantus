package org.spantus.work.ui.container.marker;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.spantus.chart.marker.MarkerComponent;
import org.spantus.chart.marker.MarkerSetComponent;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class MarkerPopupMenuShower extends MouseAdapter {

	private MarkerPopupMenu popup;
	
	private MarkerComponent currentMarker;

	private Point currentPoint;
	
	public MarkerPopupMenuShower(SpantusWorkInfo info, SpantusWorkCommand handler) {
		this.popup = new MarkerPopupMenu();
		this.popup.setInfo(info);
		this.popup.setHandler(handler);
		popup.initialize();
	}
	public void mousePressed(MouseEvent mouseEvent) {
		showIfPopupTrigger(mouseEvent);
	}

	public void mouseReleased(MouseEvent mouseEvent) {
		showIfPopupTrigger(mouseEvent);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2){
//			if(e.getComponent() instanceof MarkerSetComponent)
//			popup.edit((JComponent)e.getComponent());
		}
		
	}
	
	protected void showIfPopupTrigger(MouseEvent mouseEvent) {
		if (popup.isPopupTrigger(mouseEvent)) {
			JComponent source = (JComponent)mouseEvent.getSource();
			if(source instanceof MarkerSetComponent){
				Component currentComponent = source.findComponentAt(mouseEvent.getPoint());
				if(currentComponent instanceof MarkerComponent){
					currentMarker = (MarkerComponent)currentComponent;
				}
			}
			currentPoint = mouseEvent.getPoint();
			popup.show((JComponent)mouseEvent.getSource(), mouseEvent.getX(),
					mouseEvent.getY());
		}
	}
	public MarkerComponent getCurrentMarker() {
		return currentMarker;
	}
	public void setCurrentMarker(MarkerComponent currentMarker) {
		this.currentMarker = currentMarker;
	}
	public Point getCurrentPoint() {
		return currentPoint;
	}
	public void setCurrentPoint(Point currentPoint) {
		this.currentPoint = currentPoint;
	}


}
