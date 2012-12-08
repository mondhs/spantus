package org.spantus.exp.synthesis;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;

import com.google.common.io.ByteStreams;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mondhs
 */
public class EspeakMbrollaGeneratorLt extends AbstractSpeechGenerator {

//    File tmpDir = Files.createTempDir();
    String voice = "/home/as/bin/mbrola/lt1/lt1";
    private static final Logger LOG = Logger.getLogger(EspeakMbrollaGeneratorLt.class);


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
                	String mbrollaStr = null;
                	//Without pitch
                	mbrollaStr = markerSynthesis.toMbrolaString();
                	//with pitch
//                	mbrollaStr = markerSynthesis.toMbrolaStringWithPitch();
                	stdin.write(
                			mbrollaStr.getBytes(Charset.forName("UTF-8")));
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
        long initSilence = 62L; ////by esepeak: 1000samples/sampleRate16K=0.0625
        transcribtion.setPreviousPhoneLength(initSilence);

        try {
            String command = MessageFormat.format(
                    "espeak -q -v mb-lt1 --pho --stdin -g {0,number,####}", 20);
            LOG.error(command);

            //write stdin
            Process process = Runtime.getRuntime().exec(command);
            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(sentence.getBytes(Charset.forName("UTF-8")));
            }


            process.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
//            parseAndAppend(transcribtion, "< 0");
            String s;
            MarkerSet words = transcribtion.getHolder().getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.word.name());
            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                if (s.length() == 0) {
                    continue;
                }
                MarkerMbrola phoneMarker = parseAndAppend(transcribtion, s, lengthCoef);
                phoneMarker.getMarker();
//                if (words.getMarkers().size() > 0) {
//                    Marker lastWord = words.getMarkers().get(words.getMarkers().size() - 1);
//                    if("-l'-ie-t".equals(lastWord.getLabel())){
//                        parseAndAppend(transcribtion, "| 0");
//                    }
//                }
            }
//            parseAndAppend(transcribtion, "> 0");
            printError(process);

        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
        return transcribtion;
    }




    protected void processMarker(MarkerMbrola phoneMarker, float lengthCoef) {
//        Marker marker = phoneMarker.getMarker();
//        switch (marker.getLabel()) {
//            case "_":
//                marker.setLength(100L);
//                break;
//            case "|":
//                marker.setLabel("_");
//                marker.setLength(100L);
//                break;
//            case "<":
//            case ">":
//                marker.setLabel("_");
//                marker.setLength(300L);
//                break;
//            default:
                float lenghtCoef =
//                        1F;
                        rand();
//                		lengthCoef;
                Marker marker = phoneMarker.getMarker();
                long newLength = (long) (marker.getLength() * lenghtCoef);
                marker.setLength(newLength);
//        }
    }

    

    public static void main(String[] args) {

        EspeakMbrollaGeneratorLt speechGenerator = new EspeakMbrollaGeneratorLt();
        Map<String, String> sentences = speechGenerator.readSentencesToMap("/home/as/tmp/lietuvos_sakiniai.csv");
        int[] snrArr = new int[]{30};
        for (Map.Entry<String, String> entry : sentences.entrySet()) {
            for (int snr : snrArr) {
                speechGenerator.bulkGeneration(entry.getKey(),entry.getValue(), "/tmp/test", snr, 1);
            }
           
//           break;
        }
        speechGenerator.bulkGeneration("lietuvos_mbr_test","trijų Baltijos valstybių vardu pranešimą skaitys Lietuvos atstovas", "/tmp/test", 30, 1);

    }
}
