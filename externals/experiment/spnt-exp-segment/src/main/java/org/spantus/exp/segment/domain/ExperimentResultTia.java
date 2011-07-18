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
	
	Double onset;
	Double steady;
	Double offset;
	Double deltaVAF;
	public Double getOnset() {
		return onset;
	}
	public void setOnset(Double onset) {
		this.onset = onset;
	}
	public Double getSteady() {
		return steady;
	}
	public void setSteady(Double steady) {
		this.steady = steady;
	}
	public Double getOffset() {
		return offset;
	}
	public void setOffset(Double offset) {
		this.offset = offset;
	}
	public Double getDeltaVAF() {
		return deltaVAF;
	}
	public void setDeltaVAF(Double deltaVAF) {
		this.deltaVAF = deltaVAF;
	}
}
