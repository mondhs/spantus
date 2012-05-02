/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2010, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 */
package org.spantus.math.dtw.abeel.dtw;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class TimeWarpInfo {
	
    private final double distance;

    private final WarpPath path;
    
    private RealMatrix costMatrix;
    private StatisticalSummary statisticalSummary;

    public TimeWarpInfo(double dist, WarpPath wp) {
        distance = dist;
        path = wp;
    }

    public double getDistance() {
        return distance;
    }

    public WarpPath getPath() {
        return path;
    }
    

    public String toString() {
        return "(Warp Distance=" + distance + ", Warp Path=" + path + ")";
    }

	public RealMatrix getCostMatrix() {
		return costMatrix;
	}

	public void setCostMatrix(RealMatrix costMatrix) {
		this.costMatrix = costMatrix;
	}

	public StatisticalSummary getStatisticalSummary() {
		return statisticalSummary;
	}

	public void setStatisticalSummary(StatisticalSummary statisticalSummary) {
		this.statisticalSummary = statisticalSummary;
	}



}