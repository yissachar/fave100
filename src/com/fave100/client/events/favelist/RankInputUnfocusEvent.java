package com.fave100.client.events.favelist;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author yissachar.radcliffe
 * 
 */
public class RankInputUnfocusEvent extends Event<RankInputUnfocusEvent.Handler> {

	public interface Handler {
		void onRankInputUnfocus(RankInputUnfocusEvent event);
	}

	private static final Type<RankInputUnfocusEvent.Handler> TYPE =
			new Type<RankInputUnfocusEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final RankInputUnfocusEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public RankInputUnfocusEvent() {
	}

	@Override
	public Type<RankInputUnfocusEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onRankInputUnfocus(this);
	}
}
