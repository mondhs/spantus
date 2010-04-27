/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.chart.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.math.BigDecimal;
import java.text.Format;
import java.text.MessageFormat;

import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphDomain;

import org.spantus.chart.bean.ClassifierChartContext;
import org.spantus.chart.functions.FrameValueThearsholdFuncton;
import org.spantus.core.FrameValues;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.logger.Logger;

/**
 * 
 * class is created by {@link FrameValueThearsholdFuncton}
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.08.02
 * 
 */
public class ThresholdChartInstance extends TimeSeriesFunctionInstance {

	// CoordinateBoundary coordinateBoundary;

	GraphDomain domain;

	ClassifierChartContext ctx;

	private int[] polylinesX = null;
	private int[] polylinesYt = null;
	private Polygon statePoligon = null;
	private int[] polylinesY = null;
	Polygon polylygonY;
	Polygon polylygonYt;

	Logger log = Logger.getLogger(this.getClass());

	public ThresholdChartInstance(ClassifierChartContext ctx) {
		this.ctx = ctx;
		this.description = ctx.getDescription();
//		for (Float float1 : ctx.getValues()) {
//			minmax(float1);
//		}
		Float min = ctx.getValues().getMinValue();
		Float max = ctx.getValues().getMaxValue();
		setOrder(0);
		log.debug("name: " + description + "; order: " + getOrder() + "; min="
				+ min + "; max: " + max + "; sampleRate:"
				+ ctx.getValues().getSampleRate() + "; length: "
				+ ctx.getValues().size());
	}

	public void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar) {
		FrameValues clonedValues = getCtx().getValues();
//		Float _min = Float.MAX_VALUE;
//		Float _max = -Float.MAX_VALUE;
//
//		for (Float float1 : clonedValues) {
//			_min = Math.min(_min, float1);
//			_max = Math.max(_max, float1);
//		}

		FrameValues threshold = getCtx().getThreshold();

		polylinesX = toCoordinatesTime(clonedValues, xScalar.floatValue());
		polylinesY = toCoordinatesValues(clonedValues, yScalar.floatValue());
		polylinesYt = toCoordinatesValues(threshold, yScalar.floatValue());
		statePoligon =  constructStatePolygon(polylinesX, ctx.getMarkSet(), xScalar.floatValue(), yScalar.floatValue());
		polylygonY = new Polygon(polylinesX, polylinesY, polylinesX.length);
		polylygonYt = new Polygon(polylinesX, polylinesYt, polylinesX.length);

//		polylinesYstate = toCoordinatesSates(getCtx().getMarkSet(), yScalar
//				.floatValue());

	}
	/**
	 * 
	 */
	public synchronized void paintFunction(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Color currentColor = ((Color) getCtx().getStyle().getPaint());
		Color currentColorTransparent = new Color(
				currentColor.getRGB() & 0x00FFFFFF | 0x33000000, true);
//		long time = System.currentTimeMillis();
		if(polylygonY!=null){
			g.drawPolygon(polylygonY);
		}
//		log.debug("[paintFunction] y time: {0} ", ( System.currentTimeMillis()-time) );
//		time = System.currentTimeMillis();
		if (polylygonYt != null ) {
			g2.setPaint(currentColor.darker().darker());
			g2.drawPolygon(polylygonYt);
		}
//		log.debug("[paintFunction] threshold time: {0} ", ( System.currentTimeMillis()-time) );
//		time = System.currentTimeMillis();
		if(statePoligon != null){
			g2.setPaint(currentColorTransparent);
			g2.fillPolygon(statePoligon);
		}
//		log.debug("[paintFunction] state time: {0} ", ( System.currentTimeMillis()-time) );
	}
	

	private CoordinateBoundary getCoordinateBoundary(FrameValues values) {
		Float xMin = 0f;
		Float xMax = Float.valueOf(values.toTime(values.size()));
		Float yMin = getOrder();
		Float yMax = getOrder() + 1;

		if (domain != null && domain.getUntil() != null) {
			xMax = domain.getUntil().floatValue();
			xMin = domain.getFrom().floatValue();
		}
		return new CoordinateBoundary(new BigDecimal(xMin),
				new BigDecimal(xMax), new BigDecimal(yMin),
				new BigDecimal(yMax));

	}

	public CoordinateBoundary getCoordinateBoundary() {
		return getCoordinateBoundary(getCtx().getValues());
	}

	public BigDecimal[] getXCoordinates() {
		return null;
	}

	public BigDecimal[] getYCoordinates() {
		return null;
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setPaint(getCtx().getStyle().getPaint());
		paintFunction(g2);

		g2.dispose();

	}

	public void render(BigDecimal xScalar, BigDecimal yScalar, Format yFormat,
			FontMetrics fontMetrics) {
		polylinesX = null;
		polylinesYt = null;
		statePoligon = null;
		polylinesY = null;
		renderFunction(null, null, xScalar, yScalar);
	}

	public GraphDomain getDomain() {
		return domain;
	}

	public void setDomain(GraphDomain domain) {
		this.domain = domain;
	}

	private int[] toCoordinatesTime(FrameValues vals, float scalar) {

		int start = 0;
		int end = vals.size();
		if (domain != null && domain.getFrom() != null) {
			start = vals.toIndex(domain.getFrom().floatValue());
			end = vals.toIndex(domain.getUntil().floatValue());
			end = Math.min(end, vals.size());
		}
		int length = end - start;
		int[] temp = new int[length];
		Float sampledScalar =  scalar * vals.getSampleRate();
		
		for (int j = 0; j < temp.length; j++) {
			temp[j] = (int) ((j + start) / (sampledScalar));
		}
		return temp;
	}

