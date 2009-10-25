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
package org.spantus.logger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class SimpleLogger implements ILogger {
    
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int ERROR = 3;
    public static final int FATAL = 4;
    public static final HashMap<Integer, String> labels = new HashMap<Integer, String>();
    private static PrintStream out =  System.out;
    private static PrintStream err =  System.err;
    

    private static int logMode = DEBUG;
    private Class<?> logClass = null;
    
    static{
        labels.put(DEBUG,"DEBUG");
        labels.put(INFO,"INFO");
        labels.put(ERROR,"ERROR");
        labels.put(FATAL,"FATAL");
    }
    
    public SimpleLogger(Class<?> logClass){
        this.logClass = logClass;
    }
    
    void log(int level, String message){
        if(level >= logMode){
            String levelStr = labels.get(level);
            String classNameStr = getSimpleName(logClass);
            
            String result = MessageFormat.format("{0,time,kk:mm:ss.SSS} {1} [{2}] {3}",
            		 new Date(),
                     levelStr,
                     classNameStr,
                     message);
            
            if(level == ERROR || level == FATAL){
                err.println(result);
            }else{
                out.println(result);    
            }
            

        }
    }

    public void debug(String pattern, Object... arguments ){
    	if(isDebugMode()){
    		log(DEBUG,MessageFormat.format(pattern, arguments));
    	}
    }
    
    public void debug(String str){
    	if(isDebugMode()){
    		log(DEBUG,str);
    	}
    }
    
    
    public void info(String pattern, Object... arguments ){
    	if(INFO >= logMode){
    		log(INFO,MessageFormat.format(pattern, arguments));
    	}
    }

    public void info(String str){
    	if(INFO >= logMode){
    		log(INFO,str);
    	}
    }
    
    public void error(String str){
        log(ERROR,str);
    }
    public void error(Exception e){
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
        log(ERROR,sw.toString());
    }
    public void fatal(String str){
        log(FATAL,str);
    }
    
 
    
    public static String getSimpleName(Class<?> argClass){
        String rtn = "";
        rtn = argClass.getName();
        rtn = rtn.substring(rtn.lastIndexOf(".")+1);
        return rtn;
        
    }

    /**
     * @return Returns the logMode.
     */
    public int getLogMode() {
        return logMode;
    }
    public boolean isDebugMode() {
        return DEBUG >= getLogMode();
    }

	@SuppressWarnings("static-access")
	public void setLogMode(int logMode) {
		this.logMode = logMode;
//		debug("Log mode set to:" + logMode);
	}

}
