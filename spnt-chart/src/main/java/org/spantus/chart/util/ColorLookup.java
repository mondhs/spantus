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
package org.spantus.chart.util;

import java.awt.Color;

import org.spantus.chart.bean.VectorSeriesColorEnum;

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
public class ColorLookup {

	// Color[] coFI = FireColor.getFireColorArray();
	// Color[] coFIi = new Color[256];
	Color[] coCO = new Color[256];
	Color[] coCOi = new Color[256];
	Color[] coRA = new Color[256];
	Color[] coRAi = new Color[256];
	Color[] coCG = new Color[256];
	Color[] coCGi = new Color[256];
	Color[] coSW = new Color[256];
	Color[] coSWi = new Color[256];

	public ColorLookup() {
		int r = 0, g = 0, b = 0;
		for (int i = 0; i < 256; i++) { // building Colorarrays
			// Generate Balck/White Colors
			coSWi[i] = new Color(i, i, i);
			coSW[i] = new Color(255 - i, 255 - i, 255 - i);
			// Generate rainbow Colors
			r = i;
			g = (255 - Math.abs(i - 120) * 4);
			if (g < 0)
				g = 0;
			b = (255 - Math.abs(i - 60) * 4);
			if (b < 0)
				b = 0;
			if (i > 210)
				b = (210 - i) * (-5);
			coRA[i] = new Color(r, g, b);
			coRAi[i] = new Color(255 - r, 255 - g, 255 - b);
			// generate normal Colors
			g = (i - 128) * 2;
			if (g < 0)
				g = 0;
			coCO[i] = new Color(r, g, b);
			coCOi[i] = new Color(255 - r, 255 - g, 255 - b);
			// generate fire Colors
			if (i <= 90) {
				r = 120 - (int) (120.0 * ((double) i / 90.0));
				g = 160 - (int) (160.0 * ((double) i / 90.0));
				b = 207;
			}
			if (i > 90)
				g = (int) (255.0 * ((double) (i - 90) / 165.0));
			if (i > 100 && i <= 150)
				r = (int) (255.0 * ((double) (i - 100) / 50.0));
			if (i > 150) {
				r = 255;
				b = 0;
			}
			if (i > 90 && i <= 150)
				r = (int) (255.0 * ((double) (i - 90) / 60.0));
			// coFIi[i] = new Color(255 - r, 255 - g, 255 - b);
			coCG[i] = new Color(i / 2 + 30, i, 20);
			coCGi[i] = new Color(i / 2, i / 2, i);
		}
	}

	public Color lookup(short s) {
		return lookup(VectorSeriesColorEnum.normal, s);
	}

	public Color lookup(VectorSeriesColorEnum type, short s) {
		if (s < 0)
			s = 0;
		if (s > 255)
			s = 255;
		switch (type) {

		case blackWhite:
			return coSW[s];
		case blackWhiteInvert:
			return coSWi[s];
		case rainbow:
			return coRA[s];
		case rainbowInvert:
			return coRAi[s];
		case fire:
			return coCG[s];
		case fireInvert:
			return coCGi[s];
		case normal:
			return coCO[s];
		case normalInvert:
			return coCOi[s];
			//Normal
		default:
			throw new RuntimeException("Not impl: " + type);
		}
	}

}
