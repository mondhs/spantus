/*
 * Created on 01.07.2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MFAudioCoding
{
/*	<element name="AudioCoding" minOccurs="0">
		<complexType>
			<sequence>
				<element name="Format" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
				<element name="AudioChannels" minOccurs="0">
					<complexType>
						<simpleContent>
							<extension base="nonNegativeInteger">
								<attribute name="front" type="nonNegativeInteger" use="optional"/>
								<attribute name="side" type="nonNegativeInteger" use="optional"/>
								<attribute name="rear" type="nonNegativeInteger" use="optional"/>
								<attribute name="lfe" type="nonNegativeInteger" use="optional"/>
								<attribute name="track" type="nonNegativeInteger" use="optional"/>
							</extension>
						</simpleContent>
					</complexType>
				</element>
				<element name="Sample" minOccurs="0">
					<complexType>
						<attribute name="rate" type="mpeg7v1:nonNegativeReal" use="optional"/>
						<attribute name="bitsPer" type="nonNegativeInteger" use="optional"/>
					</complexType>
				</element>
				<element name="Emphasis" minOccurs="0">
					<simpleType>
						<union>
							<simpleType>
								<restriction base="NMTOKEN">
									<enumeration value="none"/>
									<enumeration value="50over15Microseconds"/>
									<enumeration value="ccittJ17"/>
								</restriction>
							</simpleType>
							<simpleType>
								<restriction base="mpeg7v1:termReferenceType"/>
							</simpleType>
						</union>
					</simpleType>
				</element>
				<element name="Presentation" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
			</sequence>
		</complexType>
	</element> */

private ControlledTermUse			format;					// cardinality: 0 - 1
private Integer						audio_channels;			// cardinality: 0 - 1
private Integer						audio_channels_front;	// use: optional
private Integer						audio_channels_side;	// use: optional
private Integer						audio_channels_rear;	// use: optional
private Integer						audio_channels_lfe;		// use: optional
private Integer						audio_channels_track;	// use: optional
private Float						sample_rate;			// use: optional
private Integer						sample_bits_per;		// use: optional
//	   not complete yet
private ControlledTermUse			presentation;			// cardinality: 0 - 1

public MFAudioCoding( )
{
	format = null;
	audio_channels = null;
	audio_channels_front = null;
	audio_channels_side = null;
	audio_channels_rear = null;
	audio_channels_lfe = null;
	audio_channels_track = null;
	sample_rate = null;
	sample_bits_per = null;
	presentation = null;
}

public Element toXML( Document doc, String name )
{
Element		coding_ele;
Element		channels_ele;
Element		sample_ele;

	coding_ele = doc.createElementNS(Namespace.MPEG7, name);

	if ( format != null )
		coding_ele.appendChild(format.toXML(doc, "Format"));

	if (	( audio_channels != null ) ||
			( audio_channels_front != null ) ||
			( audio_channels_side != null ) ||
			( audio_channels_rear != null ) ||
			( audio_channels_lfe != null ) ||
			( audio_channels_track != null ) )
	{
		channels_ele = doc.createElement("AudioChannels");
		coding_ele.appendChild(channels_ele);
	
		if ( audio_channels_front != null )
			channels_ele.setAttribute("front", audio_channels_front.toString());

		if ( audio_channels_side != null )
			channels_ele.setAttribute("side", audio_channels_side.toString());
		
		if ( audio_channels_rear != null )
			channels_ele.setAttribute("rear", audio_channels_rear.toString());
	
		if ( audio_channels_lfe != null )
			channels_ele.setAttribute("lfe", audio_channels_lfe.toString());
		
		if ( audio_channels_track != null )
			channels_ele.setAttribute("track", audio_channels_track.toString());
		
		if ( audio_channels != null )
			Utils.setContent(doc, channels_ele, audio_channels.toString());
	}

	if (	( sample_rate != null ) ||
			( sample_bits_per != null ) )
	{
		sample_ele = doc.createElement("Sample");
		coding_ele.appendChild(sample_ele);
	
		if ( sample_rate != null )
			sample_ele.setAttribute("rate", sample_rate.toString());

		if ( sample_bits_per != null )
			sample_ele.setAttribute("bitsPer", sample_bits_per.toString());
	}

	if ( presentation != null )
		coding_ele.appendChild(presentation.toXML(doc, "Presentation"));

	return(coding_ele);
}

public int getAudioChannels( )
{
	return(audio_channels.intValue());
}

public int getAudioChannelsFront( )
{
	return(audio_channels_front.intValue());
}

public int getAudioChannelsLfe( )
{
	return(audio_channels_lfe.intValue());
}

public int getAudioChannelsRear( )
{
	return(audio_channels_rear.intValue());
}

public int getAudioChannelsSide( )
{
	return(audio_channels_side.intValue());
}

public int getAudioChannelsTrack( )
{
	return(audio_channels_track.intValue());
}

public ControlledTermUse getFormat( )
{
	return(format);
}

public ControlledTermUse getPresentation( )
{
	return(presentation);
}

public int getSampleBitsPer( )
{
	return(sample_bits_per.intValue());
}

public float getSampleRate( )
{
	return(sample_rate.floatValue());
}

public void setAudioChannels( int audio_channels )
{
	this.audio_channels = new Integer(audio_channels);
}

public void setAudioChannelsFront( int front )
{
	this.audio_channels_front = new Integer(front);
}

public void setAudioChannelsLfe( int lfe )
{
	this.audio_channels_lfe = new Integer(lfe);
}

public void setAudioChannelsRear( int rear )
{
	this.audio_channels_rear = new Integer(rear);
}

public void setAudioChannelsSide( int side )
{
	this.audio_channels_side = new Integer(side);
}

public void setAudioChannelsTrack( int track )
{
	this.audio_channels_track = new Integer(track);
}

public void setFormat( ControlledTermUse format )
{
	this.format = format;
}

public void setPresentation( ControlledTermUse presentation )
{
	this.presentation = presentation;
}

public void setSampleBitsPer( int bits )
{
	this.sample_bits_per = new Integer(bits);
}

public void setSampleRate( float rate )
{
	this.sample_rate = new Float(rate);
}

}
