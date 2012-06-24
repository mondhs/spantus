/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.services.impl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import javax.sound.sampled.AudioFormat;

/**
 *
 * @author as
 */
public class EncodingConverter implements Converter {

	@SuppressWarnings("rawtypes") 
    @Override
    public boolean canConvert(Class type) {
        return AudioFormat.Encoding.class == type;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
      AudioFormat.Encoding encoding = (AudioFormat.Encoding)source;
      writer.setValue(encoding.toString());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
       String encodingStr = reader.getValue();
        AudioFormat.Encoding encoding = new AudioFormat.Encoding(encodingStr);
        return encoding;
    }
}
