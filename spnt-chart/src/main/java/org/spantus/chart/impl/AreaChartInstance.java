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
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.IllegalPathStateException;
import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.quies.math.plot.ChartStyle;
import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphDomain;

import org.spantus.core.FrameVectorValues;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.04.11
 * 
 */
public class AreaChartInstance extends TimeSeriesFunctionInstance {

	private FrameVectorValues values;
//	CoordinateBoundary coordinateBoundary;
	private GraphDomain domain;

	private Double order;

	ChartStyle style;
	List<Point> minPoints = new ArrayList<Point>();
	LinkedList<Point> maxPoints = new LinkedList<Point>();

	
	private Double min = Double.MAX_VALUE;
	private Double max = -Double.MAX_VALUE;
	
	private Polygon polygon; 

	public AreaChartInstance(String description, FrameVectorValues values,
			ChartStyle style) {
		this.description = description;
		this.values = values;
		this.style = style;
		for (List<Double> frameValues : values) {
			if (frameValues.size() != 2) {
				throw new IllegalArgumentException(
						"Area values should be from 2 vectors");
			}
			min(frameValues.get(0));
			max(frameValues.get(1));
		}
		setOrder(0D);
		log
				.debug("name: " + description + "; order: " + getOrder()
						+ "; min=" + min + "; max: " + max + "; sampleRate:"
						+ values.getSampleRate() + "; length: "
						+ values.size());
	}

	public synchronized void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar) {
		if (minPoints.size() > 0)
			return;
		int j = 0;

		Double _min = Double.MAX_VALUE;
		Double _max = -Double.MAX_VALUE;
		FrameVectorValues clonedValues = new FrameVectorValues(values);

		for (List<Double> fv : clonedValues) {
			int x = toCoordinateTime(j, xScalar);
			Double yMin = fv.get(0);
			Point p = new Point(x, 0);
			p.y = toCoordinateValues(yMin, yScalar);
			_min = Math.min(_min, yMin);
			minPoints.add(p);

			p = new Point(x, 0);
			Double yMax = fv.get(1);
			p.y = toCoordinateValues(yMax, yScalar);
			_max = Math.max(_max, yMax);
			maxPoints.addFirst(p);
			j++;

		}
		this.min = _min;
		this.max = _max;
		
		polygon = new Polygon();
		
		for (Point p : minPoints) {
			if(p==null)continue;
			polygon.addPoint(p.x, p.y-1);
		}
		for (Point p : maxPoints) {
			if(p==null)continue;
			polygon.addPoint(p.x, p.y);
		}
	}

	public synchronized void paintFunction(Graphics g) {
//		log.debug("paint: " + description + "; size: " + values.size());
//		Integer fx = null, fy = null, lx = null, ly = null;
		try{
		//		log.debug("polygon size: " + minPoints.size());
			g.fillPolygon(polygon);
		}catch (IllegalPathStateException e) {
			log.info(e.getMessage());
		}
	}

	private CoordinateBoundary getCoordinateBoundary(FrameVectorValues values) {

		Double xMin = 0D;
		Double xMax = Double.valueOf(values.toTime(values.size()));
		Double yMin = getOrder();
		Double yMax = getOrder() + 1;
		if (domain != null && domain.getUntil() != null) {
			xMax = domain.getUntil().doubleValue();
			xMin = domain.getFrom().doubleValue();
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
		minPoints.clear();
		maxPoints.clear();
		renderFunction(null, null, xScalar, yScalar);
	}

	public GraphDomain getDomain() {
		return domain;
	}

	public void setDomain(GraphDomain domain) {
		this.domain = domain;
	}

	private int toCoordinateTime(int j, BigDecimal scalar) {
		int x = (int) (j * 1000 / (scalar.doubleValue()  * values.getSampleRate()));
		return x;
	}

	private int toCoordinateValues(Double value, BigDecimal scalar) {
		int x = 0;
		Double delta = (max - min);
		value = (value - min) / delta;
		value += getOrder();
		x = (int) (value / scalar.doubleValue());
		return x;
	}



	private void min(Double f1) {
		min = Math.min(min, f1);
	}

	private void max(Double f1) {
		max = Math.max(max, f1);
	}


	public Double getOrder() {
		return order;
	}

	public void setOrder(Double order) {
		this.order = order;

		for (List<Double> frameValues : values) {
			if (frameValues.size() != 2) {
				throw new IllegalArgumentException(
						"Area values should be from 2 vectors");
			}
			min(frameValues.get(0));
			max(frameValues.get(1));
		}

//		coordinateBoundary = getCoordinateBoundary(values);
	}
	
	public String getValueOn(BigDecimal x) {
		int index = values.toIndex(x.longValue());
		if(index> values.size()-1){
			index = values.size()-1;
		}
		List<Double> value = values.get(index);
		
		return value.toString();
	}

}
