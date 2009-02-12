/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
	int samples = 0;
	
	@Override
	protected Float calculateState(Long sample, Float windowValue, Float threshold) {
		Float val = super.calculateState(sample, windowValue, threshold);
		if(lastVal.compareTo(val)!=0){
			if(val == 0){
				fireSilence(samples/getExtractorSampleRate());
			}else{
				fireSignal(samples/getExtractorSampleRate());
			}
			samples = 0;
		}
		lastVal = val;
		samples++;
		return val;
	}
	@Override
	public Float getCoef() {
		if(coef == null){
			coef = 1.3f;//*30%
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
		log.error("signal: " + samples/getExtractorSampleRate());
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
