/*
  Copyright (c) 2002-2006, Holger Crysandt

  This file is part of MPEG7AudioEnc.
*/

package de.crysandt.audio.mpeg7audio;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public class ConfigDefault
	extends Config
{
	public ConfigDefault() {
		super();
		setValue("Resizer", "HopSize", 10);
		setValue("Resizer", "enable", true);
        
		setValue("SignalEnvelope", "windowlength", 10);
		setValue("SignalEnvelope", "windowslide", 5);
		
		setValue("LogAttackTime", "enable", false);
		setValue("LogAttackTime", "threshold", 0.02f);
		
		setValue("TemporalCentroid", "enable", false);

		setValue("AudioWaveform", "enable",true);
		setValue("DigitalClip","enable",false);
		setValue("DigitalZero","enable",false);
		setValue("SampleHold","enable",false);
		setValue("Click","enable",false);
		setValue("BackgroundNoiseLevel","enable",false);
		setValue("DcOffset", "enable",false);
		setValue("BandWidth", "enable",false);
		
		setValue("AudioPower", "enable",true);
		setValue("AudioPower", "logScale",false);

		setValue("AudioFundamentalFrequency", "enable", false);
		setValue("AudioFundamentalFrequency", "lolimit", 50.0f);
		setValue("AudioFundamentalFrequency", "hilimit", 12000.0f);

		setValue("AudioHarmonicity", "enable", false);

		setValue("HarmonicPeaks", "nonHarmonicity", 0.15f);
		setValue("HarmonicPeaks", "threshold", 0.0f);
		
		setValue("HarmonicSpectralCentroid", "enable", false);
		setValue("HarmonicSpectralVariation", "enable",false);
		setValue("HarmonicSpectralDeviation", "enable",false);
		setValue("HarmonicSpectralSpread", "enable",false);

		setValue("SpectralCentroid", "enable", false);

		setValue("AudioSpectrumCentroidSpread", "enable", true);

		setValue("AudioSpectrumBasisProjection", "enable", false);
		setValue("AudioSpectrumBasisProjection", "frames", 0);
		setValue("AudioSpectrumBasisProjection", "numic", 8);

		setValue("AudioSpectrumDistribution", "enable", false);

		setValue( "AudioSpectrumEnvelope", "enable", true);

		setValue( "AudioSpectrumEnvelope", "resolution", 0.25f);
		setValue( "AudioSpectrumEnvelope", "loEdge", 62.5f);
		setValue( "AudioSpectrumEnvelope", "hiEdge", 16000.0f);
		setValue( "AudioSpectrumEnvelope", "dbScale", false);
		setValue( "AudioSpectrumEnvelope", "normalize", "off");

		setValue( "AudioSpectrumFlatness", "enable", false);
		setValue( "AudioSpectrumFlatness", "loEdge", 250.0f);
		setValue( "AudioSpectrumFlatness", "hiEdge", 16000.0f);

		setValue( "AudioSignature", "enable", false);
		setValue( "AudioSignature", "decimation", 32);

		setValue( "SoundModel", "enable", false);
		setValue( "SoundModel", "numberOfStates", 12);
		setValue( "SoundModel", "label", "myLabel");

		setValue( "Silence", "enable", false);
		setValue( "Silence","min_dur", 300);
		setValue( "Silence","levelofref", 200);

		setValue( "AudioTempoType", "enable", false);
		setValue( "AudioTempoType", "loLimit", 40);
		setValue( "AudioTempoType", "hiLimit", 200);
		setValue( "AudioTempoType", "ATTHopsize", 3000);
	}
}
