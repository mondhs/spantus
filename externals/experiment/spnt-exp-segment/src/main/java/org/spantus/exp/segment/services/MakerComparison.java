package org.spantus.exp.segment.services;

import org.spantus.core.marker.MarkerSet;
import org.spantus.exp.segment.beans.ComparisionResult;

public interface MakerComparison {
	public ComparisionResult compare(MarkerSet original,MarkerSet test);
}
