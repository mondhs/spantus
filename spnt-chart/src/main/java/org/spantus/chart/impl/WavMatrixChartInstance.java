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
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.Format;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphDomain;

import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.chart.util.ColorLookup;
import org.spantus.core.FrameVectorValues;
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
public class WavMatrixChartInstance extends TimeSeriesFunctionInstance {

	FrameVectorValues values;
	CoordinateBoundary coordinateBoundary;
	ColorLookup colorLookup = new ColorLookup();
	GraphDomain domain;

	float order = 0f;

	VectorSeriesColorEnum colorType = VectorSeriesColorEnum.blackWhite;

	
	// private LowerLimitInstance lowerLimit = null;
	// private UpperLimitInstance upperLimit = null;

	BigDecimal xScalar = new BigDecimal(1);
	BigDecimal yScalar = new BigDecimal(1);

	Logger log = Logger.getLogger(getClass());

	public WavMatrixChartInstance(String description, FrameVectorValues values) {
		this.description = description;
		this.values = values;
		// this.style = style;
		coordinateBoundary = getCoordinateBoundary(values);
		log.debug("name: " + description + "; order: " + getOrder() + "; sampleRate:" + values.getSampleRate()+ "; length: " + values.size());

	}

	public void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar) {
		this.xScalar = xScalar;
		this.yScalar = yScalar;
	}

	public void paintFunction(Graphics g) {
		
		int height = (int) (1  / ( yScalar.floatValue()  ));
		int startY = (int) ((1 + getOrder()) / (  yScalar.floatValue()  ));
		int width = (int) (values.toTime(values.size()) /  (xScalar.floatValue()));
		g.drawImage(getImage(values), 0, startY, width, -height, null);
	}

	BufferedImage image = null;

	private BufferedImage getImage(FrameVectorValues vals) {
		if (image != null){
			if(domain.getFrom() != null){
				int from = vals.toIndex(domain.getFrom().floatValue());
				int to = vals.toIndex(domain.getUntil().floatValue());
				to = Math.min(to, vals.toIndex(vals.size()));
				return image.getSubimage(from, 0, to-from, image.getHeight());
			}else{
				return image;
			}

		}
		minmax(vals);
		
		image = new BufferedImage(vals.size(), vals.get(0).size(),
				BufferedImage.TYPE_INT_RGB);
		int[] rgbArray = new int[vals.size() * vals.get(0).size()];
		int x = 0, y = 0;
		for (List<Float> fv : vals) {
			int delta = vals.size();
			y = 0;
			LinkedList<Float> lf = new LinkedList<Float>(fv);
			for (ListIterator<Float> iterator2 = lf.listIterator(fv.size()); iterator2.hasPrevious();) {
				Float f1 = (Float) iterator2.previous();
				rgbArray[x + (y * delta)] = lookupColor(f1);
				y++;
			}
			x++;
		}
		image.setRGB(0, 0, vals.size(), vals.get(0).size(), rgbArray, 0, vals
				.size());
		return image;
	}

	private CoordinateBoundary getCoordinateBoundary(FrameVectorValues values) {

		Float xMin = 0f, xMax = new Float(values.toTime(values.size())), yMin = getOrder(), 
		yMax = new Float(getOrder()+1);
		if(domain != null && domain.getUntil() != null){
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

		paintFunction(g2);
		// paintTerminators(g2);

		g2.dispose();

	}

	
	public void render(BigDecimal xScalar, BigDecimal yScalar, Format yFormat,
			FontMetrics fontMetrics) {
		renderFunction(null, null, xScalar, yScalar);
	}

	Float min = Float.MAX_VALUE;
	Float max = Float.MIN_VALUE;
	private void minmax(FrameVectorValues values){
		for (List<Float> fv : values) {
			for (Float f1 : fv) {
				min = Math.min(min, f1);
				max = Math.max(max, f1);
			}
		}
	}


	public int lookupColor(float floatValue) {

		float delta = max - min;
		float fColor = ((float) (floatValue - min) / (delta));
		short s = (short)(256*fColor);
		Color clr = colorLookup.lookup(getColorType(), s);
		return clr.getRGB();
	}

	public GraphDomain getDomain() {
		return domain;
	}

	public void setDomain(GraphDomain domain) {
		this.domain = domain;
	}

	public float getOrder() {
		return order;
	}

	public void setOrder(float order) {
		this.order = order;
	}

	public VectorSeriesColorEnum getColorType() {
		return colorType;
	}

	public void setColorType(VectorSeriesColorEnum colorType) {
		this.colorType = colorType;
	}
}
