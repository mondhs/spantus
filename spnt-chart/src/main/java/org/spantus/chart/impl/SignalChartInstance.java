/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */

package org.spantus.chart.impl;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.math.BigDecimal;
import java.text.Format;
import java.text.MessageFormat;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphDomain;

import org.spantus.core.FrameValues;
import org.spantus.logger.Logger;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.11
 *
 */
public class SignalChartInstance extends TimeSeriesFunctionInstance{

	FrameValues values;
	CoordinateBoundary coordinateBoundary;
	GraphDomain domain;

	
	float order;
	
	ChartStyle style;

//	private final ArrayList<int[]> polylinesX = new ArrayList<int[]>();
//	private final ArrayList<int[]> polylinesY = new ArrayList<int[]>();
	private Polygon signalPolygon; 
	BigDecimal xScalar = new BigDecimal(1);
	BigDecimal yScalar = new BigDecimal(1);
	
	Logger log = Logger.getLogger(this.getClass());

	public SignalChartInstance(String description, FrameValues values, ChartStyle style) {
		this.description = description;
		this.values = values;
		this.style = style;
		Double min = values.getMinValue();
		Double max = values.getMaxValue();
		
		
//		for (Float float1 : values) {
//			minmax(float1);
//		}
		setOrder(0);
		log.debug("name: " + description + "; order: " + getOrder() + "; min=" +min + "; max: " + max +
				"; sampleRate:" + values.getSampleRate() + "; length: " + values.size());
	}

	public void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar) {
//
//		if(polylinesX.size() > 0) return;

//		int[] x = toCoordinatesTime(values.size(), xScalar.floatValue());
//		int[] y = toCoordinatesValues(values, yScalar.floatValue());
		
//		polylinesX.add(x);
//		polylinesY.add(y);
//		if(signalPolygon==null){
			signalPolygon =toPolygon(values, xScalar.floatValue(), yScalar.floatValue());
//		}
		this.xScalar = xScalar;
		this.yScalar = yScalar;
	}

	public void paintFunction(Graphics g) {
//		long time = System.currentTimeMillis();
//		log.debug("paint: " + description);
//		int i = polylinesX.size();
//		while (--i >= 0) {
//		for (int i = 0; i < polylinesX.size(); i++) {
//			int[] x = polylinesX.get(i);
//			int[] y = polylinesY.get(i);
//			g.drawPolygon(x, y, x.length);
//		}
		if(signalPolygon != null){
			g.drawPolygon(signalPolygon);
		}
		
//		log.debug("[paintFunction] time: " + (System.currentTimeMillis()-time));
	}
	
	private CoordinateBoundary getCoordinateBoundary(FrameValues values) {

		Float xMin = 0f, xMax = new Float(values.toTime(values.size())), yMin = 0f+getOrder(), yMax = 1f+getOrder();
		if (domain != null && domain.getUntil() != null) {
			xMax = domain.getUntil().floatValue();
			xMin = domain.getFrom().floatValue();
		}

		return new CoordinateBoundary(new BigDecimal(xMin),
				new BigDecimal(xMax), new BigDecimal(yMin),
				new BigDecimal(yMax));

	}

	
	public CoordinateBoundary getCoordinateBoundary() {
		return getCoordinateBoundary(this.values);
	}

	
	public BigDecimal[] getXCoordinates() {
		return null;
	}

	
	public BigDecimal[] getYCoordinates() {
		return null;
	}

	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setPaint(style.getPaint());
		paintFunction(g2);
		// paintTerminators(g2);

		g2.dispose();

	}

	
	public void render(BigDecimal xScalar, BigDecimal yScalar, Format yFormat,
			FontMetrics fontMetrics) {
//		polylinesX.clear();
//		polylinesY.clear();
		renderFunction(null, null, xScalar, yScalar);
	}

	public GraphDomain getDomain() {
		return domain;
	}

	public void setDomain(GraphDomain domain) {
		this.domain = domain;
	}

//	private int[] toCoordinatesTime(int size,
//			float scalar) {
//		int[] temp = new int[size];
//		for (int j = 0; j < size; j++) {
//			temp[j] = (int)(j / (scalar*values.getSampleRate())) ;			
//		}
//		return temp;
//	}
	
	private Polygon toPolygon(FrameValues vals,
			float xScalar, float yScalar) {
		Polygon polygon = new Polygon();
//		int[] temp = new int[size];
//		for (int j = 0; j < size; j++) {
//			temp[j] = (int)(j / (scalar*values.getSampleRate())) ;			
//		}
//		temp[i] = (int)(floatValue/scalar);
		
		Double delta =  vals.getDeltaValue();
		int i=0; 
		Integer previousIndex = null;

		Integer index = (int)(i / (xScalar/1000*values.getSampleRate()));
//		Integer minValue = (int)(vals.getMinValue()/yScalar);
//		polygon.addPoint(index, minValue);

		
		for (Double floatValue : vals) {
			
			index = (int)(i / (xScalar/1000*values.getSampleRate()));
			
			if(previousIndex == null){
				previousIndex = index;
				floatValue = (floatValue-vals.getMinValue())/delta;
				floatValue += getOrder();
				Integer value = (int)(floatValue/yScalar);
				polygon.addPoint(index, value);
				i++;
				continue;
			}
			
			if(index == previousIndex){
				i++;
				continue;
			}
			previousIndex = index;
			floatValue = (floatValue-vals.getMinValue())/delta;
			floatValue += getOrder();
			Integer value = (int)(floatValue/yScalar);
			polygon.addPoint(index, value);
			i++;
		}
//		polygon.addPoint(index, minValue);

		return polygon;
	}
	
//	Float min = Float.MAX_VALUE;
//	Float max = Float.MIN_VALUE;
//	private void minmax(Float f1){
//		min = Math.min(min, f1);
//		max = Math.max(max, f1);
//	}
	
//	private int[] toCoordinatesValues(FrameValues vals,
//			float scalar) {
//		int[] temp = new int[vals.size()];
//		int i=0; 
//		float delta =  vals.getDeltaValue();
//		for (Float floatValue : vals) {
//			floatValue = (floatValue-vals.getMinValue())/delta;
//			floatValue += getOrder();
//			temp[i] = (int)(floatValue/scalar);
//			i++;
//		}
//		return temp;
//	}

	public float getOrder() {
		return order;
	}

	public void setOrder(float order) {
		this.order = order;
//		for (Float f1 : values) {
//			minmax(f1);
//		}
		coordinateBoundary = getCoordinateBoundary(values);
	}
	
	public String getValueOn(BigDecimal x) {
		int index = values.toIndex(x.longValue());
		index += 1;
		Double value = null;
		if(index > 0 && index<values.size()){
			value = values.get(index);
		}
		String valueStr = MessageFormat.format(
				"{0,number} \n index: {1}", value,
				 index);
		return valueStr;
	}

}
