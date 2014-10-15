package com.fave100.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RegisterDialogRequestedEvent extends GwtEvent<RegisterDialogRequestedHandler> {

	private final static Type<RegisterDialogRequestedHandler> TYPE = new Type<>();

	public static Type<RegisterDialogRequestedHandler> getType() {
		return TYPE;
	}

	public RegisterDialogRequestedEvent() {
	}

	@Override
	public Type<RegisterDialogRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RegisterDialogRequestedHandler handler) {
		handler.onRegisterDialogRequested();
	}

}
