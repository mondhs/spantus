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
package org.spantus.work.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.marker.Marker;
import org.spantus.core.threshold.ClassifierEnum;
import org.spantus.core.threshold.IClassifier;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.extractor.segments.online.ExtremeOnlineRuleClassifier;
import org.spantus.logger.Logger;

/**
 *
 * @author mondhs
 */
public class ExtractorReaderServiceImpl implements ExtractorReaderService {

    private static Logger log = Logger.getLogger(ExtractorReaderServiceImpl.class);
    
    private int windowLengthInMilSec = ExtractorsFactory.DEFAULT_WINDOW_LENGHT;
    private int overlapInPerc = ExtractorsFactory.DEFAULT_WINDOW_OVERLAP;

	private String rulePath;

	private boolean rulesTurnedOn;

    public FrameVectorValues findFeatureVectorValuesForMarker(IExtractorInputReader reader,
            Marker marker, String featureName) {
        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            //extractors can have prefixes, jus check if ends with
            if (!extractor.getName().endsWith(featureName)) {
                continue;
            }
            FrameVectorValues values = extractor.getOutputValues();
            Double fromIndex = (marker.getStart().doubleValue() * values.getSampleRate()) / 1000;
            Double toIndex = fromIndex + (marker.getLength().doubleValue() * values.getSampleRate()) / 1000;
            FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
            return fvv;
        }
        return null;
    }

    public Map<String, IValues> findAllVectorValuesForMarker(IExtractorInputReader reader, Marker marker) {
        Map<String, IValues> result = new HashMap<String, IValues>();

        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            //extractors can have prefixes, jus check if ends with
            FrameVectorValues values = extractor.getOutputValues();
            int endIndex = values.size()-1;
//            if(values.get(0).size()<=2){
//                continue;
//            }
            Double fromIndex = (marker.getStart().doubleValue() * values.getSampleRate()) / 1000;
            fromIndex = fromIndex < 0 ? 0 : fromIndex;
            Double toIndex = fromIndex + (marker.getLength().doubleValue() * values.getSampleRate()) / 1000;
            toIndex = endIndex < toIndex?endIndex:toIndex;
            FrameVectorValues fvv = values.subList(fromIndex.intValue(), toIndex.intValue());
            String key = preprocess(extractor.getName());
            result.put(key, fvv);
        }
        for (IExtractor extractor : reader.getExtractorRegister()) {
        	if(extractor.getName().endsWith(ExtractorEnum.SIGNAL_EXTRACTOR.name())){
        		continue;
        	}
            //extractors can have prefixes, just check if ends with
            FrameValues values = extractor.getOutputValues();
            int endIndex = values.size()-1;
            Double fromIndex = (marker.getStart().doubleValue() * values.getSampleRate()) / 1000;
            fromIndex = fromIndex < 0 ? 0 : fromIndex;
            Double toIndex = fromIndex + (marker.getLength().doubleValue() * values.getSampleRate()) / 1000;
            toIndex = endIndex < toIndex?endIndex:toIndex;
            FrameValues fv = values.subList(fromIndex.intValue(), toIndex.intValue());
            String key = preprocess(extractor.getName());
            result.put(key, fv);
        }

        return result;
    }
    
    /**
     * 
     */
	public Map<String, IValues> findAllVectorValuesForMarker(IExtractorInputReader reader) {
    	  Map<String, IValues> result = new HashMap<String, IValues>();
          for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
        	  String key = preprocess(extractor.getName());
        	  result.put(key, extractor.getOutputValues());
          }
          for (IExtractor extractor : reader.getExtractorRegister()) {
        	  if(extractor.getName().endsWith(ExtractorEnum.SIGNAL_EXTRACTOR.name())){
          		continue;
          	}
        	  String key = preprocess(extractor.getName());
        	  result.put(key, extractor.getOutputValues());
          }
          return result;
    }
	/**
	 * 
	 * @param name
	 * @return
	 */
	 private String preprocess(String name) {
			return name.replace("BUFFERED_", "");
		}
    
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReaderWithClassifier(ExtractorEnum[] extractors, 
            File inputFile) {
    	return createReaderWithClassifier(extractors, inputFile, null);
    }
    /**
     * 
     */
    public IExtractorInputReader createReaderWithClassifier(ExtractorEnum[] extractors, File inputFile, Map<String, ExtractorParam> params){
        URL inputUrl;
        try {
            inputUrl = inputFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        AudioReader audioReader = AudioReaderFactory.createAudioReader();
        IExtractorInputReader extractorReader = ExtractorsFactory.createReader(
                audioReader.getAudioFormat(inputUrl), getWindowLengthInMilSec(), getOverlapInPerc());
        log.debug("[createReaderWithClassifier] reader config{0}", extractorReader.getConfig() );
        List<IClassifier> classifiers = ExtractorUtils.
                registerThreshold(extractorReader, extractors, params, ClassifierEnum.rules);
//                registerThreshold(extractorReader, extractors, null);
		if (isRulesTurnedOn()) {
	        log.error("registering rules repo");
			for (IClassifier iClassifier : classifiers) {
				if (iClassifier instanceof ExtremeOnlineRuleClassifier) {
					WorkServiceFactory.udpateClassifierRuleBaseService(
							(ExtremeOnlineRuleClassifier) iClassifier,
							getRulePath());
				}
			}
		}
        
        log.debug("[createReaderWithClassifier] reader features{0}", extractorReader.getGeneralExtractor() );

        audioReader.readSignal(inputUrl, extractorReader);

        return extractorReader;
    }
    
    
    
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReader(ExtractorEnum[] extractors,
            File inputFile) {
        URL inputUrl;
        try {
            inputUrl = inputFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        AudioReader audioReader = AudioReaderFactory.createAudioReader();
        IExtractorInputReader extractorReader = ExtractorsFactory.createReader(
                audioReader.getAudioFormat(inputUrl), getWindowLengthInMilSec(), getOverlapInPerc());
        ExtractorUtils.register(extractorReader, extractors, null);
        audioReader.readSignal(inputUrl, extractorReader);

        return extractorReader;
    }
    
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReader(ExtractorEnum[] extractors,
            AudioInputStream ais) {
    	
    	AudioReader audioReader = AudioReaderFactory.createAudioReader();
        IExtractorInputReader extractorReader = ExtractorsFactory.createReader(
        		ais.getFormat(), getWindowLengthInMilSec(), getOverlapInPerc());
        ExtractorUtils.register(extractorReader, extractors, null);
        File tmpFile;
		try {
			tmpFile = File.createTempFile("test", ".wav");
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, tmpFile);
	        audioReader.readSignalSmoothed(tmpFile.toURI().toURL(), extractorReader);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	

        return extractorReader;
    }

    
    
    /**
     * 
     * @param extractors
     * @param inputFile
     * @return
     */
    public IExtractorInputReader createReaderAndSave(ExtractorEnum[] extractors,
            File inputFile) {
        
        IExtractorInputReader extractorReader = createReader(extractors, inputFile);
        //save
        WorkServiceFactory.createReaderDao().write(extractorReader, createExtactorFile(inputFile));
        return extractorReader;
    }

    /**
     * 
     * @param name
     * @param reader
     * @return
     */
    public IGeneralExtractor findExtractorByName(String name, IExtractorInputReader reader) {
        for (IExtractor extractor : reader.getExtractorRegister()) {
            if (extractor.getName().contains(name)) {
                return extractor;
            }
        }
        for (IExtractorVector extractor : reader.getExtractorRegister3D()) {
            if (extractor.getName().contains(name)) {
                return extractor;
            }
        }
        return null;
    }

    protected File createExtactorFile(File wavFile) {
        File newFile = new File(wavFile.getAbsoluteFile().toString() + ".sspnt.xml");
        return newFile;
    }

	public int getWindowLengthInMilSec() {
		return windowLengthInMilSec;
	}

	public void setWindowLengthInMilSec(int windowLengthInMilSec) {
		this.windowLengthInMilSec = windowLengthInMilSec;
	}

	public int getOverlapInPerc() {
		return overlapInPerc;
	}

	public void setOverlapInPerc(int overlapInPerc) {
		this.overlapInPerc = overlapInPerc;
	}

	public String getRulePath() {
		return rulePath;
	}

	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}

	public boolean isRulesTurnedOn() {
		return rulesTurnedOn;
	}

	public void setRulesTurnedOn(boolean rulesTurnedOn) {
		this.rulesTurnedOn = rulesTurnedOn;
	}
}
