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
package org.spantus.segment.online;

import java.text.MessageFormat;

import org.spantus.segment.offline.BaseDecisionSegmentatorParam;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 1, 2010
 *
 */
public class OnlineDecisionSegmentatorParam extends BaseDecisionSegmentatorParam {
	
	private Long expandStart;
	private Long expandEnd;
	
	
	
	public Long getExpandStart() {
		if(expandStart == null){
			expandStart = 0L;
		}
		return expandStart;
	}

	public void setExpandStart(Long latency) {
		this.expandStart = latency;
	}
	
	public Long getExpandEnd() {
		if(expandEnd == null){
			expandEnd = 0L;
		}
		return expandEnd;
	}
	
	public void setExpandEnd(Long expandEnd) {
		this.expandEnd = expandEnd;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0}[minLength: {1}; minSpace: {2}; expandStart: {3}; expandEnd{4}]", OnlineDecisionSegmentatorParam.class.getSimpleName(),getMinLength(),getMinSpace(), getExpandStart(), getExpandEnd());
	}

}
