package org.spantus.chart.marker;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerTimeComparator;
import org.spantus.logger.Logger;

public class MarkerComponentServiceImpl {
	
	Logger log = Logger.getLogger(getClass());
	/**
	 * 
	 * @param markerSetComponent
	 * @param p
	 * @param segmentLength
	 */
	public void addMarker(MarkerSetComponent markerSetComponent, Point p, Long segmentLength){
		Long start = MarkerComponentUtil.screenToTime(
				markerSetComponent.getCtx(), p.x);
		Marker marker = new Marker();
		marker.setLabel("0");
//				+ (markerSetComponent.getMarkerSet().getMarkers().size()+1));
		marker.setStart(start);
		marker.setLength(segmentLength);
		markerSetComponent.getMarkerSet().getMarkers().add(marker);
		
		relable(markerSetComponent.getMarkerSet());
		
		markerSetComponent.repaint();
	}
	/**
	 * 
	 * @param markerSet
	 */
	protected void relable(MarkerSet markerSet){
		List<Marker> markers = markerSet.getMarkers();
		Collections.sort(markers, new MarkerTimeComparator());
		int i = 1;
		for (Marker marker : markers) {
			try{
				Integer.valueOf(marker.getLabel());
				marker.setLabel(Integer.valueOf(i++).toString());
			}catch (NumberFormatException  e) {
				//if label set, then it is custom label set and do nothing
			}
			
		}
	}
	/**
	 * 
	 * @param markerSetComponent
	 */
	public Marker remove(MarkerSetComponent markerSetComponent, MarkerComponent markerComponent) {
			if(markerComponent == null){
				log.error("marker set component is null");
				return null;
			}
			Marker _marker = markerComponent.getMarker();
			markerSetComponent.getMarkerSet().getMarkers().remove(_marker);
			markerSetComponent.remove(markerComponent);
			relable(markerSetComponent.getMarkerSet());
			markerSetComponent.repaint();
			return _marker;
	}
}
