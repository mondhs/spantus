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
package org.spantus.logger;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public interface ILogger {
	
	public abstract void debug(String pattern, Object... arguments);

	public abstract void debug(String str);

	public abstract void info(String pattern, Object... arguments);

	public abstract void info(String str);

	public abstract void error(String str);
        
        public abstract void error(String pattern, Object... arguments);

	public abstract void error(Exception e);
        
	public abstract void error(String str, Throwable e);

	public abstract void fatal(String str);

	public abstract boolean isDebugMode();

}