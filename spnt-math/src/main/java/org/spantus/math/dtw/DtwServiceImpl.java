package org.spantus.math.dtw;

import java.util.List;

import org.spantus.math.DTW;
import org.spantus.math.dtw.DtwInfo.DtwType;

public class DtwServiceImpl implements DtwService {

    public Float calculateDistanceVector(List<List<Float>> targetMatrix,
            List<List<Float>> sampleMatrix) {
        DtwInfo info = DTW.createDtwVectorInfo(targetMatrix, sampleMatrix);
        info.setType(DtwType.typeIII);
        info.resetIterationCount();
        return calculateDistance(info);
    }

    public Float calculateDistance(List<Float> targetVector,
            List<Float> sampleVector) {
        DtwInfo info = DTW.createDtwInfo(targetVector, sampleVector);
        info.setType(DtwType.typeIII);
        info.resetIterationCount();
        return calculateDistance(info);
    }

    public Float calculateDistance(DtwInfo info) {
        return DTW.dtwRecusion(info).getResult();
    }

    public DtwResult calculateInfoVector(List<List<Float>> targetMatrix,
            List<List<Float>> sampleMatrix) {
        DtwInfo info = DTW.createDtwVectorInfo(targetMatrix, sampleMatrix);
        info.setType(DtwType.typeIII);
        info.resetIterationCount();
        return DTW.dtwRecusion(info);
    }
}
