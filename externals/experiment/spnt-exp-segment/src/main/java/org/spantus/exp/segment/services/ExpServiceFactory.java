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
package org.spantus.exp.segment.services;

import org.spantus.exp.segment.services.impl.FileProcessReaderImpl;
import org.spantus.exp.segment.services.impl.MakerComparisonTIAImpl;
import org.spantus.exp.segment.services.impl.ProcessReaderImpl;



/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Jun 11, 2008
 *
 */
public abstract class ExpServiceFactory {
	
	private static MakerComparison defaultMakerComparison;
	public static MakerComparison createMakerComparison(){
		if(defaultMakerComparison == null){
//			defaultMakerComparison = new MakerComparisonFisherImpl();
//			defaultMakerComparison = new MakerComparisonImpl();
			defaultMakerComparison = new MakerComparisonTIAImpl();
		}
		return defaultMakerComparison; 
	}
	private static ProcessReader defaultProcessReader;
	public static ProcessReader createProcessReader(){
		if(defaultProcessReader == null){
			ProcessReaderImpl pr = new FileProcessReaderImpl();
			defaultProcessReader = pr;
		}
		return defaultProcessReader; 
	}
	
}
