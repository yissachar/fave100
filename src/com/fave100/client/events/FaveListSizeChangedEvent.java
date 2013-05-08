package com.fave100.client.events;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This event indicates that song was selected from
 * a {@link SongSuggestBox}.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class FaveListSizeChangedEvent extends Event<FaveListSizeChangedEvent.Handler> {

	public interface Handler {
		void onFaveListLoaded(FaveListSizeChangedEvent event);
	}

	private static final Type<FaveListSizeChangedEvent.Handler> TYPE =
			new Type<FaveListSizeChangedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final FaveListSizeChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private int size = 0;

	public FaveListSizeChangedEvent(final int size) {
		this.size = size;
	}

	@Override
	public Type<FaveListSizeChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onFaveListLoaded(this);
	}

	public int getSize() {
		return size;
	}
}
