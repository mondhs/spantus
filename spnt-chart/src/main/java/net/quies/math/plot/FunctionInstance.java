package net.quies.math.plot;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.text.Format;

/*
 Copyright (c) 2005, 2006 Pascal S. de Kloe. All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products derived
 from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 OF SUCH DAMAGE.
 */

/**
 * @author Pascal S. de Kloe
 * @see Function#getInstance(GraphDomain, ChartStyle)
 */
public interface FunctionInstance {
	/**
	 * @param xCoordinate
	 *            one or more arguments in ascending order.
	 * @param yCoordinate
	 *            the corresponding values.
	 * @param xScalar
	 *            transforms graphical horizontal positions to x-coordinates.
	 * @param yScalar
	 *            transforms graphical vertical positions to y-coordinates.
	 */
	public void renderFunction(BigDecimal[] xCoordinate,
			BigDecimal[] yCoordinate, BigDecimal xScalar, BigDecimal yScalar);

	public void paintFunction(Graphics g);

	public BigDecimal[] getXCoordinates();

	public BigDecimal[] getYCoordinates();
	
	public void	paint(Graphics g);
	
	public CoordinateBoundary getCoordinateBoundary();
	
	public void render(BigDecimal xScalar, BigDecimal yScalar, Format yFormat, FontMetrics fontMetrics);



}
