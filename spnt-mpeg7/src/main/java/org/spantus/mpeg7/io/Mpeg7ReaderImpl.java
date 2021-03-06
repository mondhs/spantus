package org.spantus.mpeg7.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.ParserConfigurationException;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.io.AudioReader;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.mpeg7.config.Mpeg7ConfigUtil;
import org.spantus.mpeg7.extractors.AudioDescriptorExtractor;
import org.spantus.mpeg7.extractors.AudioDescriptorVectorExtractor;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Utils.Mpeg7attrs;
import org.spantus.mpeg7.io.Mpeg7Utils.Mpeg7nodes;
import org.spantus.utils.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.audio.mpeg7audio.MP7DocumentBuilder;

public class Mpeg7ReaderImpl implements AudioReader {
	Logger log = Logger.getLogger(getClass());

	
	public AudioFileFormat findAudioFormat(URL url) {
		try {
			return AudioSystem.getAudioFileFormat(url);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	public void readSignal(List<URL> urls, IExtractorInputReader reader){
		readSignal(urls.get(0), reader);
	}

	public void readSignal(URL url, IExtractorInputReader reader)
			throws ProcessingException {
		try {
			readInternal(url, reader);
		} catch (IOException e) {
			throw new ProcessingException(e);
		} catch (ParserConfigurationException e) {
			throw new ProcessingException(e);
		} catch (URISyntaxException e) {
			throw new ProcessingException(e);
		}
	}
	/**
	 * 
	 * @param url
	 * @param reader
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 */
	public void readInternal(URL url, IExtractorInputReader reader)
			throws IOException, ParserConfigurationException, URISyntaxException {
		IExtractorConfig conf = null;
		try {
			if(reader instanceof Mpeg7ExtractorInputReader ){
				conf = ((Mpeg7ExtractorInputReader)reader).getConfig();
			}
			Document mpeg7doc = MP7DocumentBuilder.encode(AudioSystem
					.getAudioInputStream(url), Mpeg7ConfigUtil.getConfig(conf));
			transform(reader, mpeg7doc);
			Mpeg7ConfigUtil.postprocess(reader, conf);
		} catch (UnsupportedAudioFileException e) {
			log.debug("Reading as audio file failed. It will try to read as xml");
			// maybe it is xml file
			try{
				transform(reader, Mpeg7Utils.readDocument(url.toURI()));
			}catch (ProcessingException pe) {
				log.error("It is not possible read file as xml");
				log.error(pe);
				//it is not possible read as xml file
			}
		}
	}
	/**
	 * 
	 * @param reader
	 * @param doc
	 */
	public void transform(IExtractorInputReader reader, Document doc) {
		List<Element> audioDescs = Mpeg7Utils.getAudioDescriptors(doc);
		for (Element descriptor : audioDescs) {
			Mpeg7Utils.register(reader, readSeries(descriptor));
		}
	}
	
	
	/**
	 * 
	 * @param descriptor
	 * @return
	 */
	protected IGeneralExtractor<?> readSeries(Element descriptor) {
		String type = Mpeg7Utils.getAttr(descriptor, Mpeg7attrs.xsi_type);
		Assert.isTrue(!"".equals(type), "Type not found");
		FrameVectorValues vectorVals = new FrameVectorValues();
		FrameValues vals = new FrameValues();
		if ((vectorVals = readSeriesOfScalar(descriptor)) != null) {
			if (vectorVals.size() == 1) {
				AudioDescriptorExtractor extractor = new AudioDescriptorExtractor();
				extractor.setName(type);
				extractor.putValues(0L, new FrameValues(vectorVals.getFirst()));
				extractor.getOutputValues().setSampleRate(vectorVals.getSampleRate());
				extractor.getConfig().setSampleRate(vectorVals.getSampleRate());
				return extractor;
			} else {
				AudioDescriptorVectorExtractor extractor = new AudioDescriptorVectorExtractor();
				extractor.setName(type);
				extractor.putValues(vectorVals);
				extractor.setSampleRate(vectorVals.getSampleRate());
				return extractor;
			}
		} else if ((vectorVals = readSeriesOfVector(descriptor)) != null) {
			AudioDescriptorVectorExtractor extractor = new AudioDescriptorVectorExtractor();
			extractor.setName(type);
			extractor.putValues(vectorVals);
			extractor.setSampleRate(vectorVals.getSampleRate());
			return extractor;
		} else if ((vals = readScalar(descriptor)) != null) {
			AudioDescriptorExtractor extractor = new AudioDescriptorExtractor();
			extractor.getConfig().setSampleRate(vals.getSampleRate());
			extractor.setName(type);
			extractor.putValues(0L, vals);
			extractor.getOutputValues().setSampleRate(vals.getSampleRate());
			return extractor;
		} else {
			StringBuilder bld = new StringBuilder();
			bld.append("Type: ").append(type).append(";").
				append(descriptor.toString());
			
			Mpeg7Utils.traverseDOMBranch(descriptor, bld);			
			throw new ProcessingException("type not implemented: " + bld.toString());
		}
	}
	/**
	 * 
	 * @param descriptor
	 * @return
	 */
	protected FrameValues readScalar(Element descriptor) {
		Element scalar = Mpeg7Utils.getFirstElement(descriptor, Mpeg7nodes.Scalar);
		Double sampleRate = readSampleRate(descriptor);
		if (scalar == null)
			return null;
		Double float1 = Double.valueOf(0);
		try {
			float1 = Double.valueOf(Mpeg7Utils.readScalar(scalar));
		} catch (NumberFormatException nfe) {
			log.error("Number format exception: " + nfe.getMessage());
		}
		FrameValues vals = new FrameValues();
		vals.setSampleRate(sampleRate);
		vals.add(float1);
		return vals;
	}

	/**
	 * 
	 * @param descriptor
	 * @return
	 */
	protected FrameVectorValues readSeriesOfScalar(Element descriptor) {
		Element seriesOfScalar = Mpeg7Utils.getFirstElement(descriptor,
				Mpeg7nodes.SeriesOfScalar);
		if (seriesOfScalar == null)
			return null;
		int totalNumOfSamples = Integer.valueOf(Mpeg7Utils.getAttr(
				seriesOfScalar, Mpeg7attrs.totalNumOfSamples));
		FrameValues vals = null;
		FrameVectorValues fv3 = new FrameVectorValues();
		if ((vals = readRaw(seriesOfScalar, Mpeg7nodes.Raw)) != null) {
			FrameValues valsWeight = readRaw(seriesOfScalar, Mpeg7nodes.Weight);
			
			Assert.isTrue(vals.size() == totalNumOfSamples, "strs.length != totalNumOfSamples: "
					+ vals.size() + "!=" + totalNumOfSamples);
			
			if(valsWeight != null){
//				fv3 = readMinMax(vals, valsWeight);
				fv3.add(valsWeight);
			}else{
				fv3.add(vals);	
			}
		} else if ((vals = readRaw(seriesOfScalar, Mpeg7nodes.Mean)) != null) {
			Assert.isTrue(vals.size() == totalNumOfSamples, "strs.length != totalNumOfSamples: "
					+ vals.size() + "!=" + totalNumOfSamples);
			
			fv3.add(vals);
		} else if ((vals = readRaw(seriesOfScalar, Mpeg7nodes.Min)) != null) {
			
			Assert.isTrue(vals.size() == totalNumOfSamples, "strs.length != totalNumOfSamples: "
					+ vals.size() + "!=" + totalNumOfSamples);

			FrameValues valsMax = readRaw(seriesOfScalar, Mpeg7nodes.Max);
			fv3 = readMinMax(vals, valsMax);
		}
		fv3.setSampleRate(readSampleRate(seriesOfScalar));
		return fv3;
	}
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected FrameVectorValues readSeriesOfVector(Element element) {
		Element seriesOfVector = Mpeg7Utils.getFirstElement(element,
				Mpeg7nodes.SeriesOfVector);
		if (seriesOfVector == null)
			return null;
		// int totalNumOfSamples = Integer.valueOf(Mpeg7Utils.getAttr(
		// seriesOfVector, Mpeg7attrs.totalNumOfSamples));
		int vectorSize = Integer.valueOf(Mpeg7Utils.getAttr(seriesOfVector,
				Mpeg7attrs.vectorSize));
		FrameVectorValues fv3 = null;
		fv3 = readVectors(seriesOfVector, Mpeg7nodes.Raw, vectorSize);
		
		Assert.isTrue(fv3 != null, "samples not read");

		// here there is problematic issue.
		// service impl totalNumOfSamples treats sample as vector, and encoding
		// samples as element.
		// this might be bug in other impl

		// if (fv3.size() != (totalNumOfSamples / vectorSize)) {
		// throw new RuntimeException("wrong sample size: " + fv3.size()
		// + "!=" + (totalNumOfSamples / vectorSize));
		// }
		fv3.setSampleRate(readSampleRate(seriesOfVector));
		return fv3;
	}
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	protected static FrameVectorValues readMinMax(FrameValues min, FrameValues max) {
		FrameVectorValues fv3 = new FrameVectorValues();
		Iterator<Double> maxIter = max.iterator();
		for (Double floatMin : min) {
			FrameValues fv = new FrameValues();
			fv.setSampleRate(min.getSampleRate());
			Double floatMax = maxIter.next();
			fv.add(floatMin);
			fv.add(floatMax);
			fv3.add(fv);
		}
		return fv3;
	}
	/**
	 * 
	 * @param seriesOfValues
	 * @param node
	 * @param vectorSize
	 * @return
	 */
	protected FrameVectorValues readVectors(Element seriesOfValues,
			Mpeg7nodes node, int vectorSize) {
		String[] strs = Mpeg7Utils.readRaw(seriesOfValues, node);
		FrameVectorValues fv3 = new FrameVectorValues();
		Double sampleRate = readSampleRate(seriesOfValues);
		FrameValues fv = new FrameValues();
		fv.setSampleRate(sampleRate);
		int i = 0;
		for (String float1 : strs) {
			if("".equals(float1)) continue;
			fv.add(Double.valueOf(float1));
			i++;
			if (i % vectorSize == 0) {
				fv3.add(fv);
				fv = new FrameValues();
				fv.setSampleRate(sampleRate);
			}
		}
		fv3.setSampleRate(sampleRate);
		return fv3;
	}
	/**
	 * 
	 * @param seriesOfValues
	 * @param node
	 * @return
	 */
	protected FrameValues readRaw(Element seriesOfValues, Mpeg7nodes node) {
		FrameValues fv = transformToFrameValue(Mpeg7Utils.readRaw(seriesOfValues, node));
		if(fv != null){
			fv.setSampleRate(readSampleRate(seriesOfValues));
		}
		return fv;
	}
	/**
	 * 
	 * @param series
	 * @param sampleSize
	 * @return
	 */
	protected Double readSampleRate(Element series) {
		String type = Mpeg7Utils.getAttr((Element)series.getParentNode(), Mpeg7attrs.xsi_type);
		String hopSize = Mpeg7Utils.getAttr(series, Mpeg7attrs.hopSize);
		int mediaDuration = Mpeg7Utils.getMediaDuration(hopSize);
		if("AudioSpectrumBasisType".equals(type) ){
			mediaDuration *= 5;
		}
		return 1000D/mediaDuration;
	}
	/**
	 * 
	 * @param strs
	 * @return
	 */
	protected FrameValues transformToFrameValue(String[] strs) {
		if (strs == null)
			return null;
		FrameValues vals = new FrameValues();
		for (int i = 0; i < strs.length; i++) {
			if("".equals(strs[i])){
				continue;
			}
			vals.add(Double.valueOf(strs[i]));
		}
		return vals;
	}



	public SignalFormat getFormat(URL url) {
		return null;
	}


	public boolean isFormatSupported(URL url) {
		return false;
	}

	public void readSignalSmoothed(URL url,
			IExtractorInputReader extractorReader) {
		throw new IllegalArgumentException("Not impl");
	}

}
