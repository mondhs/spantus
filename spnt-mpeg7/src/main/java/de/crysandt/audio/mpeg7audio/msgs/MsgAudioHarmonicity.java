/*
  Copyright (c) 2003, Francesco Saletti

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio.msgs;

/**
 * User: Francesco
 * Date: Nov 18, 2003
 * Time: 5:26:04 PM
 */
public class MsgAudioHarmonicity
    extends Msg
{
    public final float harmonicratio;
    public final float upperlimit;

    public MsgAudioHarmonicity(int time,
                               int duration,
                               float harmonicratio,
                               float upperlimit)
    {
        super(time, duration);
        this.harmonicratio = harmonicratio;
        this.upperlimit = upperlimit;
    }
}
