package org.spantus.extractor.segments;

import org.spantus.extractor.segments.offline.ExtremeSegment;

public class ExtremeSegmentServiceImpl {
	public boolean isIncrease(ExtremeSegment current){
		if(current.getStartEntry() == null || current.getEndEntry() == null) return false;
		Double start = current.getStartEntry().getValue();
		Double end = current.getEndEntry().getValue();
		return end.compareTo(start)<0;
	}
	public boolean isDecrease(ExtremeSegment current){
		if(current.getStartEntry() == null || current.getEndEntry() == null) return false;
		Double start = current.getStartEntry().getValue();
		Double end = current.getEndEntry().getValue();
		return start.compareTo(end)>0;
	}
	public boolean isDecrease(ExtremeSegment current, ExtremeSegment segment){
//		Double thisPeak = this.getPeakEntry().getValue();
//		Double otherPeak = segment.getPeakEntry().getValue();
//		boolean decrease = isDecrease() && segment.isDecrease() && thisPeak>otherPeak; 
		Double thisPeak = current.getPeakEntry().getValue();
		Double otherPeak = segment.getPeakEntries().getLast().getValue();
		Double thisStart = current.getStartEntry().getValue();
		Double otherStart = segment.getStartEntry().getValue();
		boolean decrease = thisPeak<otherPeak && thisStart < otherStart;
		return decrease;
	}

	public boolean isIncrease(ExtremeSegment current, ExtremeSegment previousSegment){
		Double thisPeak = current.getPeakEntry().getValue();
		Double otherPeak = previousSegment.getPeakEntries().getLast().getValue();
		Double thisStart = current.getStartEntry().getValue();
		Double otherStart = previousSegment.getStartEntry().getValue();
		boolean increase = thisPeak-otherPeak>(thisPeak+otherPeak)*0 && thisStart > otherStart;
		 
		
//		boolean increase = isIncrease() && previousSegment.isIncrease() && thisPeak>otherPeak;  
		return increase;
	}
	/**
	 * Idea was taken from:
	 * http://ea.donntu.edu.ua:8080/jspui/bitstream/123456789/8104/1/FEDYAEV_BONDARENKO_print.pdf
	 * 
	 * @param current
	 * @return
	 */
	public Double angle(ExtremeSegment current){
		
		if(current == null ||  current.getPeakEntry() == null || current.getEndEntry() == null){
			return 0D;
		}
		
		Double cx = current.getPeakEntry().getIndex().doubleValue();
		Double cy = current.getPeakEntry().getValue();
		
		Double x1 = current.getStartEntry().getIndex().doubleValue();
		Double y1 = current.getStartEntry().getValue();

		Double x2 = current.getEndEntry().getIndex().doubleValue();
		Double y2 = current.getEndEntry().getValue();
		

		Double d1x = x1 - cx;
		Double d1y = y1 - cy;
		Double d2x = x2 - cx;
		Double d2y = y2 - cy;
		Double tx1 = Math.atan2(d1y, d1x);
		Double tx2 = Math.atan2(d2y, d2x);
		Double deg = Math.toDegrees(tx2-tx1);
		return deg;
		
	}
	
	public boolean isSimilar(ExtremeSegment current, ExtremeSegment segment){
//		Double lastArea = segment.getCalculatedArea();
//		Double currentArea = this.getCalculatedArea();
//		Integer lastPeak = segment.getPeakEntries().size();
//		Integer currentPeak = this.getPeakEntries().size();
//		Long lastLength = segment.getCalculatedLength();
//		Long currentLength = this.getCalculatedLength();
//                Double diff = 0D;
//                if(this.peakEntry.getValue() < segment.peakEntry.getValue()){
//                    diff = this.peakEntry.getValue()/segment.peakEntry.getValue();
//                }else{
//                    diff = segment.peakEntry.getValue()/this.peakEntry.getValue();
//                }
                
                double similarity = 0;
//                similarity += diff>.99?.5:0;
//		similarity = lastArea>currentArea*.99 && lastArea<currentArea?0.50:0;
//		similarity += currentArea>lastArea*.99 && currentArea<lastArea?0.50:0;
//		similarity += currentArea==lastArea?0.50:0;
//		similarity += lastLength==currentLength?0.25:0;
//		similarity += lastLength>currentLength*.9 && lastLength<currentLength?0.25:0;
//		similarity += currentLength>lastLength*.9 && currentLength<lastLength?0.25:0;
//		similarity += lastPeak==currentPeak?0.25:0;
		boolean similar = similarity>=.5;
		return similar;
	}

	//calculates
	public Double getCalculatedArea(ExtremeSegment current){
		Double area = 0D;
		for (Double f1: current.getValues()) {
			area += f1;
		}
		return area/current.getPeakEntries().size();
	}
	
	public Long getCalculatedLength(ExtremeSegment current){
		Long time = current.getValues().indextoMils(current.getValues().size());
		return time;
	}
}
