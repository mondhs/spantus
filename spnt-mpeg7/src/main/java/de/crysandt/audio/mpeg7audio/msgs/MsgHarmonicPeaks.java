/*
  Copyright (c) 2004, Michele Bartolucci
  
  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.audio.mpeg7audio.msgs;

import java.util.*;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class MsgHarmonicPeaks extends Msg {
	
	private ArrayList<?> peaks;
	
	public MsgHarmonicPeaks ( int time,
							  int duration,
							  ArrayList<?> peaks) {
		super(time, duration);
		this.peaks = peaks;
	}
	
	public ArrayList<?> getPeaks() {
		return (ArrayList<?>)peaks.clone();
	}
	
	public int getHarmonicPeaksSize() {
	    return peaks.size();
	  }

}
