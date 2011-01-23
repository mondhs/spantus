/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.math.cluster;

import java.util.List;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

/**
 *
 * @author mondhs
 */
public class ClusterServiceJavaMLImpl implements ClusterService {

    public ClusterCollection cluster(List<List<Float>> vectors, int clusterSize) {
        Dataset data = new DefaultDataset();
        for (List<Float> floats : vectors) {
            Instance tmpInstance = new SparseInstance(floats.size());
            int i = 0;
            for (Float f1 : floats) {
                tmpInstance.put(i++, f1.doubleValue());
            }
            data.add(tmpInstance);
        }

//        Clusterer km = new KMeans(clusterSize);
//        Dataset[] clusters = km.cluster(data);
//        ClusterCollection cc = new ClusterCollection();
        return null;
    }
}
