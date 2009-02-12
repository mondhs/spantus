/*
 * Copyright (c) 2004, Michele Bartolucci
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;

import java.util.*;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
@SuppressWarnings("unchecked")
public class HarmonicPeaks 
	extends MsgSpeaker
	implements MsgListener
{

	private float nonHarmonicity, thresold;
	private ArrayList peaks =new ArrayList();
	private float[] signalSpectrum;
	private int spectrumLength;
	private float dF;

	
	
	public HarmonicPeaks(float nonHarmonicity, float thresold){
		this.nonHarmonicity = nonHarmonicity;
		this.thresold = thresold;
	};
	
	public void receivedMsg( Msg msg ) {		
		
		if (msg instanceof MsgAudioSpectrum) {
			MsgAudioSpectrum mas = (MsgAudioSpectrum) msg;
			signalSpectrum = mas.getAudioSpectrum();
			spectrumLength = mas.getAudioSpectrumLength();
			dF = mas.deltaF;		
		}
		
		if (msg instanceof MsgAudioFundamentalFrequency) {
			MsgAudioFundamentalFrequency maff = (MsgAudioFundamentalFrequency) msg;
			float fundamental = maff.fundfreq;
			
			if (fundamental == 0) // indispensable!!!
				return;
			
			int mafftime = maff.time;
			int maffduration = maff.duration;

			
			 /* double[][] vect = new double[signalSpectrum.length][2];
			 
			 for( int i = 0; i < signalSpectrum.length; i++)
			 {
			 vect[i][0] = i*dF; // prima riga: x
			 vect[i][1] = signalSpectrum[i]; // seconda riga: y
			 }
			 SimplePloter graphic = new SimplePloter("Spettro su tutto il segmento audio");
			 graphic.setAssesName("Power", "Frequency");
			 graphic.createOverlaidChart(vect,graphic.SOLIDLINE, "spettro");
			 graphic.myplotter( "Power Spectrum"); */
			
			
			
			ArrayList a = new ArrayList(); 
			ArrayList b = new ArrayList();
			
			// a and b delimit the range of the single peak "area"
			
			for(int h=1; Math.floor((fundamental*h/dF)) < spectrumLength; h++ )
			{	
				
				int sx = (int)Math.floor((h-nonHarmonicity)*(fundamental/dF));
				int dx = (int)Math.ceil((h+nonHarmonicity)*(fundamental/dF));
				if (dx < spectrumLength) {
					a.add(new Integer(sx));
					b.add(new Integer(dx));
				}
			}
			
			// now we go from a to b and find the maximum value of amplitudeSpectrum
			// in this range; this for everyone of j peaks
			for(int j=0; j < a.size(); j++) //
			{
				int index = (((Integer)a.get(j)).intValue())-1;
				float maxAmp = signalSpectrum[index];
				int k = index+1;
				for( ; k<((Integer)b.get(j)).intValue(); k++)
					if(signalSpectrum[k]>maxAmp)
					{	
						maxAmp = signalSpectrum[k];
						index = k;
					}
					
				float[] peakData = new float[2];
				peakData[0] = dF*index;
				peakData[1] = signalSpectrum[index];
				
				peaks.add(peakData);
				
			}
			
			float[] pd = (float[])peaks.get(0);
			float maxPeak = pd[1];
			
			// search for the max amplitude's peak
			
			for( int z=1; z < peaks.size(); z++)
			{
				pd = (float[])peaks.get(z);
				if( pd[1] >= maxPeak)
					maxPeak = pd[1];
			}
			
			// remove peaks whose amplitude is less than thresold*maxPeak
			
			for( int h = 0; h < peaks.size();)
			{
				pd = (float[])peaks.get(h);
				if( pd[1] < (thresold*maxPeak))
					peaks.remove(h);
				else h++;
			}
			
			peaks.trimToSize(); // wipe out the null elements
			
			 /* Iterator i = peaks.iterator();
			 while( i.hasNext() )
			 {
			 	float[] x = (float[])i.next();
			 	System.out.print( x[0] + "," + x[1] + " ");
			 }
			
			 System.out.println(""); */
			
			send( new MsgHarmonicPeaks(mafftime, maffduration, peaks));
			peaks.clear();
			
		}
		
	}
	
	
}








;