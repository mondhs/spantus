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

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.extractor.impl.FFTExtractor;
import org.spantus.extractor.impl.FFTExtractorCached;
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
	
	public static IExtractorInputReader createReader(SignalFormat format){
		ExtractorInputReader reader = new ExtractorInputReader();
		reader.setConfig(createConfig(format));
		return reader;
	}
	
	public static IExtractorInputReader createReader(IExtractorConfig extractorConfig){
		ExtractorInputReader reader = new ExtractorInputReader();
		reader.setConfig(extractorConfig);
		return reader;
	}
	
	public static IExtractorInputReader createReader(AudioFormat format){
		ExtractorInputReader reader = new ExtractorInputReader();
		reader.setConfig(createConfig(format));
		return reader;
	}
	
	public static IExtractorConfig createConfig(AudioFormat format){
		return ExtractorConfigUtil.defaultConfig(format.getSampleRate());
	}
	public static IExtractorConfig createConfig(SignalFormat format){
		return ExtractorConfigUtil.defaultConfig(format.getSampleRate());
	}
	
	public static IExtractorInputReader createReader(AudioFileFormat format){
		Assert.isTrue(format!=null,"audio file format cannot be null");
		return createReader(format.getFormat());
	}
	public static IExtractorInputReader createNormalizedReader(){
		return new ExtractorInputReader();
	}

    public static FFTExtractor createFftExtractor(){
            return new FFTExtractorCached();
    }

}
