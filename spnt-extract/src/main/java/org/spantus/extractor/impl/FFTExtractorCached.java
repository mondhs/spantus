/*
 * Copyright (c) 2010 Mindaugas Greibus (spantus@gmail.com)
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

package org.spantus.extractor.impl;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: mondhs
 * Date: 10.12.12
 * Time: 17.06
 */
public class FFTExtractorCached extends FFTExtractor {
    private static LinkedHashMap<Long, FrameVectorValues> cache = new LinkedHashMap<Long, FrameVectorValues>();

    public FrameVectorValues calculateWindow(FrameValues window) {
        if (cache.get(window.getFrameIndex()) == null) {
            FrameVectorValues calculated = super.calculateWindow(window);
            cache.put(window.getFrameIndex(), calculated);
            return calculated;
        } else {
            return cache.get(window.getFrameIndex());
        }

    }

    public void flush() {
        cache.clear();
    }

}