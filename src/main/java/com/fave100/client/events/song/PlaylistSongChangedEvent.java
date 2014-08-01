package com.fave100.client.events.song;

import java.util.List;

import com.fave100.client.pagefragments.playlist.PlaylistItem;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
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

	private final String _songId;
	private final String _song;
	private final String _artist;
	private final String _list;
	private final String _username;
	private final List<PlaylistItem> _playlist;

	public PlaylistSongChangedEvent(final String songID, String song, String artist, String list, String username, List<PlaylistItem> playlist) {
		this._songId = songID;
		_song = song;
		_artist = artist;
		_list = list;
		_username = username;
		_playlist = playlist;
	}

	@Override
	public Type<PlaylistSongChangedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public String getSongId() {
		return _songId;
	}

	public String getSong() {
		return _song;
	}

	public String getArtist() {
		return _artist;
	}

	public String getList() {
		return _list;
	}

	public String getUsername() {
		return _username;
	}

	public List<PlaylistItem> getPlaylist() {
		return _playlist;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onPlaylistSongChanged(this);
	}
}
