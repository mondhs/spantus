package org.spantus.exp.synthesis;

import com.google.common.base.Splitter;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.services.impl.MarkerProxyDao;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mondhs
 */
public class EspeakGeneratorLogLt extends AbstractSpeechGenerator {

//    File tmpDir = Files.createTempDir();
    String voice = "/home/as/bin/mbrola/lt1/lt1";
    private static final Logger LOG = Logger.getLogger(EspeakGeneratorLogLt.class);

    @Override
    public AudioInputStream generate(Transcribtion transcribtion) {

        String command = MessageFormat.format(
                "/home/as/src/espeak-sample/bulkGenerator wav", "");

        try {
            //write stdin
            Process process = Runtime.getRuntime().exec(command);
            try (OutputStream stdin = process.getOutputStream()) {

                stdin.write(transcribtion.getOriginalText().toString()
                        .getBytes(Charset.forName("UTF-8")));

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
//        sentence = "[[_]]"+sentence;
        LOG.error(sentence);
        transcribtion.setOriginalText(sentence);
        try {
            String command = MessageFormat.format(
                    "/home/as/src/espeak-sample/bulkGenerator txt", "");
            LOG.error(command);
            //write stdin
            Process process = Runtime.getRuntime().exec(command);
            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(sentence.getBytes(Charset.forName("UTF-8")));
            }
            process.waitFor();
            MarkerDao aucacityMarkerDao = WorkServiceFactory.resolveMarkerDao(MarkerProxyDao.AUDACITY_TXT);
            MarkerSetHolder retrievedInfo = aucacityMarkerDao.read(process.getInputStream());
            MarkerSet phonesSet = retrievedInfo.getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.phone.name());
            for (Marker entry : phonesSet.getMarkers()) {
                parseAndAppend(transcribtion, MessageFormat.format("{0} {1,number,####}", 
                       entry.getLabel(), entry.getLength() ));
            }

        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
        return transcribtion;
    }
    
    @Override
    protected void processMarker(MarkerMbrola phoneMarker) {
        //Do nothing
    }

    public static void main(String[] args) {

        EspeakGeneratorLogLt speechGenerator = new EspeakGeneratorLogLt();
        Map<String, String> sentences = speechGenerator.readSentencesToMap("/home/as/tmp/lietuvos_sakiniai.csv");
        int[] snrArr = new int[]{30};
//        for (Map.Entry<String, String> entry : sentences.entrySet()) {
//            for (int snr : snrArr) {
//                speechGenerator.bulkGeneration(entry.getKey(),entry.getValue(), "/tmp/test", snr, 1);
//            }
//           
////           break;
//        }
        speechGenerator.bulkGeneration("lietuvos_test", "trijų Baltijos valstybių vardu pranešimą skaitys Lietuvos atstovas as ", "/tmp/test", 30, 1);

    }
}
