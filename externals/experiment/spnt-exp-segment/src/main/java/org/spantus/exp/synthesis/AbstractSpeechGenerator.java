/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.logger.Logger;
import org.spantus.work.services.WorkServiceFactory;
import scikit.util.Pair;

/**
 *
 * @author as
 */
public abstract class AbstractSpeechGenerator {

    MarkerDao markerDao;
    private static final Logger LOG = Logger.getLogger(AbstractSpeechGenerator.class);
    Splitter splitter = Splitter.on(Pattern.compile("\\s+")).trimResults();
    
    protected abstract AudioInputStream generate(Transcribtion trascribtion);
    protected abstract Transcribtion translate(String sentence, float lengthCoef);
    protected abstract void processMarker(MarkerMbrola phoneMarker);
    

    /**
     *
     * @param ais
     * @param string
     */
    public String persist(AudioInputStream ais, String fileName) {
        String st = AudioManagerFactory.createAudioManager()
                .save(ais, fileName);
        return st;
    }

    public Transcribtion persist(Transcribtion transcribtion, String fileName) {
        getMarkerDao().write(transcribtion.getHolder(), new File(fileName));
        return transcribtion;
    }

    /**
     * Noising
     *
     * @param ais
     * @return
     */
    public void addNoize(String filename, Integer snr) {
//		DoubleDataSource audio = new AudioDoubleDataSource(ais);
//		float samplingRate = ais.getFormat().getSampleRate();
//		double cutOff = 100 / samplingRate;
//		double transition = 100 / samplingRate;
//		marytts.signalproc.filter.HighPassFilter hFilter = new marytts.signalproc.filter.HighPassFilter(
//				cutOff, transition);
//		DoubleDataSource filtered = hFilter.apply(audio);
//		return new DDSAudioInputStream(filtered, ais.getFormat());


        String command = MessageFormat.format(
                //				"octave --path /home/as/src/ocatave/ --silent --eval 'noisefication(\"{0}\",{1});'"
                //				"octave --silent --eval ''[y,Fs,bits]=wavread(\"{0}\",);y=awgn(y'''',{1},\"measured\");wavwrite(\"{0}1\",y'''',Fs);''"
                "octave --path /home/as/src/ocatave/ --silent --eval [y,Fs,bits]=wavread(''{0}'');y=awgn(y'''',{1},''measured'');wavwrite(\"{0}\",y'''',Fs);", filename, snr);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));

            String s = null;
            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out
                    .println(command);
            // read any errors from the attempted command
            System.out
                    .println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (InterruptedException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public void bulkGeneration(String text, String path, Integer snr, int count) {
        bulkGeneration("sin_zodis", text, path, snr, count);
    }
    
    public void bulkGeneration(String prefix, String text, String path, Integer snr, int count) {
        for (int i = 1; i <= count; i++) {
            Transcribtion trascribtion = translate(text.toLowerCase(), rand());
            AudioInputStream ais = generate(trascribtion);
            String fileName = MessageFormat.format("{0}/{1}-{2}_{3}", path, prefix, snr, i);
            File wavFile = new File(fileName + ".wav");
            persist(ais, wavFile.getAbsolutePath());
            persist(trascribtion, fileName + ".mspnt.xml");
            if(snr != null){
                addNoize(wavFile.getAbsolutePath(), snr);
            }
        }
    }
    /**
     * read any errors from the attempted command
     * @param process
     * @throws IOException 
     */
    protected void printError(Process process) throws IOException {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(
                process.getErrorStream()));
        // read any errors from the attempted command
        String s;
        LOG.debug("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            LOG.debug(s);
        }
        LOG.debug("printError end:\n");
    }
    
    public float rand() {
        float speed = 1.5f;
        float rand =
                (float) (.95f * (Math.random() - .5f));
//				0f;
        return speed - rand;
    }
    
    protected Map<String, String> readSentencesToMap(String fileName) {
        try {
            final Map<String, String> sentences = Maps.newLinkedHashMap();
            Files.readLines(new File(fileName), Charset.defaultCharset(),
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
    
        /**
     *
     * @param transcribtion
     * @param phone
     */
    protected void parseAndAppend(Transcribtion transcribtion, String phone) {
        MarkerSet words = transcribtion.getHolder().getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.word.name());
        MarkerSet phonemes = transcribtion.getHolder().getMarkerSets().get(MarkerSetHolder.MarkerSetHolderEnum.phone.name());
        Marker lastWord = null;
        if (words.getMarkers().size() > 0) {
            lastWord = words.getMarkers().get(words.getMarkers().size() - 1);
        }
        Iterator<String> splitedIter = splitter.split(phone).iterator();
        String phoneLabel = splitedIter.next();
        String lengthStr = splitedIter.next();
        long previousPhoneLength = transcribtion.getPreviousPhoneLength();
        Long nextPhoneLength = Long.valueOf(lengthStr);
        MarkerMbrola phoneMarker = newMarkerSynthesis(transcribtion.getFinished(),
                previousPhoneLength, phoneLabel, splitedIter);
        previousPhoneLength = phoneMarker.getMarker().getLength();
        Long currentEnd = phoneMarker.getMarker().getEnd();
        if ("_".equals(phoneLabel)) {
            if (lastWord != null) {
                lastWord.setEnd(phoneMarker.getMarker().getStart());
            }
            lastWord = new Marker();
            lastWord.setLabel("");
            lastWord.setStart(currentEnd);
            lastWord.setLength(1L);
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
        transcribtion.incFinished(previousPhoneLength);
        transcribtion.setPreviousPhoneLength(nextPhoneLength);
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

    public MarkerDao getMarkerDao() {
        if (markerDao == null) {
            markerDao = WorkServiceFactory.createMarkerDao();
        }
        return markerDao;
    }

    public void setMarkerDao(MarkerDao markerDao) {
        this.markerDao = markerDao;
    }

   
}
