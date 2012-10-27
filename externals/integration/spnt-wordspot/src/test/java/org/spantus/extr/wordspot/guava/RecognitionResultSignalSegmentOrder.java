package org.spantus.extr.wordspot.guava;

import java.util.Map.Entry;

import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
/**
 * 
 * @author mondhs
 * @since 0.3
 *
 */
public class RecognitionResultSignalSegmentOrder extends Ordering<Entry<RecognitionResult, SignalSegment>> {
	@Override
	public int compare(Entry<RecognitionResult, SignalSegment> left,
			Entry<RecognitionResult, SignalSegment> right) {
		return Longs.compare(left.getValue().getMarker().getStart(), right
				.getValue().getMarker().getStart());
	}
}
