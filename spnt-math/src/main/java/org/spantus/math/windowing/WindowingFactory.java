package org.spantus.math.windowing;

public abstract class WindowingFactory {
	
	public static Windowing createWindowing(WindowingEnum windowingEnum){
		if(windowingEnum == null){
			return new HammingWindowing();			
		}
		switch (windowingEnum) {
		case Hamming:
			return new HammingWindowing();			
		case Hanning:
			return new HanningWindowing();			
		case Barlett:
			return new BarlettWindowing();			
		case Welch:
			return new WelchWindowing();			
		case Rextangular:
			return new RextangularWindowing();			
		case ButterworthWindowing:
			return new ButterworthWindowing();
		default:
			return new HammingWindowing();			
		}
	}

}
