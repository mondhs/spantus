package org.spantus.android;

import java.text.MessageFormat;

import org.spantus.logger.ILogger;

import android.util.Log;

public class SpntAndroidLogger implements ILogger{

	@Override
	public void debug(String msg) {
		try {
			Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, msg);
		} catch (Exception e) {
			System.out.println(msg);
		}
	}

	@Override
	public void debug(String format, Object... params) {
		String msg = MessageFormat.format(format, params);
		debug(msg);
	}

	@Override
	public void error(String msg) {
		try {
			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, msg);
		} catch (Exception e) {
			System.err.println(e);
		}			
	}

	@Override
	public void error(Exception ex) {
		try {
			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, "", ex);
		} catch (Exception e) {
			System.err.println(e);
		}		
	}

	@Override
	public void error(String msg, Throwable t) {
		try {
			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, msg, t);
		} catch (Exception e) {
			System.err.println(msg);
		}
	}

	@Override
	public void fatal(String msg) {
		try {
			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, msg);
		} catch (Exception e) {
			System.err.println(msg);
		}		
		
	}

	@Override
	public void info(String msg) {
		try {
			Log.i(SpntConstant.SPNT_ANDROID_LOG_TAG, msg);
		} catch (Exception e) {
			System.out.println(msg);
		}				
	}

	@Override
	public void info(String format, Object... params) {
		String msg = MessageFormat.format(format, params);
		info(msg);		
	}

	@Override
	public boolean isDebugMode() {
		return false;
	}

//	public static final SpntAndroidLogger LOG = new SpntAndroidLogger();

//	public static SpntAndroidLogger getLogger(Class<?> clazz) {
//		return LOG;
//	}


//
//	public void debug(String format, Object... params) {
//		String msg = MessageFormat.format(format, params);
//		try {
//			Log.d(SpntConstant.SPNT_ANDROID_LOG_TAG, msg);
//		} catch (Exception e) {
//			System.out.println(msg);
//		}
//	}
//
//	public void error(Throwable e) {
//		try {
//			Log.e(SpntConstant.SPNT_ANDROID_LOG_TAG, e.getMessage(), e);
//		} catch (Exception e1) {
//			System.err.println(e);
//		}
//	}
	
	

}
