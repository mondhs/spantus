package org.spantus.mpeg7;

public enum Mpeg7ExtractorEnum {
	//AudioSignature, SoundModel,Silence, AudioTempoType;// n/a
	//DigitalClip, SampleHold, DigitalZero, Click,BackgroundNoiseLevel,DcOffset,BandWidth,HarmonicPeaks,//AudioSignalQuality problem

	//AudioSignature(Mpeg7ExtractorTypeEnum.Basic),
	//SoundModel(Mpeg7ExtractorTypeEnum.Basic),
	//Silence(Mpeg7ExtractorTypeEnum.Basic),
	//AudioTempoType(Mpeg7ExtractorTypeEnum.Basic),
	
	//Basic
	AudioWaveform(Mpeg7ExtractorTypeEnum.Basic), 
	AudioPower(Mpeg7ExtractorTypeEnum.Basic), 

	
	//Basic Spectral
	AudioSpectrumEnvelope(Mpeg7ExtractorTypeEnum.BasicSpectral),
//	AudioSpectrumCentroidSpread(Mpeg7ExtractorTypeEnum.BasicSpectral),//generates: AudioSpectrumSpread, AudioSpectrumCentroid
	AudioSpectrumSpread(Mpeg7ExtractorTypeEnum.BasicSpectral), 
	AudioSpectrumCentroid(Mpeg7ExtractorTypeEnum.BasicSpectral),
	
	AudioSpectrumDistribution(Mpeg7ExtractorTypeEnum.BasicSpectral),
	AudioSpectrumFlatness(Mpeg7ExtractorTypeEnum.BasicSpectral),
	
	//Signal Parameters
	AudioFundamentalFrequency(Mpeg7ExtractorTypeEnum.SignalParameters), 
	AudioHarmonicity(Mpeg7ExtractorTypeEnum.SignalParameters),  
	
	//Timbral Temporal
//	LogAttackTime(Mpeg7ExtractorTypeEnum.TimbralTemporal),//scalar
//	TemporalCentroid(Mpeg7ExtractorTypeEnum.TimbralTemporal),//scalar
	
	//Timbral Spectral
//	SpectralCentroid(Mpeg7ExtractorTypeEnum.TimbralSpectral),//Scalar
//	HarmonicSpectralCentroid(Mpeg7ExtractorTypeEnum.TimbralSpectral),
//	HarmonicSpectralDeviation(Mpeg7ExtractorTypeEnum.TimbralSpectral),
//	HarmonicSpectralSpread(Mpeg7ExtractorTypeEnum.TimbralSpectral),
//	HarmonicSpectralVariation(Mpeg7ExtractorTypeEnum.TimbralSpectral),

	//Spectral Basis
	//AudioSpectrumBasisProjection(Mpeg7ExtractorTypeEnum.SpectralBasis)
	//generates: 
	AudioSpectrumBasis(Mpeg7ExtractorTypeEnum.SpectralBasis),
	AudioSpectrumProjection(Mpeg7ExtractorTypeEnum.SpectralBasis)

	;
	
	Mpeg7ExtractorTypeEnum typeEnum;
	Mpeg7ExtractorEnum(Mpeg7ExtractorTypeEnum typeEnum ){
		this.typeEnum = typeEnum;
	}
	
}
