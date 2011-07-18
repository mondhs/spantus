package org.spantus.math.services;

import java.util.List;

import org.spantus.math.Autocorrelation;
import org.spantus.math.LPC;
import org.spantus.math.LPCResult;

public class LPCServiceImpl implements LPCService {

	public LPCResult calculateLPC(List<Double> x, int order) {
		List<Double> autocorr = Autocorrelation.calc(x, order+1);
		LPCResult lpc = LPC.calcForAutocorr(autocorr);
		return lpc;
	}

}
