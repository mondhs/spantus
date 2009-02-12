package org.spantus.utils;

public abstract class Assert {
	
	static public void isTrue(boolean val){
		if(val == false)
		throw new IllegalArgumentException("assertion failed");
	}
	static public void isTrue(boolean val, String msg){
		if(val == false)
		throw new IllegalArgumentException(msg);
	}

}
