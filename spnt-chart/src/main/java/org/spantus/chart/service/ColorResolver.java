package org.spantus.chart.service;

import java.awt.Color;

import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;

public class ColorResolver implements IColorResolver{
	
	public static Color[] LINE_COLORS = new Color[] {Color.BLACK, Color.BLUE,  Color.GREEN, Color.RED, 
		Color.GRAY, Color.ORANGE };
	public int i = 0; 
	
	public Color resolveColor(IExtractor extr){
		return LINE_COLORS[i++ % LINE_COLORS.length];
	}
	public VectorSeriesColorEnum resolveColorType(IExtractorVector extr){
		return VectorSeriesColorEnum.blackWhite;
	}
	public Color resolveColor(IExtractorVector extr) {
		return LINE_COLORS[i++ % LINE_COLORS.length];
	}
}
