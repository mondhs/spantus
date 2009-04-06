/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
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
package org.spantus.chart.marker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;

import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;
/**
 * 
 * @author Mindaugas Greibus
 *
 */
public class MarkerComponent extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Color MARK_COLOR = UIManager.getColor("textHighlight");
	private final Color MARK_TRANSPARENT = new Color(MARK_COLOR.getRGB() & 0x00FFFFFF | 0x77000000, true);
	private final Color MARK_SELECTED_TRANSPARENT = new Color(MARK_COLOR.getRGB() & 0x00FFFFA0 | 0x77000000, true);

	Integer startX = null;
	Integer endX = null;
	
	Marker marker;
	
	MarkerGraphCtx ctx;
	
	Logger log = Logger.getLogger(getClass());
	
	public MarkerComponent() {
//		addMouseListener(this);
//		addMouseMotionListener(this);
		setFocusable(true);
		setBorder(BorderFactory.createLineBorder(MARK_COLOR));
	}
	
	public void changeSize(Dimension size){
		Rectangle _r = new Rectangle();
		_r.x = Math.min(getStartX(), getEndX());
		_r.width = Math.max(getStartX(), getEndX()) - _r.x;
		
		_r.y += 3;
		_r.height = size.height - 9;
		Dimension _size = new Dimension(_r.width,_r.height);
		setLocation(_r.getLocation());
		setSize(_size);
//		log.debug(getName() + ":" + getCtx() + ":" + getMarker());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(MARK_COLOR.darker());
		Point textLocation = new Point();
		Rectangle2D textRectangle = g.getFontMetrics().getStringBounds(getName(), g); 
		textLocation.x = ((getSize().width-(int)textRectangle.getWidth())/2);;
		textLocation.y = (getSize().height + (int)textRectangle.getHeight())/2;
		
		if(isFocusOwner()){
			g.setFont (new Font ("Sanserif", Font.BOLD, 13));
		}else{
			g.setFont (new Font ("Sanserif", Font.PLAIN, 10));
		}
		g.drawString(getName(), textLocation.x, textLocation.y);
		

		if(isFocusOwner()){
			g.setColor(MARK_SELECTED_TRANSPARENT);
		}else{
			g.setColor(MARK_TRANSPARENT);
		}
		g.fillRect(0, 0, getSize().width, getSize().height);
		
//		g.fillRoundRect(0, 0, getSize().width, getSize().height,100,10);

	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	protected int getStartX(){
		if(this.startX == null){
			this.startX = MarkerComponentUtil.timeToScreen(getCtx(), getMarker().getStart());	
		}
		return this.startX;
	}

	protected void setStartX(int startX){
		this.startX = startX;
		Long start = MarkerComponentUtil.screenToTime(getCtx(), startX);
		getMarker().setStart(start);
	}

	public void resetScreenCoord(){
		this.startX = MarkerComponentUtil.timeToScreen(getCtx(), getMarker().getStart());	
		Long endXTime = getMarker().getStart()+getMarker().getLength();
		this.endX = MarkerComponentUtil.timeToScreen(getCtx(), endXTime);
	}
	
	protected int getEndX(){
		if(endX == null){
			Long endXTime = getMarker().getStart()+getMarker().getLength();
			this.endX = MarkerComponentUtil.timeToScreen(getCtx(), endXTime);
		}
		return this.endX;
	}
	
	protected void setEndX(int endX){
		this.endX = endX;
		Long end = MarkerComponentUtil.screenToTime(getCtx(), endX);
		Long length =  end-getMarker().getStart();
		getMarker().setLength(length);
	}
	
	@Override
	public String getName() {
		return getMarker().getLabel();
	}


	public MarkerGraphCtx getCtx() {
		return ctx;
	}

	public void setCtx(MarkerGraphCtx ctx) {
		this.ctx = ctx;
	}
}
