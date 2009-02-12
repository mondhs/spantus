/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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
package org.spantus.mpeg7.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;

import de.crysandt.audio.mpeg7audio.ConfigDefault;

/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 * Created May 21, 2008
 * 
 */
public abstract class Mpeg7ConfigUtil {
	static Map<Mpeg7ExtractorEnum, String> enumMapping = new HashMap<Mpeg7ExtractorEnum, String>();
	static {
		enumMapping.put(Mpeg7ExtractorEnum.AudioSpectrumBasis,
				"AudioSpectrumBasisProjection");
		enumMapping.put(Mpeg7ExtractorEnum.AudioSpectrumProjection,
				"AudioSpectrumBasisProjection");
		enumMapping.put(Mpeg7ExtractorEnum.AudioSpectrumCentroid,
			"AudioSpectrumCentroidSpread");
		enumMapping.put(Mpeg7ExtractorEnum.AudioSpectrumSpread,
			"AudioSpectrumCentroidSpread");

		
	}

	public static ConfigDefault getConfig(IExtractorConfig mp7conf) {
		ConfigDefault conf = new ConfigDefault();
		if (mp7conf == null) {
			return conf;
		}
		conf.enableAll(false);
		conf.setValue("AudioPower", "logScale", true);
//		conf.setValue("Resizer", "HopSize", 20);
		for (Mpeg7ExtractorEnum extractor : Mpeg7ExtractorEnum.values()) {
			conf.setValue(getEnumMapping(extractor), "enable", mp7conf.getExtractors()
					.contains(extractor.name()));
		}
		if(mp7conf.getExtractors().contains(Mpeg7ExtractorEnum.AudioSpectrumBasis.name()) ){
			conf.setValue(getEnumMapping(Mpeg7ExtractorEnum.AudioSpectrumBasis), "enable", true);
		}else if(mp7conf.getExtractors().contains(Mpeg7ExtractorEnum.AudioSpectrumProjection.name()) ){
			conf.setValue(getEnumMapping(Mpeg7ExtractorEnum.AudioSpectrumProjection), "enable", true);
		}
		if(mp7conf.getExtractors().contains(Mpeg7ExtractorEnum.AudioSpectrumCentroid.name()) ){
			conf.setValue(getEnumMapping(Mpeg7ExtractorEnum.AudioSpectrumCentroid), "enable", true);
		}else if(mp7conf.getExtractors().contains(Mpeg7ExtractorEnum.AudioSpectrumSpread.name()) ){
			conf.setValue(getEnumMapping(Mpeg7ExtractorEnum.AudioSpectrumSpread), "enable", true);
		}
		return conf;
	}
	public static String getEnumMapping(Mpeg7ExtractorEnum extractor){
		if(enumMapping.containsKey(extractor)){
			return enumMapping.get(extractor);
		}
		return extractor.name();
	}

	public static IExtractorConfig createConfig(Mpeg7ExtractorEnum[] extractors) {
		Mpeg7ExtractorConfig conf = new Mpeg7ExtractorConfig();
		for (Mpeg7ExtractorEnum mpeg7extrEnum : extractors) {
			conf.getExtractors().add(mpeg7extrEnum.name());
		}
		return conf;
	}
	public static IExtractorConfig createConfig(Collection<Mpeg7ExtractorEnum> extractors) {
		Mpeg7ExtractorConfig conf = new Mpeg7ExtractorConfig();
		for (Mpeg7ExtractorEnum mpeg7extrEnum : extractors) {
			conf.getExtractors().add(mpeg7extrEnum.name());
		}
		return conf;
	}

	public static Set<Mpeg7ExtractorEnum> convertExtractorsEnum(Set<String> extrStrs){
		Set<Mpeg7ExtractorEnum> mp7extrs = new HashSet<Mpeg7ExtractorEnum>();
		for (String string : extrStrs) {
			mp7extrs.add(Mpeg7ExtractorEnum.valueOf(string));
		}
		return mp7extrs;
		
	}
	public static Set<String> convertExtractors(Set<Mpeg7ExtractorEnum> mp7extrs){
		Set<String> extrStrs = new HashSet<String>();
		for (Mpeg7ExtractorEnum mp7extr: mp7extrs) {
			extrStrs.add(mp7extr.name());
		}
		return extrStrs;
		
	}	
	/**
	 * 
	 * @param reader
	 * @param conf
	 */
	public static void postprocess(IExtractorInputReader reader, IExtractorConfig conf){
		Set<Mpeg7ExtractorEnum> extrs = convertExtractorsEnum(conf.getExtractors());
		if(extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumBasis) && 
				!extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumProjection)){
			reader.getExtractorRegister3D().remove(reader.getExtractorRegister3D().toArray()[0]);
		}else if(!extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumBasis) && 
				extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumProjection)){
			reader.getExtractorRegister3D().remove(reader.getExtractorRegister3D().toArray()[1]);			
		}
		if(extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumSpread) && 
				!extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumCentroid)){
			reader.getExtractorRegister().remove(reader.getExtractorRegister().toArray()[0]);
		}else if(!extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumSpread) && 
				extrs.contains(Mpeg7ExtractorEnum.AudioSpectrumCentroid)){
			reader.getExtractorRegister().remove(reader.getExtractorRegister().toArray()[1]);			
		}

		
		
	}

}
