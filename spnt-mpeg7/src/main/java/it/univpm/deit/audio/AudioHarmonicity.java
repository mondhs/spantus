/*
  Copyright (c) 2003, Francesco Saletti
  
  This file is part of the MPEG7AudioEnc project.
*/
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

import it.univpm.deit.FFT2N;

import java.util.*;

/**
 * User: Francesco
 * Date: Nov 17, 2003
 * Time: 4:11:53 PM
 *
 * @author Francesco Saletti
 */
@SuppressWarnings("unchecked")
public class AudioHarmonicity
    extends MsgSpeaker
    implements MsgListener
{
    private final int LENGTH_WINDOW = 30;
    private LinkedList<MsgAudioFundamentalFrequency> afflist = new LinkedList<MsgAudioFundamentalFrequency>();
    
	private Map        hamming = new TreeMap();
    private Map        fft2n   = new TreeMap();
    private float[] CombSpectrum;
    private float[] SignalSpectrum;
    private int SpectrumLength;
    private float dF;

    public void receivedMsg(Msg msg) {
        if (msg instanceof MsgAudioFundamentalFrequency) {
            MsgAudioFundamentalFrequency maff = (MsgAudioFundamentalFrequency) msg;
            afflist.addLast(maff);
            if (!(LENGTH_WINDOW % maff.duration == 0 ))
                throw new AssertionError ();

            // get the combed-signal spectrum
            if( afflist.size()*maff.duration == LENGTH_WINDOW ) {
                CombSpectrum = getCombSpectrum();
            }
       }

        if (msg instanceof MsgAudioSpectrum) {
            MsgAudioSpectrum mas = (MsgAudioSpectrum) msg;
            SignalSpectrum = mas.getAudioSpectrum();
            SpectrumLength = mas.getAudioSpectrumLength();
            dF = mas.deltaF;
        }

        if (afflist.size()*msg.duration  == LENGTH_WINDOW) {

            //find the spectrum index beyond which it cannot be considered harmonic anymore
            int uplimit = getUpperLimit(SignalSpectrum, CombSpectrum);

            // calculate the corrisponding frequency and converts it in a octave-based scale
            float uplimitfreq = upperFrequency(uplimit);

            //System.out.println(uplimitfreq);
            MsgAudioFundamentalFrequency msgaff = (MsgAudioFundamentalFrequency) afflist.getLast();
            send(new MsgAudioHarmonicity(msgaff.time, msgaff.duration, msgaff.confidence, uplimitfreq));

            //remove the first element of the list
            afflist.removeFirst();
        }
    }
    private float[] getHamming( int length ) {
      Integer key = new Integer(length);
      float[] window = (float[]) hamming.get(key);
      if (window == null) {
        window = new float[length];
        for (int n = 0; n < window.length; ++n)
          window[n] =(float)(
              0.54 - 0.46 * Math.cos(n * 2.0 * Math.PI / (window.length - 1)));
        hamming.put(key, window);
      }
      return window;
    }

    private FFT2N getFFT2N( int length_fft ) {
      Integer key = new Integer( length_fft );
      FFT2N fft = (FFT2N) fft2n.get( key );
      if (fft == null) {
        fft = new FFT2N(length_fft);
        fft2n.put(key, fft);
      }
      return fft;
    }

    private float[] getCombSpectrum() {
        // calculate length of signal
        int length = 0;
        Iterator i = afflist.iterator();
        while (i.hasNext())
            length += ((MsgAudioFundamentalFrequency) (i.next())).getCombSignalLength();

        float[] s = new float[length];

        // merge msgs to signal
        i = afflist.iterator();
        int index = 0;
        while (i.hasNext()) {
            float[] source = ((MsgAudioFundamentalFrequency) i.next()).getCombSignal();
            System.arraycopy(source, 0, s, index, source.length);
            index += source.length;
        }

        // get or calculate hamming window
        float[] window = getHamming(s.length);

        // scale signal with hamming window
        for (int n = 0; n < window.length; ++n)
            s[n] *= window[n];

        // calculate length of fft
        int length_fft = (int) Math.ceil(Function.log2(s.length));
        length_fft = (int) Math.pow(2.0, length_fft);

        FFT2N fft = getFFT2N(length_fft);
        float[] signal;

        if (length_fft == s.length) {
            signal = s;
        } else {
            signal = new float[length_fft];
            System.arraycopy(s, 0, signal, 0, s.length);
            Arrays.fill(signal, s.length, signal.length, 0.0f);
        }

        fft.fft(signal);
        float[] ps = FFT2N.PowerSpectrum(signal);
        return ps;
    }

    private int getUpperLimit(float[] sigspec, float[] combspec) {

        // find the upper spectrum index for which the spectral power ratio between the
        // signal and the combed signal is less than 0.5

        for (int lolimit = SpectrumLength - 1; lolimit >= 0; lolimit--) {
            float SignalPower = 0;
            float CombedSignalPower = 0;
            for (int i = SpectrumLength - 1; i >= lolimit; i--) {
                SignalPower += sigspec[i];
                CombedSignalPower += combspec[i];
            }
            if (CombedSignalPower/SignalPower < 0.5)
                return lolimit;
        }
        return 0;

    }

    private float upperFrequency (int index) {
        float[] octaves = {31.25f, 62.5f, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
        float freq = index * dF;
        // convert the frequency in an octave-based scale centered upon 1KHz

        if (freq < octaves[0])
            return octaves[0];
        else if (freq > octaves[9])
            return octaves[9];
        else {
            int i = 1;
            while (freq > octaves[i]) {
                i++;
            }
            if (freq < (octaves[i] + octaves[i-1])/2)
                return octaves[i-1];
            else
                return octaves[i];
        }


    }
}
