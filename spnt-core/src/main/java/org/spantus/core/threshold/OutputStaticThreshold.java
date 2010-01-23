/**
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

package org.spantus.core.threshold;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.11.27
 *
 */
public class OutputStaticThreshold extends StaticThreshold {
	
	Logger log = Logger.getLogger(getClass());
	
	Float lastVal = Float.valueOf(0f);
	
	
	@Override
	protected void calculateState(Long sample, Float windowValue, Float threshold) {
		Marker lastMarker = getMarker();
		super.calculateState(sample, windowValue, threshold);
		
		if(getMarker() != null && getMarker().getExtractionData().getStartSampleNum() == sample){
				fireSilence(getMarker().getStart());
		//for end use lastMarker as current marker is reseted
		}else if(lastMarker !=null &&  lastMarker.getExtractionData().getLengthSampleNum() != null){
				fireSignal(lastMarker.getStart()+lastMarker.getLength());
		}
	}
	@Override
	public Float getCoef() {
		if(super.getCoef() == null){
			setCoef(1.3f);//*30%
		}
		return super.getCoef();
	}
	
	protected void fireSilence(float time){
		log.error("silence: " + time);
		try {
			getWriter().write("L");
			getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void fireSignal(float time){
		log.debug("signal: " + time);
		try {
			getWriter().write("H");
			getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	BufferedWriter out = null;
	public Writer getWriter() throws IOException{
		if(out == null){
				out = new BufferedWriter(new PrintWriter(System.out));
		}
		return out;
	}
	
}
