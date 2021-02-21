package org.spantus.exp.exec;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioReader;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.ISegmentatorService;
import org.spantus.segment.offline.BasicSegmentatorServiceImpl;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SyllableCounter {


    public IExtractorInputReader readSignal()
            throws UnsupportedAudioFileException, IOException {
        File wavFile = new File("../data/t_1_2.wav");
        URL urlFile = wavFile.toURI().toURL();
        AudioReader reader = AudioReaderFactory.createAudioReader();
        IExtractorInputReader bufferedReader = ExtractorsFactory
                .createReader(reader.findAudioFormat(urlFile));
        ExtractorUtils.registerThreshold(bufferedReader, new ExtractorEnum[]{
                ExtractorEnum.ENERGY_EXTRACTOR,
//				ExtractorEnum.CROSSING_ZERO_EXTRACTOR,
//                ExtractorEnum.ENVELOPE_EXTRACTOR,
//                ExtractorEnum.LOG_ATTACK_TIME,
                ExtractorEnum.LOUDNESS_EXTRACTOR,
//                ExtractorEnum.SPECTRAL_ENTROPY_EXTRACTOR,
//                ExtractorEnum.AUTOCORRELATION_EXTRACTOR,
                ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR

        }, null);
        reader.readSignal(urlFile, bufferedReader);
        return bufferedReader;
    }

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        SyllableCounter syllableCounter = new SyllableCounter();
        IExtractorInputReader reader = syllableCounter.readSignal();
        Set<IExtractor> extractors = reader.getExtractorRegister();
        List<IClassifier> thresholds = extractors
                .stream().filter(x -> x instanceof IClassifier)
                .map(x -> (IClassifier) x)
                .collect(Collectors.toList());
        ISegmentatorService segmentator = new BasicSegmentatorServiceImpl();
        MarkerSetHolder segments = segmentator.extractSegments(thresholds);
        List<Marker> markers = segments.getMarkerSets().get("phone").getMarkers();


    }

}
