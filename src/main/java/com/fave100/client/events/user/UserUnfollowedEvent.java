package com.fave100.client.events.user;

import com.fave100.client.generated.entities.AppUser;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * 
 * @author yissachar.radcliffe
 * 
 */
public class UserUnfollowedEvent extends Event<UserUnfollowedEvent.Handler> {

	public interface Handler {
		void onUserUnfollowed(UserUnfollowedEvent event);
	}

	private static final Type<UserUnfollowedEvent.Handler> TYPE =
			new Type<UserUnfollowedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final UserUnfollowedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private String _exception = "";
	private AppUser _user = null;

	public UserUnfollowedEvent() {
	}

	public UserUnfollowedEvent(final AppUser user, final String exception) {
		_user = user;
		_exception = exception;
	}

	public UserUnfollowedEvent(final AppUser user) {
		_user = user;
	}

	public String getException() {
		return _exception;
	}

	public AppUser getUser() {
		return _user;
	}

	@Override
	public Type<UserUnfollowedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onUserUnfollowed(this);
	}
}
