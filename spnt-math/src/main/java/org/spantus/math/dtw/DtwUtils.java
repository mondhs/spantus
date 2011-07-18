package org.spantus.math.dtw;

import java.awt.Point;
import java.util.List;


public class DtwUtils {
	protected static void debug(String msg) {
//		System.out.println("[" + DTW.class.getSimpleName() + "]" + msg);
	}
	
	protected static void debug(DtwResult result) {
//		debug("result: " + result);
//		StringBuffer sb = new StringBuffer();
//		for (Point p : result.getPath()) {
//			sb.append(p.x).append(", ").append(p.y).append("\n");
//		}
//		debug("path: \n" + sb);

	}

	protected static void track(DtwResult currentResult, DtwResult selected) {
		currentResult.getPath().addAll(selected.getPath());
		currentResult.getPath().add(selected.getP());

	}

	protected static DtwCompare getMinValue(DtwResult[] dtwResults) {
		DtwCompare res = new DtwCompare();
		int minIndex = 0;
		DtwResult min = new DtwResult();
		min.setResult(Double.MAX_VALUE);
		for (DtwResult dr : dtwResults) {
			if(dr == null) continue;
			if (Math.min(min.getResult(), dr.getResult()) == dr.getResult()) {
				min = dr;
			}
		}
		res.setMinIndex(minIndex);
		res.setMinValue(min);
		return res;
	}

	public static Point point(int x, int y) {
		return new Point(x, y);
	}

	public static String logMatrix(List<List<Float>> matrix) {
		StringBuffer sb = new StringBuffer();
		for (List<Float> row : matrix) {
			for (Float float1 : row) {
				sb.append(float1).append(",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
