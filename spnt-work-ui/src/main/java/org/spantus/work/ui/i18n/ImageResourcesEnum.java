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
package org.spantus.work.ui.i18n;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.06.10
 *
 */
public enum ImageResourcesEnum {
	smallLogo("/org/spantus/work/ui/img/small_logo.png"),
	spntIcon("org/spantus/work/ui/img/icon.gif"),

	segmentScreenshot("org/spantus/work/ui/res/help/img/spnt-segment-screen.png"),
	
	open("org/spantus/work/ui/icon/gtk-media-eject.png"),
	refresh("org/spantus/work/ui/icon/gtk-view-refresh.png"),
	play("org/spantus/work/ui/icon/gtk-media-playback-start.png"),
	stop("org/spantus/work/ui/icon/gtk-media-playback-stop.png"),
	record("org/spantus/work/ui/icon/gtk-media-playback-record.png"),
	preferences("org/spantus/work/ui/icon/gtk-preferences-system.png"),
	zoomin("org/spantus/work/ui/icon/gtk-zoom-in.png"),
	zoomout("org/spantus/work/ui/icon/gtk-zoom-out.png");
	
	private String code;
	
	private ImageResourcesEnum(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
}
