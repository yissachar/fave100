package com.fave100.client.events.favelist;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author yissachar.radcliffe
 * 
 */
public class ListAddedEvent extends Event<ListAddedEvent.Handler> {

	public interface Handler {
		void onListAdded(ListAddedEvent event);
	}

	private static final Type<ListAddedEvent.Handler> TYPE =
			new Type<ListAddedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final ListAddedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private String list = "";

	public ListAddedEvent(final String list) {
		this.list = list;
	}

	@Override
	public Type<ListAddedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onListAdded(this);
	}

	public String getList() {
		return list;
	}
}