//	Float min = Float.MAX_VALUE;
//	Float max = -Float.MAX_VALUE;
//
//	private void minmax(Float f1) {
//		min = Math.min(min, f1);
//		max = Math.max(max, f1);
//	}

	private int[] toCoordinatesValues(FrameValues vals, float scalar) {
		return toCoordinatesValues(vals, scalar, 1f);
	}
	
	/**
	 * 
	 * @param x
	 * @param markerSet
	 * @return
	 */
	protected Polygon constructStatePolygon(int[] x, MarkerSet markerSet, float xScalar, float yScalar) {
		Polygon polygon = new Polygon();// x, yState, x.length);
//		Integer prevState = null;
//		Integer prevX = null; 
//		Float delta = max - min; 
		Float maxvalueF = (1+getOrder())/yScalar;
		int maxvalue = maxvalueF.intValue(); 
		Float minvalueF = (getOrder())/yScalar;
		int minvalue = minvalueF.intValue();
		Float sampledXScalar = xScalar * getCtx().getValues().getSampleRate();
		polygon.addPoint(0, minvalue);
		for (Marker marker : markerSet.getMarkers()) {
			int startIndex = getCtx().getValues().toIndex(marker.getStart()/1000f);
			int endIndex = getCtx().getValues().toIndex(marker.getEnd()/1000f);
			Float start = startIndex/sampledXScalar;
			Float end = endIndex/sampledXScalar;
			polygon.addPoint(start.intValue(), minvalue);
			polygon.addPoint(start.intValue(), maxvalue);
			polygon.addPoint(end.intValue(), maxvalue);
			polygon.addPoint(end.intValue(), minvalue);
		}
//		for (int j = 0; j < yState.length; j++) {
//			int currX = x[j];
//			int currState = yState[j];
//			prevState = prevState == null ? currState : prevState;
//			prevX = prevX == null ? currX : prevX;
//			if (currState != prevState) {
//				polygon.addPoint(prevX, currState);
//			}
//			polygon.addPoint(currX, currState);
//			prevState = currState;
//			prevX = currX;
//		}
		return polygon;
	}

//	private int[] toCoordinatesSates(MarkerSet markerSet, float scalar) {

		// int start = 0;
		// int end = vals.size();
		// if(domain != null && domain.getFrom()!=null){
		// start = vals.toIndex(domain.getFrom().floatValue());
		// end = vals.toIndex(domain.getUntil().floatValue());
		// end = Math.min(end, vals.size());
		// }
		// int length = end-start;
		// int[] temp = new int[length];
		//
		//
		// int i=0,skip=0;
		// float delta = max-min;
		// for (Float floatValue : valsClone) {
		// if(skip<start){
		// skip++;
		// continue;
		// }
		// floatValue=(delta*floatValue)+min; //floatValue == 1?max:min;
		// //fix that polygone come to singnal y point
		// if(i+1 == valsClone.size() || i == 0 || i+1>=length){
		// floatValue = min;
		// }
		// floatValue = (floatValue-min)/delta;
		// floatValue += getOrder();
		// temp[i] = (int)(floatValue/scalar);
		// i++;
		// if(i>=length){
		// break;
		// }
		// }
//		return temp;
//	}

	private int[] toCoordinatesValues(FrameValues vals, float scalar, float coef) {
		FrameValues valsClone = vals;
		Float min = ctx.getValues().getMinValue();
//		Float max = ctx.getValues().getMaxValue();
		Float delta = ctx.getValues().getDeltaValue();
		
		if (vals == null || vals.size() == 0) {
			return null;
		}
		// synchronized (vals) {
		// valsClone = new FrameValues(vals);
		// }
		int start = 0;
		int end = ctx.getValues().size();
		if (domain != null && domain.getFrom() != null) {
			start = ctx.getValues().toIndex(domain.getFrom().floatValue());
			end = ctx.getValues().toIndex(domain.getUntil().floatValue());
			end = Math.min(end, vals.size());
		}
		int length = end - start;
		int[] temp = new int[length];

		int i = 0, skip = 0;
		
		
		for (Float floatValue : valsClone) {
			if (skip < start) {
				skip++;
				continue;
			}
			floatValue *= coef;
			if (i + 1 == valsClone.size() || i == 0 || i >= length) {
				floatValue = min;
			}
			floatValue = (floatValue - min) / delta;
			floatValue += getOrder();
			temp[i] = (int) (floatValue / scalar);
			i++;
			if (i >= length) {
				break;
			}
		}
		return temp;
	}

	public float getOrder() {
		return getCtx().getOrder();
	}

	public void setOrder(float order) {
		getCtx().setOrder(order);
//		for (Float f1 : getCtx().getValues()) {
//			minmax(f1);
//		}
		// coordinateBoundary = getCoordinateBoundary(getCtx().getValues());
	}

	protected ClassifierChartContext getCtx() {
		return ctx;
	}

	public String getValueOn(BigDecimal x) {
		int index = getCtx().getValues().toIndex(x.floatValue());
		index++;
		Float value = null;
		if (index < getCtx().getValues().size() && index>0) {
			value = getCtx().getValues().get(index);
		}
		Float thresholdValue = null;
		if (index < getCtx().getThreshold().size() && index>0) {
			thresholdValue = getCtx().getThreshold().get(index);
		}
		String thresholdValueStr = thresholdValue == null ? "-"
				: thresholdValue.toString();
		String valueStr = MessageFormat.format(
				"{0,number} \n Threshold: {1} \n index: {2}", value,
				thresholdValueStr, index);

		return valueStr;
	}

}
