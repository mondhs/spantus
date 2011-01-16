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
package org.spantus.chart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.math.BigDecimal;

import net.quies.math.plot.XAxis;
import net.quies.math.plot.XAxisInstance;


/**
 @author Mindaugas Greibus
 */
class XAxisGridInstance extends XAxisInstance {

XAxisGridInstance(XAxis parent, BigDecimal min, BigDecimal max, int length) {
	super(parent, min, max, length);
}

@Override
public void paint(Graphics g) {
	super.paint(g);
	Graphics2D g2 = (Graphics2D) g.create();
	g2.setPaint(Color.lightGray);
	Float prevPoint = 0f;
	for (Shape shape : getNailRender()) {
		Line2D.Float line = (Line2D.Float)((Line2D.Float)shape).clone();
		line.y1 = 10;
        line.y2 = (float) -g2.getDeviceConfiguration().getBounds().getHeight();
//		line.y2 = new Float(g2.getClip().getBounds2D().getMinY())+10;
		g2.draw(line);
		Line2D.Float linePrev = (Line2D.Float)line.clone();
		linePrev.x1=(line.x1+prevPoint)/2;
		linePrev.x2=linePrev.x1;
		prevPoint = line.x1;
		g2.draw(linePrev);
		
	}
	
}

}
