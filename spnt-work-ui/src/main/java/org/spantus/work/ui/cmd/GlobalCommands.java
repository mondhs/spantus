/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.ui.cmd;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class GlobalCommands {
	public enum file{open, newProject, openProject, saveProject, 
		currentProjectChanged, exportFile, importFile, currentSampleChanged,
		exit}
	public enum sample{play, stop, record, zoomin, zoomout, selectionChanged, reloadMarkers, reloadSampleChart, calculateSNR, calculateStatistics}
	public enum tool{option, reloadResources, autoSegmentation, sphinxRecognition, saveSegments, appendNoise, learn, recognize}
	public enum help{signalInfo, about, userGuide}
	
}
