package org.spantus.exp.synthesis;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import scikit.util.Pair;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author as
 */
public class EspeakGeneratorLt extends AbstractSpeechGenerator {

//    File tmpDir = Files.createTempDir();
    String voice = "/home/as/bin/mbrola/lt1/lt1";
    private static final Logger LOG = Logger.getLogger(EspeakGeneratorLt.class);
    Splitter splitter = Splitter.on(Pattern.compile("\\s+")).trimResults();

    @Override
    public AudioInputStream generate(Transcribtion transcribtion) {
        List<MarkerMbrola> phonemes = transcribtion.getMarkerBrolas();

        String command = MessageFormat.format(
                "mbrola -e {0} - -", voice);

        try {
            //write stdin
            Process process = Runtime.getRuntime().exec(command);
            try (OutputStream stdin = process.getOutputStream()) {
                for (MarkerMbrola markerSynthesis : phonemes) {
                    stdin.write(markerSynthesis.toMbrolaString()
                            .getBytes(Charset.forName("UTF-8")));
                }
            }
            AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);

            byte[] audioBytes = ByteStreams.toByteArray(process.getInputStream());
            InputStream is = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioInputStream =
                    new AudioInputStream(is, format, audioBytes.length);
            process.waitFor();
            printError(process);
            return audioInputStream;
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public Transcribtion translate(String sentence, float lengthCoef) {
        Transcribtion transcribtion = new Transcribtion();

        long id = System.currentTimeMillis();

        try {
            String command = MessageFormat.format(
                    "espeak -q -v mb-lt1 --pho --stdin -g {0,number,####}", 20);
            System.out.println(command);

            //write stdin
            Process process = Runtime.getRuntime().exec(command);
            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(sentence.getBytes(Charset.forName("UTF-8")));
            }


            process.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            parseAndAppend(transcribtion, "< 0");
            String s;
            MarkerSet words = transcribtion.getHolder().getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.word.name());
            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                if (s.length() == 0) {
                    continue;
                }
                parseAndAppend(transcribtion, s);
                if (words.getMarkers().size() > 0) {
                    Marker lastWord = words.getMarkers().get(words.getMarkers().size() - 1);
                    if("-l'-ie-t".equals(lastWord.getLabel())){
                        parseAndAppend(transcribtion, "| 0");
                    }

                }
            }
            parseAndAppend(transcribtion, "> 0");

        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
        return transcribtion;
    }

    /**
     *
     * @param transcribtion
     * @param phone
     */
    private void parseAndAppend(Transcribtion transcribtion, String phone) {
        MarkerSet words = transcribtion.getHolder().getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.word.name());
        MarkerSet phonemes = transcribtion.getHolder().getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.phone.name());
        Marker lastWord = null;
        if (words.getMarkers().size() > 0) {
            lastWord = words.getMarkers().get(words.getMarkers().size() - 1);
        }
        Iterator<String> splitedIter = splitter.split(phone).iterator();
        String phoneLabel = splitedIter.next();
        String lengthStr = splitedIter.next();
        Long phoneLength = Long.valueOf(lengthStr);
        MarkerMbrola phoneMarker = newMarkerSynthesis(transcribtion.getFinished(),
                phoneLength, phoneLabel, splitedIter);
        phoneLength = phoneMarker.getMarker().getLength();
        Long currentEnd = phoneMarker.getMarker().getEnd();
        if ("_".equals(phoneLabel)) {
            if (lastWord != null) {
                lastWord.setEnd(phoneMarker.getMarker().getStart());
            }
            lastWord = new Marker();
            lastWord.setLabel("");
            lastWord.setStart(currentEnd);
            words.getMarkers().add(lastWord);

        } else {
            if (lastWord != null) {
                lastWord.setEnd(currentEnd);
                lastWord.setLabel(lastWord.getLabel() + "-" + phoneLabel);
            }
        }

        phonemes.getMarkers().add(phoneMarker.getMarker());
        transcribtion.getMarkerBrolas().add(phoneMarker);
        transcribtion.getTransctiption().append(phone);
        transcribtion.incFinished(phoneLength);
    }

    /**
     *
     * @param start
     * @param length
     * @param phoneLabel
     * @param splitedIter
     * @return
     */
    private MarkerMbrola newMarkerSynthesis(long start, Long length, String phoneLabel, Iterator<String> splitedIter) {
        Marker marker = new Marker();
        marker.setStart(start);
        marker.setLength(length);
        marker.setLabel(phoneLabel);
        MarkerMbrola phoneMarker = new MarkerMbrola(marker);
        for (Iterator<String> it = splitedIter; it.hasNext();) {
            String pitchDurationStr = it.next();
            String pitchValueStr = it.next();
            Integer pitchDuration = Integer.valueOf(pitchDurationStr);
            Integer pitchValue = Integer.valueOf(pitchValueStr);
            phoneMarker.getPitches().add(new Pair<>(pitchDuration, pitchValue));
        }
        processMarker(phoneMarker);
        return phoneMarker;
    }

    private void processMarker(MarkerMbrola phoneMarker) {
        Marker marker = phoneMarker.getMarker();
        switch (marker.getLabel()) {
            case "_":
                marker.setLength(60L);
                break;
            case "|":
                marker.setLabel("_");
                marker.setLength(60L);
                break;
            case "<":
            case ">":
                marker.setLabel("_");
                marker.setLength(300L);
                break;
            default:
                float lenghtCoef =
                        1;
//                        rand();
                long newLength = (long) (marker.getLength() * lenghtCoef);
                marker.setLength(newLength);
        }
    }

    public Map<String, String> readSentencesToMap() {
        try {
            final Map<String, String> sentences = Maps.newLinkedHashMap();
            Files.readLines(new File("/home/as/tmp/lietuvos_sakiniai.csv"), Charset.defaultCharset(),
                    new LineProcessor<String>() {
                        @Override
                        public boolean processLine(String line) throws IOException {
                            Iterable<String> splited = Splitter.on(";").trimResults().split(line);
                            sentences.put(Iterables.getFirst(splited, ""),
                                    Iterables.getLast(splited, ""));
                            return true;
                        }

                        @Override
                        public String getResult() {
                            return null;
                        }
                    });
            return sentences;
        } catch (IOException ex) {
           throw new IllegalArgumentException(ex);
        }
    }

    public static void main(String[] args) {

        EspeakGeneratorLt speechGenerator = new EspeakGeneratorLt();
        Map<String, String> sentences = speechGenerator.readSentencesToMap();
        for (Map.Entry<String, String> entry : sentences.entrySet()) {
           speechGenerator.bulkGeneration(entry.getKey(),entry.getValue(), "/tmp/test", 30, 1);
        }
        speechGenerator.bulkGeneration("lietuvos_test","laisvos Lietuvos kariai 40 aš tu jis ji kad kai", "/tmp/test", 30, 1);

    }
}
