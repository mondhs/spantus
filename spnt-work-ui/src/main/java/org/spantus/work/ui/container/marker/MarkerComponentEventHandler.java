package org.spantus.work.ui.container.marker;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import org.spantus.chart.impl.MarkeredTimeSeriesMultiChart;
import org.spantus.chart.marker.MarkerComponent;
import org.spantus.chart.marker.MarkerSetComponent;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class MarkerComponentEventHandler extends MouseAdapter implements MouseMotionListener, KeyListener{

	private MarkerPopupMenu popup;
	
	private MarkerComponent currentMarker;

	private Point currentPoint;
	
	protected Logger log = Logger.getLogger(MarkerComponentEventHandler.class);
	
	private MarkeredTimeSeriesMultiChart chart; 
	
	
	public MarkeredTimeSeriesMultiChart getChart() {
		return chart;
	}
	public void setChart(MarkeredTimeSeriesMultiChart chart) {
		this.chart = chart;
	}
	public MarkerComponentEventHandler(SpantusWorkInfo info, SpantusWorkCommand handler) {
		this.popup = new MarkerPopupMenu();
		this.popup.setInfo(info);
		this.popup.setHandler(handler);
		popup.initialize();
	}
	
	public void removeMarker(MarkerComponent markerComponent){
//		Container parent = markerComponent.getParent();
		popup.getMarkerComponentService().remove((MarkerSetComponent)markerComponent.getParent(), markerComponent);
 		getChart().changeSelection(0, 0);
 		log.debug("mark as removed: " + markerComponent.getMarker());
// 		parent.invalidate();
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		showIfPopupTrigger(mouseEvent);
	}
	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		showIfPopupTrigger(mouseEvent);
	}
	
	public void mouseDragged(MouseEvent e){
		Component currentComponent = ((JComponent)e.getSource()).findComponentAt(e.getPoint());
		if(currentComponent instanceof MarkerComponent){
			getChart().changeSelection(currentComponent.getX(), currentComponent.getWidth());
		}else if(currentComponent instanceof MarkerSetComponent){
			for (MarkerComponent markCmp : ((MarkerSetComponent)currentComponent).getMarkerComponents()) {
				if(markCmp.isFocusOwner()){
					getChart().changeSelection(markCmp.getX(), markCmp.getWidth());
				}
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	  

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2){
			Component currentComponent = ((JComponent)e.getSource()).findComponentAt(e.getPoint());
			//edit
			if(currentComponent instanceof MarkerComponent){
				popup.editMarker((MarkerComponent)currentComponent, (MarkerSetComponent) e.getSource());
				currentComponent.getParent().repaint();
			//add
			}else if(currentComponent instanceof MarkerSetComponent){
				popup.getMarkerComponentService()
				.addMarker( (MarkerSetComponent) currentComponent, e.getPoint(), popup.getDefaultSegmentLength());
				currentComponent.repaint();
			}
		}else if(e.getClickCount()==1){
			Component currentComponent = ((JComponent)e.getSource()).findComponentAt(e.getPoint());
			//change selection
			if(currentComponent instanceof MarkerComponent){
				getChart().changeSelection(currentComponent.getX(), currentComponent.getWidth());
//				currentComponent.getParent().repaint();
			}
		}
		
	}
	
	
	
	/**
	 * 
	 * 
	 * Keyboard handling
	 * 
	 */
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
    
	public void keyTyped(KeyEvent e) {
         int keyChar = e.getKeyChar();
         if(32 == keyChar){
        	 //space to play the segment
         	if(e.getComponent() instanceof MarkerComponent){
         		Marker m = ((MarkerComponent)e.getComponent()).getMarker();
         		popup.getInfo().getProject().setFrom(m.getStart()/1000f);
         		popup.getInfo().getProject().setLength(m.getLength()/1000f);
         		popup.getHandler().execute(GlobalCommands.sample.play.name(), popup.getInfo());
         	}
         }else if(127 == keyChar){
        	 //delete to remove segment
         	if(e.getComponent() instanceof MarkerComponent){
         		MarkerComponent markerComponent =(MarkerComponent)e.getComponent();
         		removeMarker(markerComponent);
         	}
         }else{
         	log.error("[keyTyped] name" + popup.getName()+ "; keyChar" + keyChar);	
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
