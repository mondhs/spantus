package org.spantus.work.services.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.extractor.AbstractExtractor;
import org.spantus.extractor.AbstractExtractorVector;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IExtractorVectorDeserializer extends JsonDeserializer<IExtractorVector> {

    @Override
    public IExtractorVector deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//        String encodingStr = jsonParser.getValueAsString();
////        AudioFormat.Encoding encoding = new AudioFormat.Encoding(encodingStr);
//        FrameValues fv = new FrameValues();
//        List<Double> result = Arrays.stream(encodingStr.split(";")).map(x -> Double.valueOf(x)).collect(Collectors.toList());
//        fv.addAll(result);
        TreeNode tree = jsonParser.readValueAsTree();
//        EnergyExtractor initalExtractor = jsonParser.readValueAs(EnergyExtractor.class);
        String name = ((TextNode)tree.get("name")).asText() ;
        String vals = ((TextNode) tree.get("outputValues").get("vals")).asText();
        double sampleRate = ((DoubleNode) tree.get("outputValues").get("sampleRate")).asDouble();
        ExtractorEnum extractorEnum = Arrays.stream(ExtractorEnum.values()).filter(x -> x.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unable to resolve : " + name));

        IGeneralExtractor<?> extractor2 = ExtractorUtils.createInstance(extractorEnum, null);

        FrameVectorValues fv = new FrameVectorValues();
        Stream<List<Double>> resultLF = Arrays.stream(vals.split(";"))
                .map(x -> Arrays.stream(x.split(",")).map(Double::valueOf).collect(Collectors.toList())
                );//.collect(Collectors.toList());
        List<FrameValues> result = resultLF.map(x -> new FrameValues(x, sampleRate)).collect(Collectors.toList());
        fv.addAll(result);
        fv.setSampleRate(sampleRate);
        ((AbstractExtractorVector)extractor2).setOutputValues(fv);

        return (IExtractorVector) extractor2;
    }
}
