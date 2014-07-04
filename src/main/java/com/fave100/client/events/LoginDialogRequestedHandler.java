package com.fave100.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface LoginDialogRequestedHandler extends EventHandler {

	void onLoginDialogRequested(boolean register);
}
