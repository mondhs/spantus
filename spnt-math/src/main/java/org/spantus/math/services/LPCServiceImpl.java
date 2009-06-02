package org.spantus.math.services;

import java.util.List;

import org.spantus.math.Autocorrelation;
import org.spantus.math.LPC;
import org.spantus.math.LPCResult;

public class LPCServiceImpl implements LPCService {

	public LPCResult calculateLPC(List<Float> x, int order) {
		List<Float> autocorr = Autocorrelation.calc(x, order);
		LPCResult lpc = LPC.calcForAutocorr(autocorr);
		lpc.setResult(MathServicesFactory.createFFTService().calculateFFTMagnitude(lpc.getResult()));
		return lpc;
	}

}
