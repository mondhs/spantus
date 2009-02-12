package org.spantus.math.dtw;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class DtwResult {
	private List<Point> path;
	private Float result;
	private Point p;
	
	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public Float getResult() {
		return result;
	}

	public void setResult(Float result) {
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
