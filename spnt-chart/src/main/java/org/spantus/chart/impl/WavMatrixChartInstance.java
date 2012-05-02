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
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.Format;
import java.text.MessageFormat;
import java.util.List;

import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphDomain;

import org.spantus.core.FrameVectorValues;
import org.spantus.logger.Logger;
import org.spantus.ui.chart.ChartUtils.ChartScale;
import org.spantus.ui.chart.ChartUtils;
import org.spantus.ui.chart.ColorLookup;
import org.spantus.ui.chart.VectorSeriesColorEnum;
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

	
	private FrameVectorValues values;
	private ColorLookup colorLookup = null;
	private ChartScale chartScale = ChartScale.sqrt;
	private GraphDomain domain;
	private BufferedImage image = null;
	private Double order = 0D;
	

	private VectorSeriesColorEnum colorType = VectorSeriesColorEnum.blackWhite;

	

	BigDecimal xScalar = new BigDecimal(1);
	BigDecimal yScalar = new BigDecimal(1);

	Logger log = Logger.getLogger(getClass());

	public WavMatrixChartInstance(String description, FrameVectorValues values) {
		this.description = description;
		this.values = values;
		this.colorLookup = new ColorLookup(colorType);
		log.debug("name: " + description + "; order: " + getOrder() + "; sampleRate:" + values.getSampleRate()+ "; length: " + values.size());

	}

	public void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar) {
		this.xScalar = xScalar;
		this.yScalar = yScalar;
	}

	public void paintFunction(Graphics g) {
		
		int height = (int) (1  / ( yScalar.doubleValue()  ));
		int startY = (int) ((1 + getOrder()) / (  yScalar.doubleValue()  ));
		int width = (int) (values.toTime(values.size()) /  (xScalar.doubleValue()));
		g.drawImage(getImage(values), 0, startY, width, -height, null);
	}

	

	private BufferedImage getImage(FrameVectorValues vals) {
		if (image != null){
			if(domain.getFrom() != null){
				int from = vals.toIndex(domain.getFrom().doubleValue());
				int to = vals.toIndex(domain.getUntil().doubleValue());
				to = Math.min(to, vals.toIndex((double)vals.size()));
				return image.getSubimage(from, 0, to-from, image.getHeight());
			}else{
				return image;
			}

		}
		image = ChartUtils.createImage(vals, getChartScale(), colorLookup);
		return image;
	}
	


	private CoordinateBoundary getCoordinateBoundary(FrameVectorValues values) {

		Double xMin = 0D, xMax = new Double(values.toTime(values.size())), yMin = getOrder(), 
		yMax = new Double(getOrder()+1);
		if(domain != null && domain.getUntil() != null){
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

		paintFunction(g2);
		// paintTerminators(g2);

		g2.dispose();

	}

	
	public void render(BigDecimal xScalar, BigDecimal yScalar, Format yFormat,
			FontMetrics fontMetrics) {
		renderFunction(null, null, xScalar, yScalar);
	}




	public GraphDomain getDomain() {
		return domain;
	}

	public void setDomain(GraphDomain domain) {
		this.domain = domain;
	}

	public Double getOrder() {
		return order;
	}

	public void setOrder(Double order) {
		this.order = order;
	}

	public VectorSeriesColorEnum getColorType() {
		return colorType;
	}

	public void setColorType(VectorSeriesColorEnum colorType) {
		this.colorLookup = new ColorLookup(colorType);
		this.colorType = colorType;
	}
	public String getValueOn(BigDecimal x) {
		int index = values.toIndex(x.doubleValue());
		if(index> values.size()-1){
			index = values.size()-1;
		}
		List<Double> value = values.get(index);
		StringBuilder sb = new StringBuilder();
                sb.append("[");
                String separator = "";
                int i=0;
                for (Double float1 : value) {
                    sb.append(separator);
                    sb.append(MessageFormat.format("{0,number,0.000}", float1));
                    separator = ";";
                    if(i==12){
                        sb.append("\n");
                        i=0;
                    }
                    i++;
                }
                if(sb.length()>50){
                	sb.delete(50, sb.length());
                	sb.append("...");
                }
                sb.append("]");
                
		return sb.toString();
	}

	public ChartScale getChartScale() {
		return chartScale;
	}

	public void setChartScale(ChartScale chartScale) {
		this.chartScale = chartScale;
	}

}
