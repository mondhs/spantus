package org.spantus.utils;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created May 4, 2010
 *
 */
public abstract class CollectionUtils {
	/**
	 * 
	 * @param strings
	 * @return
	 */
	public static <T> List<T> toList(T... objs){
		List<T> objList = new ArrayList<T>();
		for (T obj : objs) {
			objList.add(obj);
		}
		return objList;
	}
        /**
         * 
         * @param values
         * @return
         */
        public static Double[] toDoubleArray(List<Float> values){
            Double[] doubles = new Double[values.size()];
            int i = 0;
            for (Float float1 : values) {
                doubles[i++]=float1.doubleValue();
            }
            return doubles;
        }
}
