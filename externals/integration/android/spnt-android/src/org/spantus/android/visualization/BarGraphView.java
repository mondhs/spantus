/*  Copyright 2011  Ashwin Kumar  (email : ashwin@linkwithweb.com)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License, version 2, as
    published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.spantus.android.visualization;

/**
 * @author Ashwin Kumar
 *
 */
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Ashwin Kumar
 * 
 */
public class BarGraphView extends View {

	private Paint paint;
	private Map<String, Double> values;

	public BarGraphView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		values = new LinkedHashMap<String, Double>();
		values.put("vienas", 1.0);
		values.put("du", 2.0);
		values.put("trys", 3.0);
		paint = new Paint();
	}

	// public BarGraphView(Context context, float[] values, String title,
	// String[] horlabels, String[] verlabels, boolean type) {
	// super(context);
	// if (values == null)
	// values = new float[0];
	// else
	// this.values = values;
	// if (title == null)
	// title = "";
	// else
	// this.title = title;
	// if (horlabels == null)
	// this.horlabels = new String[0];
	// else
	// this.horlabels = horlabels;
	// if (verlabels == null)
	// this.verlabels = new String[0];
	// else
	// this.verlabels = verlabels;
	// this.type = type;
	// paint = new Paint();
	// }

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
//		float horstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;
		Double max = getMax();
		Double min = getMin();
		Double diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);

		paint.setTextAlign(Align.LEFT);
		int hors = values.size() - 1;

		paint.setTextAlign(Align.CENTER);
		// canvas.drawText(title, (graphwidth / 2) + horstart, border - 4,
		// paint);

		if (max != min) {
			paint.setColor(Color.LTGRAY);
			float datalength = values.size();
//			float colwidth = (width - (2 * border)) / datalength;
			float colHeight = (height - (2 * border)) / datalength;
			int i = 0;
			for (Entry<String, Double> valEntry : values.entrySet()) {
				Double val = valEntry.getValue() - min;
				Double rat = (val / diff)+.2D;
				Double wD = graphwidth * rat;
				Float w = wD.floatValue();
				canvas.drawRect(border+5, (i * colHeight) ,
						(border - w) +3+ graphwidth, ((i * colHeight))-5
								+ (colHeight - 1), paint);
				i++;
			}

			int ih = 0;
			for (Entry<String, Double> valEntry : values.entrySet()) {
				paint.setColor(Color.RED);
				// float x = ((graphwidth / hors) * ih) + horstart;
				float y = ((graphheight / hors) * ih) + 15;
				// canvas.drawLine(x, height - border, x, border, paint);
//				canvas.drawLine(width - border, y, border, y, paint);
//				paint.setTextAlign(Align.CENTER);
//				if (ih == values.size() - 1) {
//					paint.setTextAlign(Align.RIGHT);
//				}
//				if (ih == 0) {
//					paint.setTextAlign(Align.LEFT);
//				}
				paint.setColor(Color.RED);
				// canvas.drawText(valEntry.getKey(), x, height - 4, paint);
				canvas.drawText(valEntry.getKey(), 25, y, paint);
				ih++;
			}

		}

	}

	private Double getMax() {
		Double largest = -Double.MAX_VALUE;
		for (Double val : values.values()) {
			largest = Math.max(val, largest);
		}
		return largest;
	}

	private Double getMin() {
		Double smallest = Double.MAX_VALUE;
		for (Double val : values.values()) {
			smallest = Math.min(val, smallest);
		}
		return smallest;
	}

	public Map<String, Double> getValues() {
		return values;
	}

	public void setValues(Map<String, Double> values) {
		this.values = values;
	}

}
