package com.fave100.client.events.favelist;

import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author yissachar.radcliffe
 * 
 */
public class FaveItemAddedEvent extends Event<FaveItemAddedEvent.Handler> {

	public interface Handler {
		void onFaveItemAdded(FaveItemAddedEvent event);
	}

	private static final Type<FaveItemAddedEvent.Handler> TYPE =
			new Type<FaveItemAddedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final FaveItemAddedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private FaveItemProxy faveItem;

	public FaveItemAddedEvent(final FaveItemProxy faveItem) {
		this.faveItem = faveItem;
	}

	@Override
	public Type<FaveItemAddedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onFaveItemAdded(this);
	}

	public FaveItemProxy getFaveItemProxy() {
		return faveItem;
	}
}
