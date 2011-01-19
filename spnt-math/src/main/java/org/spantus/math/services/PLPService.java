/*
 * Copyright (c) 2011 Mindaugas Greibus (spantus@gmail.com)
 * Part of program for analyze speech signal
 * http://spantus.sourceforge.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.spantus.math.services;

import java.util.List;
/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.3
 * 
 */
public interface PLPService {
	public List<Float> calculate(List<Float> x, float sampleRate);
    public List<Float> calculateFromSpectrum(List<Float> fft, float sampleRate);

}
