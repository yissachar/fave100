package com.fave100.client.events.user;

import com.fave100.client.generated.entities.AppUserDto;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * 
 * @author yissachar.radcliffe
 * 
 */
public class UserFollowedEvent extends Event<UserFollowedEvent.Handler> {

	public interface Handler {
		void onUserFollowed(UserFollowedEvent event);
	}

	private static final Type<UserFollowedEvent.Handler> TYPE =
			new Type<UserFollowedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final UserFollowedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private AppUserDto _user = null;

	public UserFollowedEvent(final AppUserDto user) {
		_user = user;
	}

	public AppUserDto getUser() {
		return _user;
	}

	@Override
	public Type<UserFollowedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onUserFollowed(this);
	}
}
