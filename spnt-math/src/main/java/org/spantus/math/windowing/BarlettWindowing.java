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

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.02.29
 *
 */
public class BarlettWindowing extends Windowing {

	
	public void apply(List<Float> values) {
		int nSamples = values.size();
		int i, j;
		for (j = 0 - nSamples / 2; j < nSamples / 2; j++) {
			i = j + nSamples / 2;
			if (j >= 0 && j <= nSamples / 2) {
				values.set(i, values.get(i) * (1.0f - 2.0f * j / nSamples));
			} else if (j >= 0 - nSamples / 2 && j < 0) {
				values.set(i, values.get(i) * (1.0f + 2.0f * j / nSamples));
			} else
				values.set(i, values.get(i) * 0f);
		}

	}

}
