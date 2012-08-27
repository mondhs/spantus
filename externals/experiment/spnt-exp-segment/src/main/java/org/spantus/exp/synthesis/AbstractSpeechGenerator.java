/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import javax.sound.sampled.AudioInputStream;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.logger.Logger;
import org.spantus.work.services.WorkServiceFactory;

/**
 *
 * @author as
 */
public abstract class AbstractSpeechGenerator {

    MarkerDao markerDao;
    private static final Logger LOG = Logger.getLogger(AbstractSpeechGenerator.class);

    public abstract AudioInputStream generate(Transcribtion trascribtion);
    public abstract Transcribtion translate(String sentence, float lengthCoef);

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

    }
    
    public float rand() {
        float speed = 1.5f;
        float rand =
                (float) (.95f * (Math.random() - .5f));
//				0f;
        return speed - rand;
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
