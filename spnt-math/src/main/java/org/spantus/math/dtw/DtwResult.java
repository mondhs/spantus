package org.spantus.math.dtw;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public class DtwResult {
	private List<Point> path;
	private Double result;
	private RealMatrix costMatrix;
	private StatisticalSummary statisticalSummary;
	private Point p;
	
	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public List<Point> getPath() {
		if(path == null){
			path = new ArrayList<Point>();
		}
		return path;
	}
	@Override
	public String toString() {
		return "; res: " + getResult();
	}

	public RealMatrix getCostMatrix() {
		return costMatrix;
	}

	public void setCostMatrix(RealMatrix costMatrix) {
		this.costMatrix = costMatrix;
	}

	public StatisticalSummary getStatisticalSummary() {
		return statisticalSummary;
	}

	public void setStatisticalSummary(StatisticalSummary statisticalSummary) {
		this.statisticalSummary = statisticalSummary;
	}

}
