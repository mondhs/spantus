/*
  Copyright (c) 2002-2003, Giuliano Marozzi - Mpeg 7 team

  This file is part of the MPEG7AudioEnc project.
*/

package it.univpm.deit.audio;

import java.util.LinkedList;

import de.crysandt.audio.mpeg7audio.msgs.*;

/**
 * @author Giuliano Marozzi
 */
public class Silence
    extends MsgSpeaker
    implements MsgListener
{
  private float power = 0.0f;
  private float ZCR = 0.0f;
  private int start_time = 0;
  private int sil_dur = 0;
  private boolean first = false;
  private boolean start_sil = false;
  private boolean end_sil = false;
  private LinkedList<MsgResizer> msglist = new LinkedList<MsgResizer>();
  private int min_dur = 0;
  private float[] buffer = {0, 0, 0};
  private float tot_conf = 0.0f;
  private float confidence = 0.0f;
  private int levelofref = 0;


  public Silence(int min_dur, int levelofref) {
    this.min_dur = min_dur;
    this.levelofref = levelofref;
  }

	public void receivedMsg(Msg msg) {
    if (msg instanceof MsgAudioSpectrum)
      receivedMsg((MsgAudioSpectrum) msg);
    else if (msg instanceof MsgResizer)
      receivedMsg((MsgResizer) msg);
  }

  public void receivedMsg(MsgAudioSpectrum mas)
  { // Power Spectrum
    float[] spectrum = mas.getAudioSpectrum();
    power = 0.0f;
    for (int j = 0; j < spectrum.length; ++j)
      power += spectrum[j];
  }


  public void flush() {
    end_sil=true;
    endOfSilence((MsgResizer) msglist.getLast());
    super.flush();
  }

  private void endOfSilence(Msg mes) {
    if((start_sil==true) && (end_sil==true) ) {
      sil_dur=mes.duration+mes.time-start_time;
      if(sil_dur>min_dur) {
        send(new MsgSilence(start_time,sil_dur,min_dur,(tot_conf*mes.hopsize)/sil_dur));
      }
      start_sil=false;
      end_sil=false;
      start_time=0;
      tot_conf=0;
      first=true;
    }
  }


	public void receivedMsg(MsgResizer msg) {

		// Zero Crossing Rate
	    msglist.addLast( msg );
		if(msglist.size()==3)
		{
		MsgResizer mes =(MsgResizer) msglist.getFirst();
		float[] x = mes.getSignal();
		float n = 0;
		int temoin = 1;

		for (int j = 2; j < x.length; j++)  {
			if ((sign(x[j])==sign(x[j-temoin])) || (sign(x[j])==0))
				temoin = temoin+1;
			else  {
				n = n+temoin;
				temoin = 1;
			}
		}
		ZCR=n/x.length ;

		for(int j=2; j>0; j--)
			buffer[j]=buffer[j-1];
    buffer[0]=ZCR*power;

		if(first==true && ZCR*power<levelofref)
			start_time=mes.time;

		float num_el=0;
		for(int j=0; j<3; j++)
			num_el+=buffer[j];
		num_el/=3;
		if(num_el<levelofref) {
			// SILENCE DETECTED
			confidence=1-num_el/levelofref;
			tot_conf+=confidence;
			first=false;
			start_sil=true;
			end_sil=false;
		}
		else
		{
			end_sil=true;
			first=true;
		}
		endOfSilence(mes);
		msglist.removeFirst();
		}
	}

	int sign(float x)
	{
		if(x>0)
			return 1;
		else
			if(x<0)
				return -1;
			else
				return 0;

	}
}

