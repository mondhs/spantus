
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
package org.spantus.work.ui.dto;

import java.awt.Dimension;
import java.awt.Point;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.26
 *
 */
public class EnviromentRepresentation {
	
	private Dimension mainWindowDimension;
	private Point location;
	private int mainWindowState;//maximized or normal
	private Boolean grid;//chart grids
	private String vectorChartColorTypes;//chart vector colors
	private Boolean popupNotifications;
	private Boolean autoSegmentation;
	private Boolean advancedMode;
	private String spantusVersion;
	
	private String laf;
        private Boolean autoRecognition;

	public Dimension getMainWindowDimension() {
		return mainWindowDimension;
	}



	public void setMainWindowDimension(Dimension clientWindow) {
		this.mainWindowDimension = clientWindow;
	}

	

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Boolean getGrid() {
		return grid;
	}

	public void setGrid(Boolean grid) {
		this.grid = grid;
	}

	public String getLaf() {
		return laf;
	}

	public void setLaf(String laf) {
		this.laf = laf;
	}

	public Boolean getPopupNotifications() {
		return popupNotifications;
	}

	public void setPopupNotifications(Boolean popupNotifications) {
		this.popupNotifications = popupNotifications;
	}

	public int getMainWindowState() {
		return mainWindowState;
	}

	public void setMainWindowState(int frameWindowState) {
		this.mainWindowState = frameWindowState;
	}

	public Boolean getAutoSegmentation() {
		return autoSegmentation;
	}

	public void setAutoSegmentation(Boolean autoSegmentation) {
		this.autoSegmentation = autoSegmentation;
	}

	public String getVectorChartColorTypes() {
		return vectorChartColorTypes;
	}

	public void setVectorChartColorTypes(String vectorChartColorTypes) {
		this.vectorChartColorTypes = vectorChartColorTypes;
	}

	public Boolean getAdvancedMode() {
		return advancedMode;
	}

	public void setAdvancedMode(Boolean advancedMode) {
		this.advancedMode = advancedMode;
	}

	public String getSpantusVersion() {
		return spantusVersion;
	}

	public void setSpantusVersion(String spantusVersion) {
		this.spantusVersion = spantusVersion;
	}

        public Boolean getAutoRecognition() {
               return autoRecognition;
        }
        public void setAutoRecognition(Boolean autoRecognition) {
            this.autoRecognition = autoRecognition;
        }

}
