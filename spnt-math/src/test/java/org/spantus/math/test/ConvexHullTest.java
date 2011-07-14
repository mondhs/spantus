package org.spantus.math.test;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class ConvexHullTest {
	public static final Logger LOG = Logger.getLogger(ConvexHullTest.class);
	private List<Point2D.Double> signal;

	@Before
	public void setup() {
		signal = new ArrayList<Point2D.Double>();
//		for (double i = 1; i < 16 * Math.PI; i+=.3) {
//			signal.add(new Point2D.Double(i, (Math.sin(i))));
//		}
		signal.add(new Point2D.Double(10.0,10.0));
		signal.add(new Point2D.Double(10.0,1.0));
		signal.add(new Point2D.Double(1.0,10.0));
		signal.add(new Point2D.Double(1.0,1.0));
		signal.add(new Point2D.Double(5.0,5.0));
	}

	@Test
	public void testConvexHull(){
		Point2D.Double[] resut =  ConvexHull.bruteForceConvexHull(signal.toArray(new Double[signal.size()]) );
		for (Double double1 : resut) {
			LOG.debug(double1);
		}
		
	}
	
}
