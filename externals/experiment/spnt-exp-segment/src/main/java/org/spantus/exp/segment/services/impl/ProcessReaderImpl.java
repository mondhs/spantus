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
package org.spantus.exp.segment.services.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.threshold.IThreshold;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.services.ProcessReader;
import org.spantus.exp.threshold.SampleEstimationThreshold;
import org.spantus.extractor.ExtractorInputReader;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Feb 12, 2009
 *
 */
public class ProcessReaderImpl implements ProcessReader {

	Logger log = Logger.getLogger(getClass());
	
	public SampleInfo processReader(IExtractorInputReader reader, ProcessReaderInfo processReaderInfo) {
		Assert.isTrue(reader != null, "reader is null");
		SampleInfo info = new SampleInfo();
		Set<IExtractor> extractors = reader.getExtractorRegister();
		Set<IThreshold> thresholds = new HashSet<IThreshold>();
		for (IExtractor extractor : extractors) {
			if(extractor.getName().endsWith(ExtractorEnum.SIGNAL_EXTRACTOR.name())){
				continue;
			}
			SampleEstimationThreshold threshold = new SampleEstimationThreshold();
			if(processReaderInfo.getThresholdCoef()!=null){
				threshold.setCoef(processReaderInfo.getThresholdCoef().floatValue());
			}
			threshold.setExtractor(extractor);
			threshold.getOutputValues();
			thresholds.add(threshold);
			log.debug("added threshold: " + threshold.getName());
		}
		for (IExtractorVector extractor3d : reader.getExtractorRegister3D()) {
			FrameVectorValues vals = extractor3d.getOutputValues().transform();
			if(!canBeProcessed(extractor3d))continue;
			
			int i = 0;
			for (FrameValues frameValues : vals) {
				frameValues.setSampleRate(vals.getSampleRate());
				SampleEstimationThreshold threshold = new SampleEstimationThreshold();
				if(processReaderInfo.getThresholdCoef()!=null){
					threshold.setCoef(processReaderInfo.getThresholdCoef().floatValue());
				}
				ExtractorOutputHolder extractor = new ExtractorOutputHolder();
				extractor.setConfig(extractor3d.getConfig());
				extractor.setOutputValues(frameValues);
				extractor.setName(extractor3d.getName()+(i++));
				threshold.setExtractor(extractor);
				threshold.getOutputValues();
				thresholds.add(threshold);
//				log.debug("added threshold: " + threshold.getName());
			}
			
		}
		IExtractorInputReader newReader = new ExtractorInputReader();
		newReader.getExtractorRegister().addAll(thresholds);
		info.setReader(newReader);
		info.getThresholds().clear();
		info.getThresholds().addAll(thresholds);
		return info;
	}

	/**
	 * 
	 */
	public Set<IThreshold> getFilterThresholdByName(Set<IThreshold> set, String endsWith) {
		Set<IThreshold> thresholds = new HashSet<IThreshold>();
		for (IThreshold iThreshold : set) {
			if(iThreshold.getName().endsWith(endsWith)){
				thresholds.add(iThreshold);
			}
		}
		return thresholds;
	}
	/**
	 * 
	 */
	public Iterable<Set<String>> generateAllCompbinations(Set<? extends IGeneralExtractor> fullSet, int combinationDepth){
		log.debug("starting generate {0}", combinationDepth);
		Set<Set<String>> allCombinations = generateList(fullSet, combinationDepth);
		log.debug("generated {0}", allCombinations.size());
		return allCombinations;
	}
	
	public String getName(IGeneralExtractor threshold) {
		String _key = threshold.getName();
		_key = _key.replaceAll("(BUFFERED_)", "");
		_key = _key.replaceAll("(_EXTRACTOR)", "");
		return _key;
	}
	/**
	 * 
	 */
	public <T extends IGeneralExtractor> Set<T> getThresholdSet(Set<T> thresholds, 
			Set<String> thresholdNames){
		Set<T> generatedSet = new LinkedHashSet<T>();
		for (T threshold : thresholds) {
			if(thresholdNames.contains(threshold.getName())){
				generatedSet.add(threshold);
			}
		}
		Assert.isTrue(generatedSet.size() == thresholdNames.size(), "Atleast one threashold should be added");
		return generatedSet;
	}
	
	//Protected
	
	protected Set<Set<String>> generateList(Set<? extends IGeneralExtractor> fullSet, 
			int combinationDepth){
		Set<Set<String>> allCombinations = new LinkedHashSet<Set<String>>();
		int start = 
//			1
			combinationDepth
			;
		for (int i = start; i <= combinationDepth; i++) {
			generateList(fullSet, allCombinations, 
					new LinkedHashSet<IGeneralExtractor>(), i);	
		}
		return allCombinations;
		
	}
	
	protected void generateList(Set<? extends IGeneralExtractor> fullSet, 
			Set<Set<String>> allCombinations, Set<IGeneralExtractor> top, int combinationDepth){
		Set<IGeneralExtractor> shrinkedFullSet = new LinkedHashSet<IGeneralExtractor>(
				fullSet);
		for (IGeneralExtractor
				threshold : fullSet) {
			Set<IGeneralExtractor> set = new LinkedHashSet<IGeneralExtractor>();
			shrinkedFullSet.remove(threshold);
			set.addAll(top);
			set.add(threshold);
			if(combinationDepth == 1){
				addToCombinationSet(allCombinations, set);
			}else{
				generateList(shrinkedFullSet, allCombinations, set, combinationDepth-1);
			}
			
		}
	}
	
	protected boolean addToCombinationSet(Set<Set<String>> allCombinations, Set<IGeneralExtractor> set){
		return allCombinations.add(getNameSet(set));
	}
	
	protected Set<String> getNameSet(Set<IGeneralExtractor> set){
		Set<String> rtnSet = new TreeSet<String>();
		for (IGeneralExtractor threshold : set) {
			rtnSet.add(getName(threshold));
		}
		return rtnSet;	
	}
	
	protected String getName(Set<IGeneralExtractor> set){
		StringBuffer buf = new StringBuffer();
		String separator = "";
		Set<String> strSet = getNameSet(set);
		for (String thresholdName : strSet) {
			buf.append(separator).append(thresholdName);
			separator = " ";
		}
		return buf.toString();	
	}
	
	protected boolean canBeProcessed(IExtractorVector extractor3d){
//		if(true) return false;
		if(true) return true;
		String name = extractor3d.getName();
		if(extractor3d.getOutputValues().iterator().next().size()>13){
			return false;
		}
		if(//name.matches(".*SPECTRAL_FLUX.*") ||
				name.matches(".*LPC.*") ||
				name.matches(".*MFCC.*") ||
				name.matches(".*FFT.*")){
			return false;
		}
		return true;
	}
}
