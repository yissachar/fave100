package com.fave100.client.events;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * 
 * @author yissachar.radcliffe
 * 
 */
public class ListUnstarredEvent extends Event<ListUnstarredEvent.Handler> {

	public interface Handler {
		void onListStarred(ListUnstarredEvent event);
	}

	private static final Type<ListUnstarredEvent.Handler> TYPE =
			new Type<ListUnstarredEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final ListUnstarredEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private String _exception = "";

	public ListUnstarredEvent() {
	}

	public ListUnstarredEvent(final String exception) {
		_exception = exception;
	}

	public String getException() {
		return _exception;
	}

	@Override
	public Type<ListUnstarredEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onListStarred(this);
	}
}
