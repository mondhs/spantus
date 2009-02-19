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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.LinkedList;

import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphDomain;

import org.spantus.chart.bean.ThresholdChartContext;
import org.spantus.core.FrameValues;
import org.spantus.logger.Logger;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.02
 *
 */
public class ThresholdChartInstance extends TimeSeriesFunctionInstance{

	CoordinateBoundary coordinateBoundary;
	
	GraphDomain domain;

	ThresholdChartContext ctx;
	
	private final ArrayList<int[]> polylinesX = new ArrayList<int[]>();
	private final ArrayList<int[]> polylinesYt = new ArrayList<int[]>();
	private final ArrayList<int[]> polylinesYstate = new ArrayList<int[]>();
	private final ArrayList<int[]> polylinesY = new ArrayList<int[]>();

	Logger log = Logger.getLogger(this.getClass());

	public ThresholdChartInstance(ThresholdChartContext ctx) {
		this.ctx = ctx;
		this.description = ctx.getDescription();
		for (Float float1 : ctx.getValues()) {
			minmax(float1);
		}
		setOrder(0);
		log.debug("name: " + description + "; order: " + getOrder() + "; min=" +min + "; max: " + max +
				"; sampleRate:" + ctx.getValues().getSampleRate() + "; length: " + ctx.getValues().size());
	}

	public void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar) {
//
		if(polylinesX.size() > 0) return;
		LinkedList<Float> list = null;
		synchronized (getCtx().getValues()) {
			list = new LinkedList<Float>(getCtx().getValues());
		}
		Float _min = Float.MAX_VALUE;
		Float _max = Float.MIN_VALUE;

		for (Float float1 : list) {
			_min = Math.min(_min, float1);
			_max = Math.max(_max, float1);
		}
		this.min = _min;
		this.max = _max;
		
		polylinesX.add(toCoordinatesTime(getCtx().getValues(), xScalar.floatValue()));
		polylinesYt.add(toCoordinatesValues(getCtx().getThreshold(), yScalar.floatValue()));
		polylinesYstate.add(toCoordinatesSates(getCtx().getState(), yScalar.floatValue()));
		polylinesY.add(toCoordinatesValues(getCtx().getValues(), yScalar.floatValue()));

	}

	public void paintFunction(Graphics g) {
//		log.severe("paint: " + description);
//		int i = polylinesX.size();
//		while (--i >= 0) {
		for (int i = 0; i < polylinesY.size(); i++) {
			int[] x = polylinesX.get(i);
			int[] y = polylinesY.get(i);
			int[] yt = polylinesYt.get(i);
			int[] yState = polylinesYstate.get(i);
			
			g.drawPolyline(x, y, x.length);
			Color currentColor = ((Color)getCtx().getStyle().getPaint());
			((Graphics2D)g).setPaint(currentColor.darker().darker());
			g.drawPolyline(x, yt, x.length);
			Color currentColorTransparent = new Color(currentColor.getRGB() & 0x00FFFFFF | 0x33000000, true);
			((Graphics2D)g).setPaint(currentColorTransparent);
			g.fillPolygon(x, yState, x.length);

		}
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
		// paintTerminators(g2);

		g2.dispose();

	}

	
	public void render(BigDecimal xScalar, BigDecimal yScalar, Format yFormat,
			FontMetrics fontMetrics) {
		polylinesX.clear();
		polylinesYt.clear();
		polylinesYstate.clear();
		polylinesY.clear();
		renderFunction(null, null, xScalar, yScalar);
	}

	public GraphDomain getDomain() {
		return domain;
	}

	public void setDomain(GraphDomain domain) {
		this.domain = domain;
	}

	private int[] toCoordinatesTime(FrameValues vals,
			float scalar) {
		int[] temp = new int[vals.size()];
		for (int j = 0; j < vals.size(); j++) {
			temp[j] = (int)(j / (scalar*vals.getSampleRate())) ;			
		}
		return temp;
	}
	
	Float min = Float.MAX_VALUE;
	Float max = Float.MIN_VALUE;
	private void minmax(Float f1){
		min = Math.min(min, f1);
		max = Math.max(max, f1);
	}

	private int[] toCoordinatesValues(FrameValues vals,
			float scalar) {
		return toCoordinatesValues(vals, scalar, 1f);
	}
	
	private int[] toCoordinatesSates(FrameValues vals,
			float scalar) {
		FrameValues valsClone = null;
		synchronized (vals) {
			valsClone = new FrameValues(vals);
		}
		int[] temp = new int[valsClone.size()];
		int i=0; 
		float delta =  max-min;
		for (Float floatValue : valsClone) {
			floatValue=floatValue == 1?max:min;
			if(i+1 == valsClone.size() || i == 0){
				floatValue = min;
			}
			floatValue = (floatValue-min)/delta;
			floatValue += getOrder();
			temp[i] = (int)(floatValue/scalar);
			i++;
		}
		return temp;
	}

	
	private int[] toCoordinatesValues(FrameValues vals,
			float scalar, float coef) {
		FrameValues valsClone = null;
		synchronized (vals) {
			valsClone = new FrameValues(vals);
		}
		int[] temp = new int[valsClone.size()];
		int i=0; 
		float delta =  max-min;
		for (Float floatValue : valsClone) {
			floatValue*=coef;
			if(i+1 == valsClone.size() || i == 0){
				floatValue = min;
			}
			floatValue = (floatValue-min)/delta;
			floatValue += getOrder();
			temp[i] = (int)(floatValue/scalar);
			i++;
		}
		return temp;
	}

	public float getOrder() {
		return getCtx().getOrder();
	}

	public void setOrder(float order) {
		getCtx().setOrder(order);
		for (Float f1 : getCtx().getValues()) {
			minmax(f1);
		}
		coordinateBoundary = getCoordinateBoundary(getCtx().getValues());
	}
	protected ThresholdChartContext getCtx() {
		return ctx;
	}

}