package org.spantus.work.services.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

import java.io.IOException;
import java.util.stream.Collectors;

public class FrameVectorValuesSerializer extends JsonSerializer<FrameVectorValues> {
    @Override
    public void serialize(FrameVectorValues processor, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        FrameVectorValues vals = (FrameVectorValues)processor;
        String valsStr = vals.stream()
                .map(x -> String.join(",",x.stream().map(d->d.toString()).collect(Collectors.toList())))
                .collect(Collectors.joining("; "));
        jsonGenerator.writeStringField("vals", valsStr);
        jsonGenerator.writeNumberField("sampleRate", vals.getSampleRate());
        jsonGenerator.writeEndObject();

    }
}
