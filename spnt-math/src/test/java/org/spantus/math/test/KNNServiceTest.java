package org.spantus.math.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.spantus.math.knn.KNNService;
import org.spantus.math.knn.KNNServiceImpl;

public class KNNServiceTest extends TestCase {

	KNNService knnService;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		knnService = new KNNServiceImpl();
	}
	
	public void testClusterisation() throws Exception {
		List<List<Float>> vectors = createVectorList();
//		new ArrayList<List<Float>>();
//		vectors.addAll(createVectorList(5, 5));//5
//		vectors.addAll(createVectorList(5, 20 ));//50
		Collections.shuffle(vectors);
		List<List<Float>> clusterCenters = knnService.cluster(vectors, 2);
		assertEquals(2, clusterCenters.size());
		assertAproxEquals(5.0, clusterCenters.get(0).get(0), 5.0);
		assertAproxEquals(150.0, clusterCenters.get(1).get(0), 70.0);
		
	}

	public List<List<Float>> createVectorList(int mean, int shift) {
		List<List<Float>> vectors = new ArrayList<List<Float>>();
		for (int j = 0; j < 5; j++) {
			List<Float> vector = new ArrayList<Float>();
			for (int i = 0; i < 2; i++) {
				vector.add( ((float) Math.random() * mean*2) + shift);
			}
			vectors.add(vector);
		}
		return vectors;
	}
	public List<List<Float>> createVectorList() {
		List<List<Float>> vectors = new ArrayList<List<Float>>();
		vectors.add(createVector(5, 5));
		vectors.add(createVector(5.1, 5));
		vectors.add(createVector(5, 5.1));
		vectors.add(createVector(4.9, 5));
		vectors.add(createVector(5, 4.9));
		vectors.add(createVector(151, 150));
		vectors.add(createVector(150, 151));
		vectors.add(createVector(150, 150));
		vectors.add(createVector(149, 150));
		vectors.add(createVector(150, 149));
		return vectors;
	}
	public List<Float> createVector(Number... numbers) {
		List<Float> floats = new ArrayList<Float>();
		for (Number number : numbers) {
			floats.add(number.floatValue());
		}
		return floats;
	}
	
	public void assertAproxEquals(Double  expected, Float actual, Double precission){
		Double delta = Math.abs(expected-actual);
		assertTrue(MessageFormat.format("expected {0} with precission {1}, but got {2}", expected, precission, actual), delta<precission);
	}
	
}
