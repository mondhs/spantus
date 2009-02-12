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

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.spantus.core.domain.Entity;
/**
 * 
 * @author Mindaugas Greibus
 * @singe 0.0.1
 *
 */
public class Experiment extends Entity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Set<ExperimentResult> experimentResults;
	
	Date experimentDate;

	public Set<ExperimentResult> getExperimentResults() {
		if(experimentResults == null){
			experimentResults = new LinkedHashSet<ExperimentResult>();
		}
		return experimentResults;
	}

	public void setExperimentResults(Set<ExperimentResult> experimentResults) {
		this.experimentResults = experimentResults;
	}

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

}
