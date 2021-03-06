package org.spantus.math.dtw;


public class DtwTypeIII implements DtwType {

	public DtwResult dtwRecusion(int x, int y, DtwInfo info) {
			DtwResult result = new DtwResult();
			result.setP(DtwUtils.point(x, y));
			info.increaseIterationCount();

			if (Double.isNaN(info.get(x, y))) {
				result.setResult(Double.MAX_VALUE);
				return result;
			}
			if(x == 0 && y == 0){
				result.setResult(info.get(x, y));
				return result;
			}
			DtwResult[] ress = new DtwResult[3];
			if (x > 0 && y > 0) {
				ress[0] = dtwRecusion(x - 1, y - 1, info);
			}
			if (x > 1 && y >0) {
				ress[1] = dtwRecusion(x - 2 , y - 1, info);
				ress[1].setResult(ress[1].getResult());

			}
			if (y > 1 && x > 0) {
				ress[2]	= dtwRecusion(x-1, y-2, info);
				ress[2].setResult(ress[2].getResult());

			}
			DtwCompare compared = DtwUtils.getMinValue(ress);
			DtwUtils.track(result, compared.getMinValue());
			Double gama = info.get(x, y) + compared.getMinValue().getResult();
			result.setResult(gama);

			return result;
	}

}
