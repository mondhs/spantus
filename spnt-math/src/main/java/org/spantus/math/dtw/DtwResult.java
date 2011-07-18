package org.spantus.math.dtw;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class DtwResult {
	private List<Point> path;
	private Double result;
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

}
