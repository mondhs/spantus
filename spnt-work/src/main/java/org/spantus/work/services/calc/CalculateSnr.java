package org.spantus.work.services.calc;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.MarkerSet;

public interface CalculateSnr {

	public Double calculate(IExtractor iExtractor, MarkerSet segments);

}
