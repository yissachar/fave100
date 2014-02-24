package com.fave100.client.events.user;

import com.fave100.client.CurrentUser;
import com.fave100.client.generated.entities.AppUser;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This event indicates that the {@link CurrentUser} has changed.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class CurrentUserChangedEvent extends Event<CurrentUserChangedEvent.Handler> {

	public interface Handler {
		void onCurrentUserChanged(CurrentUserChangedEvent event);
	}

	private static final Type<CurrentUserChangedEvent.Handler> TYPE =
			new Type<CurrentUserChangedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final CurrentUserChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private final AppUser user;

	public CurrentUserChangedEvent(final AppUser user) {
		this.user = user;
	}

	@Override
	public Type<CurrentUserChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public AppUser getUser() {
		return user;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onCurrentUserChanged(this);
	}
}
