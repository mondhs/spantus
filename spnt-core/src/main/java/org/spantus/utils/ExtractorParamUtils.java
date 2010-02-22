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
package org.spantus.utils;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.extractor.ExtractorParam;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 22, 2010
 *
 */
public abstract class ExtractorParamUtils {
	public enum commonParam{thresholdType, threasholdCoef};
	
	public static Boolean getBoolean(ExtractorParam param,String propertyName){
		return (Boolean)param.getProperties().get(propertyName);
	}
	public static Boolean getBoolean(ExtractorParam param,String propertyName, Boolean defaultVal){
		if(param == null){
			return defaultVal;
		}
		Boolean val = getBoolean(param, propertyName);
		if(val == null){
			val = defaultVal;
		}
		return val;
	}

	public static void setBoolean(ExtractorParam param,String propertyName, Boolean val){
		param.getProperties().put(propertyName, val);
	}

	public static String getString(ExtractorParam param,String propertyName){
		return (String)param.getProperties().get(propertyName);
	}
	public static String getString(ExtractorParam param,String propertyName, String defaultVal){
		return ExtractorParamUtils.<String>getValue(param, propertyName, defaultVal);
	}
	public static <T> void setValue(ExtractorParam param,String propertyName, T val){
		param.getProperties().put(propertyName, val);
	}

	@SuppressWarnings("unchecked")
	public static <E> E getValue(ExtractorParam param,String propertyName){
		return (E)param.getProperties().get(propertyName);
	}
	public static <E> E getValue(ExtractorParam param,String propertyName, E defaultVal){
		E val = ExtractorParamUtils.<E>getValue(param, propertyName);
		if(val == null){
			val = defaultVal;
		}
		return val;
	}
	public static void setString(ExtractorParam param,String propertyName, String val){
		param.getProperties().put(propertyName, val);
	}

	
	public static ExtractorParam getSafeParam(Map<String, ExtractorParam> paramMap, String key){
		if(paramMap == null){
			paramMap = new HashMap<String, ExtractorParam>();
		}
		ExtractorParam param = paramMap.get(key);
		if(param == null){
			param = new ExtractorParam();
			paramMap.put(key, param);
		}
		return param;
	}
}
