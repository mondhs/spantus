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
package org.spantus.math.windowing;

import java.util.List;
import java.util.ListIterator;

/**
 * http://www-users.cs.york.ac.uk/~fisher/mkfilter/trad.html
 * 
 * filtertype = Butterworth; passtype = Highpass; ripple = order = 2 samplerate =
 * 11000 corner1 = 125 corner2 = adzero = logmin =
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.3
 * 
 *        Created 2012.04.28
 * 
 */
public class ButterworthWindowing extends Windowing {
	private static final Double GAIN = 1.051784333e+00;
	double[] xv = new double[3];
	double[] yv = new double[3];

	public void apply(List<Double> values) {
		ListIterator<Double> iter = values.listIterator();

		while (iter.hasNext()) {
			Double nextInputValue = (Double) iter.next();
			xv[0] = xv[1];
			xv[1] = xv[2];
			xv[2] = nextInputValue / GAIN;
			yv[0] = yv[1];
			yv[1] = yv[2];
			yv[2] = (xv[0] + xv[2]) - 2 * xv[1] + (-0.9039560414 * yv[0])
					+ (1.8991049796 * yv[1]);
			double nextOutputValue = yv[2];
			iter.set(nextOutputValue);

		}

	}

}
