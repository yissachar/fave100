package com.fave100.client.events;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * 
 * @author yissachar.radcliffe
 * 
 */
public class ListStarredEvent extends Event<ListStarredEvent.Handler> {

	public interface Handler {
		void onListStarred(ListStarredEvent event);
	}

	private static final Type<ListStarredEvent.Handler> TYPE =
			new Type<ListStarredEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final ListStarredEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public ListStarredEvent() {
	}

	@Override
	public Type<ListStarredEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onListStarred(this);
	}
}
