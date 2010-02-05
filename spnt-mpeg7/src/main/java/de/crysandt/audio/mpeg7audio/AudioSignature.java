/*
 Copyright (c) 2002-2003, Holger Crysandt

 This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import java.util.*;

import de.crysandt.audio.mpeg7audio.msgs.*;
import de.crysandt.math.Function;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
class AudioSignature
    extends MsgSpeaker
    implements MsgListener
{
  private static final int HOP_SIZE = 30; /* ms */

  private final int DECIMATION;

  private final static float LO_EDGE = 250.0f;
  private final static float HI_EDGE = 4000.0f;

  private LinkedList<MsgAudioSpectrumFlatness> msglist =  new LinkedList<MsgAudioSpectrumFlatness>();

  public AudioSignature( int decimation ) {
    this.DECIMATION = decimation;
  }

  public void receivedMsg(Msg msg){
  	if (msg instanceof MsgAudioSpectrumFlatness)
  		receivedMsg((MsgAudioSpectrumFlatness) msg);
  }

  private void receivedMsg(MsgAudioSpectrumFlatness masf) {
    if (masf.time % HOP_SIZE != 0)
      return;

    msglist.addLast( masf );

    if (msglist.size() == DECIMATION) {
      masf = (MsgAudioSpectrumFlatness) msglist.getFirst();
      int    time     = masf.time;
      int    hopsize  = masf.hopsize;
      
      float  hi_edge = Math.min(masf.hi_edge, HI_EDGE);
      
      int dim250 = 4 * Math.round(Function.log2(LO_EDGE/masf.lo_edge));
      int length = 4 * Math.round(Function.log2(hi_edge/LO_EDGE));

      // copy flatness from msgs into matrix
      float[][] flatness = new float[DECIMATION][length];
      
      int index = 0;
      for (Iterator<MsgAudioSpectrumFlatness> i = msglist.iterator(); i.hasNext(); ++index) {
        System.arraycopy(
        		((MsgAudioSpectrumFlatness)i.next()).getFlatness(), dim250, 
				flatness[index], 0, 
				length);
      }

        // calulate mean and variance for each sub-band (=column)
      float[] mean = Function.mean_arith(flatness);      
      float[] var  = Function.variance(flatness, mean);

      send(new MsgAudioSignature(
      		time,
				hopsize * DECIMATION,
				HOP_SIZE,
				mean,
				var,
				hi_edge,
				DECIMATION));

        // delete old msgs
      msglist.clear();
    }
  }
}
