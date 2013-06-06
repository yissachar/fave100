package com.fave100.client.events;

import com.fave100.client.CurrentUser;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This event indicates that the {@link CurrentUser} has changed.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class PlaylistSongChangedEvent extends Event<PlaylistSongChangedEvent.Handler> {

	public interface Handler {
		void onPlaylistSongChanged(PlaylistSongChangedEvent event);
	}

	private static final Type<PlaylistSongChangedEvent.Handler> TYPE =
			new Type<PlaylistSongChangedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final PlaylistSongChangedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private final String songID;

	public PlaylistSongChangedEvent(final String songID) {
		this.songID = songID;
	}

	@Override
	public Type<PlaylistSongChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String songID() {
		return songID;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onPlaylistSongChanged(this);
	}
}
