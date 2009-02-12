/*
 * Created on Jul 19, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import java.net.*;

import de.crysandt.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class MediaHelper
{

/* public helper functions */

public static MediaInformation createMediaInformation( )
{
	return(new MediaInformation(new MediaProfile()));
}

/**
 * Sets the URI of the media instance.<br>
 * XPath: ./MediaProfile/MediaInstance/MediaLocator/MediaUri
 * @param media_info		MediaInformation node
 * @param media_uri			the URI of the media instance (file)
 */
public static void setMediaLocation(
	MediaInformation media_info,
	URI media_uri )
{
	getMediaLocator(media_info).setMediaUri(media_uri);
}

/**
 * Sets the size of the file storing the multimedia content.<br>
 * XPath: ./MediaProfile/MediaFormat/FileSize
 * @param media_info		MediaInformation node
 * @param size				file size
 */
public static void setFileSize(
	MediaInformation media_info,
	long size )
{
	getMediaFormat(media_info).setFileSize(size);
}

/**
 * Sets the content type of the media.<br>
 * XPath: ./MediaProfile/MediaFormat/Content
 * @param media_info		MediaInformation node
 * @param content_type		media content type (see class "MFContent" for a list of available types)
 */
public static void setContentType(
	MediaInformation media_info,
	int content_type )
{
	getContent(media_info).setContentType(content_type);
}

/**
 * Indicates the bandwidth covered by the coded multimedia content.<br>
 * XPath: ./MediaProfile/MediaFormat/Bandwidth
 * @param media_info		MediaInformation node
 * @param bandwidth			bandwidth range in Hz
 */
public static void setBandwidth(
	MediaInformation media_info,
	float bandwidth )
{
	getMediaFormat(media_info).setBandwidth(bandwidth);
}

/**
 * Sets the nominal bit rate of the media profile.<br>
 * XPath: ./MediaProfile/MediaFormat/BitRate
 * @param media_info		MediaInformation node
 * @param bitrate			bit rate in bit/s
 */
public static void setBitRate(
	MediaInformation media_info,
	int bitrate )
{
	getMediaFormat(media_info).setBitRate(bitrate);
}

/**
 * Indicates whether the bit rate is variable or fixed.<br
 * XPath: ./MediaProfile/MediaFormat/BitRate@variable
 * @param media_info		MediaInformation node
 * @param variable			true, if the bit rate should be variable
 */
public static void setBitRateVariable(
	MediaInformation media_info,
	boolean variable )
{
	getMediaFormat(media_info).setBitRateVariable(variable);
}

/**
 * Sets the minimum numerical value of the bit rate in case it is variable.<br>
 * XPath: ./MediaProfile/MediaFormat/BitRate@minimum
 * @param media_info		MediaInformation node
 * @param minimum			minimum bit rate
 */
public static void setBitRateMinimum(
	MediaInformation media_info,
	int minimum )
{
	getMediaFormat(media_info).setBitRateMinimum(minimum);
}

/**
 * Sets the average numerical value of the bit rate in case it is variable.<br>
 * XPath: ./MediaProfile/MediaFormat/BitRate@average
 * @param media_info		MediaInformation node
 * @param average			average bit rate
 */
public static void setBitRateAverage(
	MediaInformation media_info,
	int average )
{
	getMediaFormat(media_info).setBitRateAverage(average);
}

/**
 * Sets the maximum numerical value of the bit rate in case it is variable.<br>
 * XPath: ./MediaProfile/MediaFormat/BitRate@maximum
 * @param media_info		MediaInformation node
 * @param minimum			maximum bit rate
 */
public static void setBitRateMaximum(
	MediaInformation media_info,
	int maximum )
{
	getMediaFormat(media_info).setBitRateMaximum(maximum);
}

/**
 * Sets the number of audio channels.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/AudioChannels
 * @param media_info		MediaInformation node
 * @param channels			number of audio channels
 */
public static void setChannels(
	MediaInformation media_info,
	int channels )
{
	getAudioCoding(media_info).setAudioChannels(channels);
}

/**
 * Sets the number of front audio channels.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/AudioChannels@front
 * @param media_info		MediaInformation node
 * @param ch_front			number of front audio channels
 */
public static void setChannelsFront(
	MediaInformation media_info,
	int ch_front )
{
	getAudioCoding(media_info).setAudioChannelsFront(ch_front);
}

