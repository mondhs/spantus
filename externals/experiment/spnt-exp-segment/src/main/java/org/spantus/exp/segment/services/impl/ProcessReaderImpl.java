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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.ExtractorOutputHolder;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorInputReader;
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
	 * @param extractor3d
	 * @return
	 */
	protected boolean canBeProcessed(IExtractorVector extractor3d){
//		if(true) return false;
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
	
	public Map<String, Set<String>> generateAllCompbinations(Set<? extends IGeneralExtractor> singleSet, int combinationDepth){
		log.debug("starting generate {0}", combinationDepth);
		Set<Set<IGeneralExtractor>> allCombinations = new LinkedHashSet<Set<IGeneralExtractor>>();
		generateList(singleSet, allCombinations, new LinkedHashSet<IGeneralExtractor>(), combinationDepth);
		log.debug("generated {0}", allCombinations.size());
		return getCombinationMap(allCombinations);
	}
	protected Map<String, Set<String>>  getCombinationMap(Set<Set<IGeneralExtractor>> allCombinations){
		Map<String, Set<String>> allCombinationsMap = new LinkedHashMap< String, Set<String>>();
		for (Set<IGeneralExtractor> set : allCombinations) {
			Set<String> nameSet = new LinkedHashSet<String>();
			for (IGeneralExtractor thr : set) {
				nameSet.add(thr.getName());
			}
			allCombinationsMap.put(getName(set), nameSet);
		} 
		return allCombinationsMap; 
	}
	
	protected void generateList(Set<? extends IGeneralExtractor> singleSet, 
			Set<Set<IGeneralExtractor>> allCombinations, Set<IGeneralExtractor> top, int combinationDepth){
		for (IGeneralExtractor
				threshold : singleSet) {
			Set<IGeneralExtractor> set = new LinkedHashSet<IGeneralExtractor>();
			set.addAll(top);
			set.add(threshold);
			if(combinationDepth == 1){
				allCombinations.add(set);
			}else{
				generateList(singleSet,allCombinations,set, combinationDepth-1);
			}
			
		}
	}
	protected String getName(Set<IGeneralExtractor> set){
		StringBuffer buf = new StringBuffer();
		String separator = "";
		for (IGeneralExtractor threshold : set) {
			buf.append(separator).append(getName(threshold));
			separator = " ";
		}
		return buf.toString();	
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
		Assert.isTrue(generatedSet.size()>0, "Atleast one threashold should be added");
		return generatedSet;
	}
}
