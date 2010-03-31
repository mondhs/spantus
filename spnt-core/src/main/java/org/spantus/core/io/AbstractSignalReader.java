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
package org.spantus.core.io;

import java.util.LinkedHashSet;
import java.util.Set;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * 
 *        Created 2010.03.26
 *
 */
public abstract class AbstractSignalReader implements ProcessedFrameLinstener,
		SignalReader{

	Set<ProcessedFrameLinstener> listeners;
	
	public void registerProcessedFrameLinstener(
			ProcessedFrameLinstener linstener) {
		getListeners().add(linstener);
	}

	public Set<ProcessedFrameLinstener> getListeners() {
		if(listeners == null){
			listeners = new LinkedHashSet<ProcessedFrameLinstener>();
		}
		return listeners;
	}
	
	public void processed(Long current, Long total) {
		for (ProcessedFrameLinstener linstener : getListeners()) {
			linstener.processed(current, total);
		}
	}

	public void started(Long total) {
		for (ProcessedFrameLinstener linstener : getListeners()) {
			linstener.started(total);
		}
	}
	public void ended() {
		for (ProcessedFrameLinstener linstener : getListeners()) {
			linstener.ended();
		}
	}

}
