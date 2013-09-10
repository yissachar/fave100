package com.fave100.client.events.favelist;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author yissachar.radcliffe
 * 
 */
public class ListChangedEvent extends Event<ListChangedEvent.Handler> {

	public interface Handler {
		void onListChanged(ListChangedEvent event);
	}

	private static final Type<ListChangedEvent.Handler> TYPE =
			new Type<ListChangedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final ListChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private String list = "";

	public ListChangedEvent(final String list) {
		this.list = list;
	}

	@Override
	public Type<ListChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onListChanged(this);
	}

	public String getList() {
		return list;
	}
}
