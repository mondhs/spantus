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
package org.spantus.chart;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.spantus.chart.bean.ChartInfo;

/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.11
 *
 */
public abstract class AbstractSwingChart extends JPanel implements Chart {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChartInfo info;
	
	public AbstractSwingChart() {
		super(new BorderLayout());
	}
	
	public ChartInfo getCharInfo() {
		if(info == null){
			info = new ChartInfo();
		}
		return info;
	}
	
	public void setCharInfo(ChartInfo info) {
		this.info = info;
	}

	public abstract void changedZoom(float from, float length);
	public abstract void initialize();
}
