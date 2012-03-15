package org.spantus.math.cluser;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.MinkowskiDistance;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class VectorQuantisationTest {
	DefaultDataset dataSet;

	@Before
	public void setup() {
		DefaultDataset dataSet = new DefaultDataset();
		Instance tmpInstance = new SparseInstance(3);
		tmpInstance.put(0, 1D);
		tmpInstance.put(1, 1D);
		tmpInstance.put(2, 1D);
		tmpInstance.setClassValue(0D);
		dataSet.add(tmpInstance);

	}

	@Test @Ignore
	public void testCluster() {
		Clusterer kmCluser = new KMeans(3, 100, new MinkowskiDistance());
		Dataset[] clusters = kmCluser.cluster(dataSet);

	}
}
