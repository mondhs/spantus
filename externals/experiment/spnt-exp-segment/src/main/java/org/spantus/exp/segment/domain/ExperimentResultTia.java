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
package org.spantus.exp.segment.domain;


/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 *
 */
public class ExperimentResultTia extends ExperimentResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Float onset;
	Float steady;
	Float offset;
	Float deltaVAF;
	public Float getOnset() {
		return onset;
	}
	public void setOnset(Float onset) {
		this.onset = onset;
	}
	public Float getSteady() {
		return steady;
	}
	public void setSteady(Float steady) {
		this.steady = steady;
	}
	public Float getOffset() {
		return offset;
	}
	public void setOffset(Float offset) {
		this.offset = offset;
	}
	public Float getDeltaVAF() {
		return deltaVAF;
	}
	public void setDeltaVAF(Float deltaVAF) {
		this.deltaVAF = deltaVAF;
	}
}
