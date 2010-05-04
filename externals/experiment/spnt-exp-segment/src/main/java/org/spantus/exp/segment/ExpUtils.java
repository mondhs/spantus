package org.spantus.exp.segment;

import org.jfree.data.xy.XYSeries;
import org.spantus.core.FrameValues;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created May 4, 2010
 *
 */
public abstract class ExpUtils {
	public static void fillSeries(FrameValues values, XYSeries series, String description){
		series.setDescription(description);
		int i = 0;
		for (Float f1 : values) {
			series.add(Float.valueOf(i), f1);
			i++;
		}
	}
}
