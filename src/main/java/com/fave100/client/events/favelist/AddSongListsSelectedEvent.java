package com.fave100.client.events.favelist;

import java.util.List;

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
public class AddSongListsSelectedEvent extends Event<AddSongListsSelectedEvent.Handler> {

	public interface Handler {
		void onAddSongListsSelected(AddSongListsSelectedEvent event);
	}

	private static final Type<AddSongListsSelectedEvent.Handler> TYPE =
			new Type<AddSongListsSelectedEvent.Handler>();

	public static HandlerRegistration register(final EventBus eventBus,
			final AddSongListsSelectedEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}

	private final List<String> _selectedLists;
	private final String _songId;
	private final String _songName;
	private final String _songArtist;

	public AddSongListsSelectedEvent(final List<String> selectedLists, String songId, String songName, String songArtist) {
		_selectedLists = selectedLists;
		_songId = songId;
		_songName = songName;
		_songArtist = songArtist;
	}

	@Override
	public Type<AddSongListsSelectedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler) {
		handler.onAddSongListsSelected(this);
	}

	public List<String> getSelectedLists() {
		return _selectedLists;
	}

	public String getSongId() {
		return _songId;
	}

	public String getSongName() {
		return _songName;
	}

	public String getSongArtist() {
		return _songArtist;
	}

}
