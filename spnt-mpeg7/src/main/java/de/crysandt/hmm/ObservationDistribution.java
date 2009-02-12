/*
  Copyright (c) 2002-2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.hmm;

/**
 * @author <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 */
public interface ObservationDistribution {
	public int getLength();
	public double getProb(float[] vector);
	public double getLogProb(float[] vector);
}
