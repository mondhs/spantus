/*
  Copyright (c) 2004, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

import java.util.Comparator;

/**
  * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
  */
class SortByColumn
	implements Comparator<float[]>
{
	private final int index;
	
	public SortByColumn(int index) {
		this.index = index;
	}
	
	public int compare(float[] o1, float[] o2) {
		return Float.compare(
				 o1[index], 
				o2[index]);
	}
}