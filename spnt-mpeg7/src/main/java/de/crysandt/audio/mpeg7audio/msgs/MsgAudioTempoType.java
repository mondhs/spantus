/*
 * Created on 27-gen-2004
 * 
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.msgs;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MsgAudioTempoType
    extends Msg
{
	public int loLimit;
    public int hiLimit;
    public float bpm;
    public float confidence;
	public int meterNumerator;
	public int meterDenominator;
	
	public MsgAudioTempoType(int time, int duration,int hopsize, int loLimit, int hiLimit, float bpm, float confidence, int meterNumerator, int meterDenominator) {
		super(time, duration, hopsize);
        this.loLimit=loLimit;
        this.hiLimit=hiLimit;
		this.bpm=bpm;
		this.confidence = confidence;
		this.meterDenominator=meterDenominator;
		this.meterNumerator=meterNumerator;
	}
}
