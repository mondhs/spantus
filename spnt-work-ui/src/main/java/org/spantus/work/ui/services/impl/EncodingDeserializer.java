package org.spantus.work.ui.services.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

public class EncodingDeserializer extends JsonDeserializer<AudioFormat.Encoding> {
    @Override
    public AudioFormat.Encoding deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String encodingStr = jsonParser.getValueAsString();
        AudioFormat.Encoding encoding = new AudioFormat.Encoding(encodingStr);
        return encoding;
    }
}
