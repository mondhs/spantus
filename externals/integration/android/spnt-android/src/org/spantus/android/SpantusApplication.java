package org.spantus.android;

import org.spantus.android.ui.admin.SpntAdminContext;

import android.app.Application;

public class SpantusApplication extends Application{
	private SpntAdminContext spntAdminContext;

	public SpntAdminContext getSpntAdminContext() {
		return spntAdminContext;
	}

	public void setSpntAdminContext(SpntAdminContext spntAdminContext) {
		this.spntAdminContext = spntAdminContext;
	}

}
