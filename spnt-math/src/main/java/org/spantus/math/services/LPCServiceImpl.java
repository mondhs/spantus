package org.spantus.math.services;

import java.util.List;

import org.spantus.math.Autocorrelation;
import org.spantus.math.LPC;

public class LPCServiceImpl implements LPCService {

	public List<Float> calculateLPC(List<Float> x, int order) {
		List<Float> autocorr = Autocorrelation.calc(x, order);
		List<Float> lpc = LPC.calcForAutocorr(autocorr);
		return lpc;
	}

}
