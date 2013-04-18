package com.fave100.client.events;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This event will be dispatched when a {@link SimplePager} changes its page.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class ResultPageChangedEvent extends Event<ResultPageChangedEvent.Handler> {

	public interface Handler {
		void onPageChanged(ResultPageChangedEvent event);
	}

	private static final Type<ResultPageChangedEvent.Handler> TYPE =
			new Type<ResultPageChangedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final ResultPageChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private final int pageNumber;

	public ResultPageChangedEvent(final int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public Type<ResultPageChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onPageChanged(this);
	}
}