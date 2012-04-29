package org.spantus.work.services.calc;

import java.util.Map;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.MarkerSet;

public interface CalculateSnr {
	enum segmentStatics{mean, stDev, min, max}

	public Double calculate(IExtractor iExtractor, MarkerSet segments);
	public Map<segmentStatics, Double> calculateStatistics(IExtractor iExtractor, Long start, Long length);

}
