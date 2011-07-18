package org.spantus.core.threshold.test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TestUtil {
	public static Double round(Double double1, int precistion){
		return BigDecimal.valueOf(double1).setScale(precistion, RoundingMode.HALF_UP).doubleValue();
	}
}