/**
 * Sets the number of side audio channels.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/AudioChannels@side
 * @param media_info		MediaInformation node
 * @param ch_side			number of side audio channels
 */
public static void setChannelsSide(
	MediaInformation media_info,
	int ch_side )
{
	getAudioCoding(media_info).setAudioChannelsSide(ch_side);
}

/**
 * Sets the number of rear audio channels.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/AudioChannels@rear
 * @param media_info		MediaInformation node
 * @param ch_rear			number of rear audio channels
 */
public static void setChannelsRear(
	MediaInformation media_info,
	int ch_rear )
{
	getAudioCoding(media_info).setAudioChannelsRear(ch_rear);
}

/**
 * Sets the number of low frequency enhancement audio channels.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/AudioChannels@lfe
 * @param media_info		MediaInformation node
 * @param ch_lfe			number of LFE audio channels
 */
public static void setChannelsLFE(
	MediaInformation media_info,
	int ch_lfe )
{
	getAudioCoding(media_info).setAudioChannelsLfe(ch_lfe);
}

/**
 * Sets the number of independent audio tracks used in multilingual audio streams.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/AudioChannels@track
 * @param media_info		MediaInformation node
 * @param tracks			number of audio tracks
 */
public static void setChannelsTrack(
	MediaInformation media_info,
	int tracks )
{
	getAudioCoding(media_info).setAudioChannelsTrack(tracks);
}

/**
 * Indicates the sampling rate for audio.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/Sample@rate
 * @param media_info		MediaInformation node
 * @param rate				sampling rate in Hz
 */
public static void setSampleRate(
	MediaInformation media_info,
	float rate )
{
	getAudioCoding(media_info).setSampleRate(rate);
}

/**
 * Indicates the audio sample accuracy in bits per sample.<br>
 * XPath: ./MediaProfile/MediaFormat/AudioCoding/Sample@bitsPer
 * @param media_info		MediaInformation node
 * @param bits				bits per sample
 */
public static void setBitsPerSample(
	MediaInformation media_info,
	int bits )
{
	getAudioCoding(media_info).setSampleBitsPer(bits);
}

/* private helper functions which create the structure */

private static MediaProfile getMediaProfile(
	MediaInformation media_info )
{
	if (media_info.getMediaProfiles().size() == 0)
		media_info.getMediaProfiles().add(new MediaProfile());
	return((MediaProfile)media_info.getMediaProfiles().get(0));
}

private static MediaFormat getMediaFormat(
	MediaInformation media_info )
{
MediaProfile		media_profile;

	media_profile = getMediaProfile(media_info);
	if (media_profile.getMediaFormat() == null)
		media_profile.setMediaFormat(new MediaFormat(new MFContent()));
	return(media_profile.getMediaFormat());
}

private static MFAudioCoding getAudioCoding(
	MediaInformation media_info )
{
MediaFormat			media_format;

	media_format = getMediaFormat(media_info);
	if (media_format.getAudioCoding() == null)
		media_format.setAudioCoding(new MFAudioCoding());
	return(media_format.getAudioCoding());
}

private static MediaInstance getMediaInstance(
	MediaInformation media_info )
{
MediaProfile		media_profile;

	media_profile = getMediaProfile(media_info);
	try
	{
		if ( media_profile.getMediaInstances().size() == 0 )
			media_profile.getMediaInstances().add(new MediaInstance(new UniqueID(), new MediaLocator(new URI(""))));
	}
	catch(Exception exc)
	{
		Debug.printStackTrace(System.err, exc);
	}
	return((MediaInstance)media_profile.getMediaInstances().get(0));
}

private static MediaLocator getMediaLocator(
	MediaInformation media_info )
{
MediaInstance		media_instance;

	media_instance = getMediaInstance(media_info);
	try
	{
		if ( media_instance.getMediaLocator() == null )
			media_instance.setMediaLocator(new MediaLocator(new URI("")));
	}
	catch(Exception exc)
	{
		Debug.printStackTrace(System.err, exc);
	}
	return(media_instance.getMediaLocator());
}


private static MFContent getContent(
	MediaInformation media_info )
{
MediaFormat			media_format;

	media_format = getMediaFormat(media_info);
	if (media_format.getContent() == null)
		media_format.setContent(new MFContent());
	return(media_format.getContent());
}

}
