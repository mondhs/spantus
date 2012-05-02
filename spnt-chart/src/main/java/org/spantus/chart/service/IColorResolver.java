package org.spantus.chart.service;

import java.awt.Color;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.ui.chart.VectorSeriesColorEnum;

public interface IColorResolver {
	public Color resolveColor(IExtractor extr);
	public Color resolveColor(IExtractorVector extr);
	public VectorSeriesColorEnum resolveColorType(IExtractorVector extr);

}
