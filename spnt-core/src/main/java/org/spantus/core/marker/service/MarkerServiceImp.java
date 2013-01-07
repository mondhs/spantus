/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.core.marker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.utils.Assert;


public class MarkerServiceImp implements IMarkerService {
        @Override
	public Marker addMarker(MarkerSet markerSet, Long start, Long length) {
		Marker marker = new Marker();
		marker.setStart(start);
		marker.setLength(length);
		markerSet.getMarkers().add(marker);
		return marker;
	}
        @Override
	public boolean removeMarker(MarkerSet markerSet, Marker marker) {
		boolean removed = markerSet.getMarkers().remove(marker);
		return removed;
	}
        @Override
	public boolean validate(MarkerSet markerSet, Marker marker,
			Long newStart, Long newLength) {
		return true;
	}
        @Override	
	public Long getTime(int sampleNum, Double sampleRate) {
		return BigDecimal.valueOf((sampleNum * 1000) / sampleRate).setScale(0,
				RoundingMode.HALF_UP).longValue();
	}

        @Override
        public Collection<Marker> findAllByLabel(MarkerSetHolder markerSetHolder, String label) {
            Assert.isTrue(markerSetHolder!=null, "markerSetHolder cannot be null");
            String searchLabel = cleanupLabel(label);
            List<Marker> rtnMarkers  = new ArrayList<Marker>();
            for ( Entry<String, MarkerSet> markerSets : markerSetHolder) {
                markerSets.getKey();
                for ( Marker marker : markerSets.getValue()) {
                	String markerLabel = cleanupLabel(marker.getLabel());
                    if(searchLabel.equals(markerLabel)){
                    	rtnMarkers.add(marker);
                    }
                }
            }
            return rtnMarkers;
        }
        
        @Override
        public Marker findFirstByLabel(MarkerSetHolder markerSetHolder, String label) {
            Assert.isTrue(markerSetHolder!=null, "markerSetHolder cannot be null");
            String searchLabel = cleanupLabel(label);
            for ( Entry<String, MarkerSet> markerSets : markerSetHolder) {
                markerSets.getKey();
                for ( Marker marker : markerSets.getValue()) {
                	String markerLabel = cleanupLabel(marker.getLabel());
                    if(searchLabel.equals(markerLabel)){
                        return marker;
                    }
                }
            }
            return null;
        }
        
		@Override
		public Collection<Marker> findAllSequenesByLabels(
				MarkerSetHolder markerSetHolder, String label1, String label2) {
            Assert.isTrue(markerSetHolder!=null, "markerSetHolder cannot be null");
            
            String searchLabel1 = cleanupLabel(label1);
            String searchLabel2 = cleanupLabel(label2);
            
            Collection<Marker> rtnMarkers = new ArrayList<Marker>();
            
            for ( Entry<String, MarkerSet> markerSets : markerSetHolder) {
                markerSets.getKey();
                Marker firstMatch  = null;
                for ( Marker marker : markerSets.getValue()) {
                	String markerLabel = cleanupLabel(marker.getLabel());
                    if(firstMatch == null && searchLabel1.equals(markerLabel)){
                    	firstMatch = marker;
                    }else if(firstMatch != null && searchLabel2.equals(markerLabel)){
                    	Marker newMarker = new Marker();
                    	newMarker.setStart(firstMatch.getStart());
                    	newMarker.setEnd(marker.getEnd());
                    	newMarker.setLabel(firstMatch.getLabel() + marker.getLabel());
            			rtnMarkers.add(newMarker);
            			firstMatch = null;
                    }else{
                    	firstMatch = null;
                    }
                }
            }
            return rtnMarkers;
		}
        
		@Override
		public Marker findFirstByPhrase(MarkerSetHolder markerSetHolder,
				String... labels) {
			if(labels.length < 2){
				throw new IllegalArgumentException("Phase should contains at least 2 labels");
			}
			LinkedList<String> labelList = new LinkedList<String>(Arrays.asList(labels));
			for ( Entry<String, MarkerSet> markerSets : markerSetHolder) {
                markerSets.getKey();
                Marker rtn = null;
                for ( Marker marker : markerSets.getValue()) {
                	String markerLabel = cleanupLabel(marker.getLabel());
                	if(!labelList.getFirst().equals(markerLabel)){
                		if(rtn != null){
                			rtn = null;
                			break;
                		}
                		continue;
                	}
                	if(rtn == null){
                		rtn = new Marker(0L,0L, "");
                		String firstRemoved = labelList.removeFirst();
                		rtn.setStart(marker.getStart());
                		rtn.setEnd(marker.getEnd());
                		rtn.setLabel(rtn.getLabel()+firstRemoved);
                	}else{
                		String firstRemoved = labelList.removeFirst();
                		rtn.setEnd(marker.getEnd());
                		rtn.setLabel(rtn.getLabel()+firstRemoved);
                	}
                	if(labelList.size() == 0){
                		return rtn; 
                	}
                }
            }
			return null;
		}
		
		@Override
		public Collection<Marker> findAllByPhrase(MarkerSetHolder markerSetHolder,
				String... labels) {
			if(labels.length < 2){
				throw new IllegalArgumentException("Phase should contains at least 2 labels");
			}
			LinkedList<String> labelList = new LinkedList<String>(Arrays.asList(labels));
			Collection<Marker> rtnList = new ArrayList<Marker>();
			for ( Entry<String, MarkerSet> markerSets : markerSetHolder) {
                markerSets.getKey();
                Marker rtn = null;
                for ( Marker marker : markerSets.getValue()) {
                	String markerLabel = cleanupLabel(marker.getLabel());
					if(!labelList.getFirst().equals(markerLabel)){
                		if(rtn  != null){
                			rtn = null;
                			break;
                		}
                		continue;
                	}
                	if(rtn == null){
                		rtn = new Marker(0L,0L, "");
                		String firstRemoved = labelList.removeFirst();
                		rtn.setStart(marker.getStart());
                		rtn.setEnd(marker.getEnd());
                		rtn.setLabel(rtn.getLabel()+firstRemoved);
                	}else{
                		String firstRemoved = labelList.removeFirst();
                		rtn.setEnd(marker.getEnd());
                		rtn.setLabel(rtn.getLabel()+firstRemoved);
                	}
                	if(labelList.size() == 0){
                		rtnList.add(rtn); 
                		labelList = new LinkedList<String>(Arrays.asList(labels));
                		rtn = null;
                	}
                }
            }
			return rtnList;
		}
		
		
		private String cleanupLabel(String label) {
			String rtn = label.replaceAll("\\-","");
			return rtn;
		}


}
