package org.spantus.math.test;

import java.util.List;

import junit.framework.TestCase;

import org.spantus.math.windowing.HammingWindowing;

public class WindowingTest extends TestCase {
	public void testHamming(){
		HammingWindowing window = new HammingWindowing();
		List<Float> windResult = window.calculate(4);
		assertEquals(4, windResult.size());
		assertEquals(11, window.calculate(11).size());
		
	}
}
