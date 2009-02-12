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

import org.spantus.core.domain.Entity;

/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 *
 */
public class ExperimentResult extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Long experimentID;
	
	//Date experimentDate;
	
	String resource;
	
	String features;
	
	Float totalResult;


	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public Float getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(Float totalResult) {
		this.totalResult = totalResult;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + features + ":" + getTotalResult() + "]";
	}

	public Long getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(Long experimentID) {
		this.experimentID = experimentID;
	}

}
