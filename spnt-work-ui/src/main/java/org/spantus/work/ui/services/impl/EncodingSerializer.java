package org.spantus.work.ui.services.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class EncodingSerializer extends StdSerializer<AudioFormat.Encoding> {
    public EncodingSerializer() {
        this(null);
    }

    public EncodingSerializer(Class<AudioFormat.Encoding> t) {
        super(t);
    }

    @Override
    public void serialize(AudioFormat.Encoding audioFormat, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String value = audioFormat.toString();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("audioFormat", value);
        jsonGenerator.writeEndObject();
    }
}
