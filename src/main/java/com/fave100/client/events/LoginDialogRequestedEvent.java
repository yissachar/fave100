package com.fave100.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class LoginDialogRequestedEvent extends GwtEvent<LoginDialogRequestedHandler> {

	private final static Type<LoginDialogRequestedHandler> TYPE = new Type<>();

	public static Type<LoginDialogRequestedHandler> getType() {
		return TYPE;
	}

	private boolean _register;

	public LoginDialogRequestedEvent() {
		this(false);
	}

	public LoginDialogRequestedEvent(boolean register) {
		_register = register;
	}

	@Override
	public Type<LoginDialogRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoginDialogRequestedHandler handler) {
		handler.onLoginDialogRequested(_register);
	}

}
