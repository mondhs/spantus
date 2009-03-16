/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.exp.segment.services.impl;

import java.util.Iterator;

import org.spantus.core.FrameValues;
import org.spantus.core.marker.MarkerSet;
import org.spantus.exp.segment.beans.ComparisionResult;
import org.spantus.exp.segment.beans.ComparisionResultTia;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class MakerComparisonTIAImpl extends MakerComparisonImpl{
	/** 
	 * 
	 * TIA/EIA-136-250
	 * ftp.tiaonline.org/UWC136/136-250.pdf 

	 * 
	 */
	
	public ComparisionResult compare(MarkerSet original,MarkerSet test){
		ComparisionResultTia result = new ComparisionResultTia();
		result.setOriginal(createSequence(original)); 
		result.setTest(createSequence(test));
		result.setSequenceResult(compare(result));
		result.getParams().clear();
		result.getParams().putAll(analyze(test));
		return result;
	}
	
	protected FrameValues compare(ComparisionResultTia result){
		FrameValues seq = new FrameValues();
		seq.setSampleRate(result.getOriginal().getSampleRate());
		TIAComparitionResultCtx ctx = new TIAComparitionResultCtx();

		Iterator<Float> idealIter = result.getOriginal().iterator();
		Iterator<Float> testIter = result.getTest().iterator();
		boolean hasMore = true;
		
		FrameValues segment = null;
		while(hasMore){
			Float ideal = 0F;  
			Float test = 0F;
			hasMore = false;
			if(idealIter.hasNext()){
				ideal = idealIter.next();
				hasMore = true;
			}
			if(testIter.hasNext()){
				test = testIter.next();
				hasMore = true;
			}
			if(ideal == 1 && segment == null){
				segment=new FrameValues();
				segment.add(test);
			}else if(ideal == 1){
				segment.add(test);
			}else if(ideal == 0 && segment != null){
				processTestSegment(segment, ctx);
				segment = null;
			}
				
			ctx.idealVoiceFrameCount += ideal;
			ctx.testVoiceFrameCount += test;
			seq.add(test-ideal);
		}
		//VAF - Voice-Activity Factor
		Float idealVAF = ctx.idealVoiceFrameCount.floatValue() / result.getOriginal().size();
		Float testVAF = ctx.testVoiceFrameCount.floatValue() / result.getTest().size();
		
		//P clip_{onset}   The probability that speech frames are incorrectly designated as non-speech.
		//onset - the first 60 ms of the segment
		Float pc_on = ctx.voiceFrameOnsetFailed.floatValue()/ctx.voiceFrameOnsetTotal;
		//P clip_{steady} The probability that speech frames are incorrectly designated as non-speech 
		Float pc_ss = ctx.voiceFrameSteadyFailed.floatValue()/ctx.voiceFrameSteadyTotal;
		//P clip{offset}   The probability that speech frames are incorrectly designated as non-speech.			
		//offset - the last 60 ms of the segment
		Float pc_off = ctx.voiceFrameOffsetFailed.floatValue()/ctx.voiceFrameOnsetTotal;
		Float deltaVAF = Math.abs(testVAF - idealVAF)/idealVAF;
		
		result.setTotalResult(.3f*pc_on+.4f*pc_ss+.1f*pc_off+.2f*deltaVAF);
		result.setOnset(pc_on);
		result.setSteady(pc_ss);
		result.setOffset(pc_off);
		result.setDeltaVAF(deltaVAF);
		
		
		
		log.debug("TIA comparition result: {4}[Pc_on:{0}; Pd_ss:{1}; Pc_off:{2}; deltaVAF:{3}]",
				pc_on, pc_ss, pc_off, deltaVAF, result.getTotalResult());
		return seq;
	}
	
	public void processTestSegment(FrameValues segment, TIAComparitionResultCtx ctx){
		Integer offsetIndex =  segment.size()-1-3,
		onsetIndex =  3,
		i=0;
		for (Float f1 : segment) {
			if(i<onsetIndex){
				ctx.voiceFrameOnsetFailed += f1==0?1:0;
				ctx.voiceFrameOnsetTotal++;
			}if(i>onsetIndex && i<offsetIndex){
				ctx.voiceFrameSteadyFailed += f1==0?1:0;
				ctx.voiceFrameSteadyTotal++;
			}if(i>offsetIndex){
				ctx.voiceFrameOffsetFailed += f1==0?1:0;
				ctx.voiceFrameOffsetTotal++;
			}
			i++;
		}
		
	}
	
	
	class TIAComparitionResultCtx{
		protected Integer 
		voiceFrameOnsetTotal= 0,
		voiceFrameOffsetTotal= 0,
		voiceFrameSteadyTotal= 0, 
		voiceFrameOnsetFailed= 0,
		voiceFrameSteadyFailed= 0,
		voiceFrameOffsetFailed= 0,
		idealVoiceFrameCount =0, 
		testVoiceFrameCount = 0; 
	}
}
