/*
  Copyright (c) 2004, Michele Bartolucci
  This file is part of the MPEG7AudioEnc project.
*/

package it.univpm.deit.audio;

import de.crysandt.audio.mpeg7audio.msgs.*;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class SpectralCentroid 
	extends MsgSpeaker
	implements MsgListener
{
	
	private float samplerate;
	private float[] sumArray;
	private boolean isFirst = true;
	private int count = 0;
	
	public SpectralCentroid(float samplerate){
		this.samplerate = samplerate;
	}
		
	public void receivedMsg( Msg m ) {
		if (m instanceof MsgAudioSpectrum)
			receivedMsg((MsgAudioSpectrum) m);
		if (m instanceof MsgEndOfSignal)
			receivedMsg((MsgEndOfSignal) m);
	}
	
	public void receivedMsg( MsgAudioSpectrum m ) {

		float[] spectrum = m.getAudioSpectrum();
		
		if( isFirst ) {
			sumArray = new float[spectrum.length];
			isFirst = false;
		}
	
		
		for( int i = 0; i < spectrum.length; i++ )
			sumArray[i] += spectrum[i];
		
		count += 1;
	}
	
	public void receivedMsg( MsgEndOfSignal meos )
	{
		for( int i = 0; i < sumArray.length; i++)
			sumArray[i] /= count;
		
		float dF = (samplerate/2)/sumArray.length;
		
		/*		double[][] vect = new double[sumArray.length][2];
		 
		 for( int i = 0; i < sumArray.length; i++)
		 {
		 vect[i][0] = i*dF; // prima riga: x
		 vect[i][1] = sumArray[i]; // seconda riga: y
		 }
		 SimplePloter graphic = new SimplePloter("Spettro su tutto il segmento audio");
		 graphic.setAssesName("Power", "Frequency");
		 graphic.createOverlaidChart(vect,graphic.SOLIDLINE, "spettro");
		 graphic.myplotter( "Power Spectrum");  */

		float num = 0;
		float den = 0;
		for( int i = 0; i < sumArray.length; i++)
		{
			num += (dF*i) * sumArray[i];
			den += sumArray[i];
		}
		
		float spectralCentroid = num/den;
		
		send( new MsgSpectralCentroid(meos.time, meos.duration, spectralCentroid));
		send(meos);
		
	}
	
}
