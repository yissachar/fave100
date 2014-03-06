package com.fave100.client.events.song;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * The playing YouTube video ended.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class YouTubePlayerEndedEvent extends Event<YouTubePlayerEndedEvent.Handler> {

	public interface Handler {
		void onYouTubePlayerEnded(YouTubePlayerEndedEvent event);
	}

	private static final Type<YouTubePlayerEndedEvent.Handler> TYPE =
			new Type<YouTubePlayerEndedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final YouTubePlayerEndedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	public YouTubePlayerEndedEvent() {
	}

	@Override
	public Type<YouTubePlayerEndedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onYouTubePlayerEnded(this);
	}
}
