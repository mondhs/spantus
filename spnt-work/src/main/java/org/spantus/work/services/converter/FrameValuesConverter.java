package org.spantus.work.services.converter;

import org.spantus.core.FrameValues;
import org.spantus.utils.Assert;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FrameValuesConverter implements Converter{

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return FrameValues.class == type;
	}

	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		FrameValues vals = (FrameValues)source;
		writer.startNode("SeriesOfScalar");
		writer.addAttribute("totalNumOfSamples", "" + vals.size());
		writer.addAttribute("sampleRate", "" + vals.getSampleRate());
		writer.startNode("Raw");
		StringBuffer sb = new StringBuffer();
		int size = 15;
		int i = size;
		for (Double float1 : vals) {
			sb.append(float1);
			String separator = " ";
			if(i<=0){
				separator = " \n";
				i = size;
			}
			sb.append(separator);
			i--;
		}
		writer.setValue(sb.toString());	
		writer.endNode();
		writer.endNode();
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		reader.moveDown();
		String totalNumOfSamples = reader.getAttribute("totalNumOfSamples");
		String sampleRate = reader.getAttribute("sampleRate");
		reader.moveDown();
		String values = reader.getValue();
		String[] strs = values.split("[\\s]+");
		FrameValues frameValues = new FrameValues();
		frameValues.setSampleRate(Double.valueOf(sampleRate));
		for (String float1Str : strs) {
			if("".equals(float1Str)) continue;
			frameValues.add(Double.valueOf(float1Str));
		}
		Assert.isTrue(frameValues.size() == Integer.valueOf(totalNumOfSamples));
		reader.moveUp();
		reader.moveUp();

		return frameValues;
	}

	

}
