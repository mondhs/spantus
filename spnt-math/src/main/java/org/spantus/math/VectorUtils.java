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
package org.spantus.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.08.13
 * 
 * Some utils method for vectors 
 * 
 */
public class VectorUtils {

    /** 
     * sum vector values
     * @param vector
     * @return
     */
    public static Float sum(List<Float> vector) {
        float sum = 0f;
        for (Float float1 : vector) {
            sum += float1;
        }
        return sum;
    }

    /**
     * 
     * @param vector
     * @return
     */
    public static Float avg(List<Float> vector) {
        return sum(vector) / vector.size();
    }

    public static Float min(List<Float> vector) {
        return Collections.min(vector);
    }

    public static Integer minArg(Float... args) {
        Integer minIndex = null;
        Float minValue = Float.MAX_VALUE;
        int i = 0;
        for (Float float1 : args) {
            if (minValue > float1) {
                minValue = float1;
                minIndex = i;
            }
            i++;
        }
        return minIndex;
    }

    public static double[] toDoubleArray(List<Float> values) {
        double[] doubles = new double[values.size()];
        int i = 0;
        for (Float float1 : values) {
            doubles[i++] = float1.doubleValue();
        }
        return doubles;
    }

    public static double[] toDoubleArray(Float[] values) {
        double[] doubles = new double[values.length];
        int i = 0;
        for (Float float1 : values) {
            doubles[i++] = float1.doubleValue();
        }
        return doubles;
    }

    public static List<Float> toFloatList(double[] values) {
        List<Float> floatList = new ArrayList<Float>(values.length);
        int i = 0;
        for (Double d1 : values) {
            floatList.add(d1.floatValue());
        }
        return floatList;
    }
}
