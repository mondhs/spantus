package org.spantus.chart.service;

import java.awt.Color;

import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;

public interface IColorResolver {
	public Color resolveColor(IExtractor extr);
	public Color resolveColor(IExtractorVector extr);
	public VectorSeriesColorEnum resolveColorType(IExtractorVector extr);

}
