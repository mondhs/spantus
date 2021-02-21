package org.spantus.work.services.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spantus.core.FrameValues;
import org.spantus.core.extractor.windowing.WindowBufferProcessor;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FrameValuesSerializer extends JsonSerializer<FrameValues> {
    @Override
    public void serialize(FrameValues processor, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        FrameValues vals = (FrameValues)processor;
        Double sampleRate = vals.getSampleRate();
        String valsStr = vals.stream().map(x -> x.toString()).collect(Collectors.joining("; "));
        jsonGenerator.writeStringField("vals", valsStr);
        jsonGenerator.writeNumberField("sampleRate", sampleRate);
        jsonGenerator.writeEndObject();

    }

}
