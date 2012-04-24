package org.spantus.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public final class HandlerUtils {

	public static void addMessage(Handler mHandler, String msgStr) {
		Message msg = Message.obtain();
		Bundle bundle = msg.getData();
		bundle.clear();
		bundle.putString("MSG", msgStr);
		mHandler.sendMessage(msg);
	}

	public static String retrieveMessageStr(Message msg) {
		return (String) msg.getData().get("MSG");
	}
}
