/*
 * Copyright (c) 2004, Michele Bartolucci
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package it.univpm.deit.audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

/**
 * @author <a href="mailto:micky78@email.it">Michele Bartolucci</a>
 */
public class SignalEnvelope 
	extends MsgSpeaker 
	implements MsgListener
{	
	private static final int LENGTH_FRAME = 30; /* ms */
	
	private ArrayList<Float> signalEnv = new ArrayList<Float>();
	private LinkedList<MsgResizer> msglist = new LinkedList<MsgResizer>();
	private int position = 0;
	private int windowlength, windowslide;
	
	public SignalEnvelope( int windowlength, int windowslide) {
		this.windowlength = windowlength;
		this.windowslide = windowslide;
	}
	
	public void receivedMsg(Msg m) {
		if (m instanceof MsgResizer)
			receivedMsg((MsgResizer) m);
		if (m instanceof MsgEndOfSignal)
			receivedMsg((MsgEndOfSignal) m);
		
	}
	
	private void receivedMsg(MsgResizer m) {
		
		msglist.addLast(m);
		if( position != 0)
			position -= m.getSignalLength();
		
		// check if one or more messages can be appended to one signal
		// with the length of LENGTH_WINDOW
		if (!(LENGTH_FRAME % m.duration == 0 ))
			throw new AssertionError ();
		
		if( msglist.size()*m.duration == LENGTH_FRAME ) {
			
			// calculate length of signal
			int length = 0;
			Iterator<MsgResizer> i = msglist.iterator();
			while( i.hasNext() )
				length += ((MsgResizer)(i.next())).getSignalLength();
			
			float[] s = new float[ length ];
			
			
			// merge msgs to signal
			i = msglist.iterator();
			int index = 0;
			while( i.hasNext() ) {
				float[] source = ((MsgResizer) i.next()).getSignal();
				System.arraycopy(source, 0, s, index, source.length);
				index += source.length;
			}

			float irms;
			int k;
			
			for(k = position; k <= (s.length)-windowlength; k+= windowslide)
			{
				irms = 0;
				int j;
				
				for( j = k; j < (k+windowlength); j++ )
				{
					irms += Function.square(s[j]);
				}
				irms /= (j-k);
				irms = (float)Math.sqrt(irms);
				signalEnv.add(new Float(irms));
			}
			
			position = k;
			msglist.removeFirst();
			
		}
		
		signalEnv.trimToSize();
		
	}
	
	
	public void receivedMsg( MsgEndOfSignal meos ) {
		int time, duration;
		time = meos.time;
		duration = meos.duration;
		
		 /* double[][] vect = new double[signalEnv.size()][2];
		 
		 for( int i = 0; i < signalEnv.size(); i++)
		 {
		 vect[i][0] = (float)(i*((float)windowslide/44100)); // prima riga: x
		 vect[i][1] = ((Float)signalEnv.get(i)).floatValue(); // seconda riga: y
		 }
		 SimplePloter graphic = new SimplePloter("Signal Envelope");
		 graphic.setAssesName("Ampiezza", "Tempo");
		 graphic.createOverlaidChart(vect,graphic.SOLIDLINE, "Envelope");
		 graphic.myplotter( "Signal Envelope"); */
		
		send( new MsgSignalEnvelope(time, duration, signalEnv, 
				windowslide) );
		send(meos);
	}
	
}
