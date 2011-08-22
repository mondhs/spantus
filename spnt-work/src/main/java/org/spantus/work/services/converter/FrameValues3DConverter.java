package org.spantus.work.services.converter;

import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.utils.Assert;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FrameValues3DConverter implements Converter{

	
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return FrameVectorValues.class == type;
	}

	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		FrameVectorValues vals = (FrameVectorValues)source;
		writer.startNode("SeriesOfVector");
		writer.addAttribute("totalNumOfSamples", "" + vals.size());
		writer.addAttribute("sampleRate", "" + vals.getSampleRate());
		writer.addAttribute("vectorSize", "" + vals.iterator().next().size());
		writer.startNode("Raw");
		StringBuffer sb = new StringBuffer();
		int size = 15;
		int i = size;
		
		for (List<Double> fv : vals) {
			for (Double float2 : fv) {
				sb.append(float2);
				String separator = " ";
				if(i<=0){
					separator = " \n";
					i = size;
				}
				sb.append(separator);
				i--;
			}
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
		Integer vectorSize = Integer.valueOf(reader.getAttribute("vectorSize"));
		reader.moveDown();
		String values = reader.getValue();
		String[] strs = values.split("[\\s]+");
		FrameVectorValues fv3d = new FrameVectorValues();
		fv3d.setSampleRate(Double.valueOf(sampleRate));
		int i = 0;
		FrameValues fv = new FrameValues();
		
		for (String float1Str : strs) {
			if("".equals(float1Str)) continue;
			fv.add(Double.valueOf(float1Str));
			i++;
			if(i>=vectorSize){
				fv3d.add(fv);
				fv = new FrameValues();
				i=0;
			}
		}
		Assert.isTrue(fv3d.size() == Integer.valueOf(totalNumOfSamples));
		reader.moveUp();
		reader.moveUp();

		return fv3d;
	}

	

}
