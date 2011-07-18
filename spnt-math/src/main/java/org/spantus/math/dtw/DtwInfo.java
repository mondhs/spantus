package org.spantus.math.dtw;

import java.util.List;

public class DtwInfo {
	

	public enum DtwType{typeI, typeII, typeIII};
	
	private List<List<Double>> distanceMatrix;
	
	private Integer iterationCount;

	private DtwType type;
	
	private Integer maxDistanceCoef;
	
	public Integer getIterationCount() {
		if(iterationCount == null){
			iterationCount = Integer.valueOf(0);
		}
		return iterationCount;
	}
	public void resetIterationCount() {
		this.iterationCount = null;
	}


	public void increaseIterationCount() {
		iterationCount = getIterationCount()+1;
	}

	public List<List<Double>> getDistanceMatrix() {
		return distanceMatrix;
	}

	public void setDistanceMatrix(List<List<Double>> distanceMatrix) {
		this.distanceMatrix = distanceMatrix;
	}
	public Double get(int x, int y){
		return getDistanceMatrix().get(y).get(x);
	}
	public DtwType getType() {
		if(type == null){
			type = DtwType.typeI;
		}
		return type;
	}

	public void setType(DtwType type) {
		this.type = type;
	}
	public Integer getMaxDistanceCoef() {
		return maxDistanceCoef;
	}
	public void setMaxDistanceCoef(Integer maxDistanceCoef) {
		this.maxDistanceCoef = maxDistanceCoef;
	}

}
