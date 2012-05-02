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
package org.spantus.chart.bean;

import org.spantus.ui.chart.VectorSeriesColorEnum;

/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created May 21, 2008
 *
 */
public class ChartInfo {
	
	private boolean playable;
	
	private boolean exportable;
	
	private boolean printable;
	
	private boolean selfZoomable = true;
	
	private Boolean grid = null;
	
	private String colorSchema = VectorSeriesColorEnum.blackWhite.name();
	
	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean playable) {
		this.playable = playable;
	}

	public boolean isExportable() {
		return exportable;
	}

	public void setExportable(boolean exportable) {
		this.exportable = exportable;
	}

	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}
	public boolean isSelfZoomable() {
		return selfZoomable;
	}

	public void setSelfZoomable(boolean selfZoomable) {
		this.selfZoomable = selfZoomable;
	}

	public Boolean getGrid() {
		return grid;
	}

	public void setGrid(Boolean grid) {
		this.grid = grid;
	}

	public String getColorSchema() {
		return colorSchema;
	}

	public void setColorSchema(String colorSchema) {
		this.colorSchema = colorSchema;
	}
	
}
