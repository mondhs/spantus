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
package org.spantus.work.ui;
/**
 * This was copied from spnt-work-ui. 
 * TODO: refactor code 
 * 
 * @author Mindaugas Greibus
 *
 *
 */
public enum ImageResourcesEnum {
	smallLogo("/org/spantus/work/ui/res/img/small_logo.png"),
	spntIcon("org/spantus/work/ui/res/img/icon.gif"),

	segmentScreenshot("org/spantus/work/ui/res/help/img/spnt-segment-screen.png"),
	
	open("org/spantus/work/ui/res/icon/gtk-media-eject.png"),
	refresh("org/spantus/work/ui/res/icon/gtk-view-refresh.png"),
	info("org/spantus/work/ui/res/icon/gtk-dialog-information.png"),
	play("org/spantus/work/ui/res/icon/gtk-media-playback-start.png"),
	stop("org/spantus/work/ui/res/icon/gtk-media-playback-stop.png"),
	record("org/spantus/work/ui/res/icon/gtk-media-playback-record.png"),
	preferences("org/spantus/work/ui/res/icon/gtk-preferences-system.png"),
	zoomin("org/spantus/work/ui/res/icon/gtk-zoom-in.png"),
	zoomout("org/spantus/work/ui/res/icon/gtk-zoom-out.png");
	
	private String code;
	
	private ImageResourcesEnum(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
}
