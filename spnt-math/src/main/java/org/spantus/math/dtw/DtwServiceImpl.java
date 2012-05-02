package org.spantus.math.dtw;

import java.util.List;

import org.spantus.math.SpntDTW;
import org.spantus.math.dtw.DtwInfo.DtwType;

public class DtwServiceImpl implements DtwService {

    public Double calculateDistanceVector(List<List<Double>> targetMatrix,
            List<List<Double>> sampleMatrix) {
        DtwInfo info = SpntDTW.createDtwVectorInfo(targetMatrix, sampleMatrix);
        info.setType(DtwType.typeIII);
        info.resetIterationCount();
        return calculateDistance(info);
    }

    public Double calculateDistance(List<Double> targetVector,
            List<Double> sampleVector) {
        DtwInfo info = SpntDTW.createDtwInfo(targetVector, sampleVector);
        info.setType(DtwType.typeIII);
        info.resetIterationCount();
        return calculateDistance(info);
    }

    public Double calculateDistance(DtwInfo info) {
        return SpntDTW.dtwRecusion(info).getResult();
    }

    public DtwResult calculateInfoVector(List<List<Double>> targetMatrix,
            List<List<Double>> sampleMatrix) {
        DtwInfo info = SpntDTW.createDtwVectorInfo(targetMatrix, sampleMatrix);
        info.setType(DtwType.typeIII);
        info.resetIterationCount();
        return SpntDTW.dtwRecusion(info);
    }

    public DtwResult calculateInfo(List<Double> targetVector, List<Double> sampleVector) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
