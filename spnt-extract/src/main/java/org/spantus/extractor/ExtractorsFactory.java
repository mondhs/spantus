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
package org.spantus.extractor;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import org.spantus.core.extractor.DefaultExtractorInputReader;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.utils.Assert;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 3, 2009
 *
 */
public abstract class ExtractorsFactory {
	
	public static final Integer DEFAULT_WINDOW_LENGHT=33;
	public static final Integer DEFAULT_WINDOW_OVERLAP=66;
	
	public static IExtractorInputReader createReader(SignalFormat format){
		IExtractorInputReader reader = createNormalizedReader();
		reader.setConfig(createConfig(format));
		return reader;
	}
	
	public static IExtractorInputReader createReader(IExtractorConfig extractorConfig){
		IExtractorInputReader reader = createNormalizedReader();
		reader.setConfig(extractorConfig);
		return reader;
	}
	
	public static IExtractorInputReader createReader(AudioFormat format, int windowLengthInMilSec, int overlapInPerc){
		IExtractorInputReader reader = createNormalizedReader();
		reader.setConfig(createConfig(format, windowLengthInMilSec, overlapInPerc));
		return reader;
	}
	public static IExtractorInputReader createReader(AudioFormat format){
		IExtractorInputReader reader = createNormalizedReader();
		reader.setConfig(createConfig(format, DEFAULT_WINDOW_LENGHT, DEFAULT_WINDOW_OVERLAP));
		return reader;
	}
	
	public static IExtractorConfig createConfig(AudioFormat format, int windowLengthInMilSec, int overlapInPerc){
		return ExtractorConfigUtil.defaultConfig((double) format.getSampleRate(), windowLengthInMilSec,  overlapInPerc);
	}
	public static IExtractorConfig createConfig(SignalFormat format){
		return ExtractorConfigUtil.defaultConfig(format.getSampleRate());
	}
	
	public static IExtractorInputReader createReader(AudioFileFormat format, int windowLengthInMilSec, int overlapInPerc){
		Assert.isTrue(format!=null,"audio file format cannot be null");
		return createReader(format.getFormat(), windowLengthInMilSec, overlapInPerc);
	}
	public static IExtractorInputReader createReader(AudioFileFormat format){
		Assert.isTrue(format!=null,"audio file format cannot be null");
		return createReader(format.getFormat(), DEFAULT_WINDOW_LENGHT, DEFAULT_WINDOW_OVERLAP);
	}
	public static IExtractorInputReader createNormalizedReader(){
		return new DefaultExtractorInputReader();
	}

    

}
