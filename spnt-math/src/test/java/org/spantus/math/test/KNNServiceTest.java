package org.spantus.math.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spantus.math.knn.KNNServiceImpl;

import junit.framework.TestCase;

public class KNNServiceTest extends TestCase {

	KNNServiceImpl knnService;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		knnService = new KNNServiceImpl();
	}
	
	public void testClusterisation() throws Exception {
		List<List<Float>> vectors = new ArrayList<List<Float>>();
		vectors.addAll(createVectorList(10));//5
		vectors.addAll(createVectorList(100));//50
		Collections.shuffle(vectors);
		List<List<Float>> clusterCenters = knnService.cluster(vectors, 2);
		assertEquals(2, clusterCenters.size());
	}

	public List<List<Float>> createVectorList(int centerValue) {
		List<List<Float>> vectors = new ArrayList<List<Float>>();
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 2; i++) {
				List<Float> vector = new ArrayList<Float>();
				vector.add((float) Math.random() * centerValue);
			}
		}
		return vectors;
	}
}
