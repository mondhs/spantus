package org.spantus.work.services.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.spantus.core.FrameValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.ExtractorConfigUtil;
import org.spantus.extractor.impl.EnergyExtractor;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IExtractorDeserializer extends JsonDeserializer<IExtractor> {

    @Override
    public IExtractor deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//        String encodingStr = jsonParser.getValueAsString();
////        AudioFormat.Encoding encoding = new AudioFormat.Encoding(encodingStr);
//        FrameValues fv = new FrameValues();
//        List<Double> result = Arrays.stream(encodingStr.split(";")).map(x -> Double.valueOf(x)).collect(Collectors.toList());
//        fv.addAll(result);
        TreeNode tree = jsonParser.readValueAsTree();
//        EnergyExtractor initalExtractor = jsonParser.readValueAs(EnergyExtractor.class);
        String name = ((TextNode)tree.get("name")).asText() ;
        ExtractorEnum extractorEnum = Arrays.stream(ExtractorEnum.values()).filter(x -> x.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to resolve : " + name));
        IGeneralExtractor<?> extractor2 = ExtractorUtils.createInstance(extractorEnum, null);
        String vals = ((TextNode) tree.get("outputValues").get("vals")).asText();
        double sampleRate = ((DoubleNode) tree.get("outputValues").get("sampleRate")).asDouble();

        FrameValues fv = new FrameValues();
        List<Double> result = Arrays.stream(vals.split(";")).map(x -> Double.valueOf(x)).collect(Collectors.toList());
        fv.addAll(result);
        fv.setSampleRate(sampleRate);
        ((AbstractExtractor)extractor2).setOutputValues(fv);
        return (IExtractor) extractor2;
    }
}
