package com.fave100.client.pages.song.widgets.playlist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.events.PlaylistSongChangedEvent;
import com.fave100.client.events.YouTubePlayerEndedEvent;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class PlaylistPresenter extends PresenterWidget<PlaylistPresenter.MyView> {

	public interface MyView extends View {
		void setUsername(String username);

		void setUrl(String avatar);

		void setPlaylist(List<PlaylistItem> playlistItems);
	}

	private EventBus _eventBus;
	private List<PlaylistItem> playlistItems = new ArrayList<PlaylistItem>(100);;
	private int playingSongIndex = 0;

	@Inject
	public PlaylistPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		_eventBus = eventBus;
	}

	@Override
	protected void onBind() {
		super.onBind();

		PlaylistSongChangedEvent.register(_eventBus, new PlaylistSongChangedEvent.Handler() {
			@Override
			public void onPlaylistSongChanged(final PlaylistSongChangedEvent event) {
				setPlayingSong(event.songID());
			}
		});

		YouTubePlayerEndedEvent.register(_eventBus, new YouTubePlayerEndedEvent.Handler() {
			@Override
			public void onYouTubePlayerEnded(final YouTubePlayerEndedEvent event) {
				// Start playing the next song in the playlist
				if (playingSongIndex < playlistItems.size()) {
					final String nextSong = playlistItems.get(playingSongIndex + 1).getSongID();
					_eventBus.fireEvent(new PlaylistSongChangedEvent(nextSong));
				}

			}
		});
	}

	public void setUserInfo(final String username, final String avatar) {
		getView().setUsername(username);
		getView().setUrl(avatar);
	}

	public void setPlaylist(final List<FaveItemProxy> playlist, final String songPageID) {
		playlistItems.clear();
		int i = 1;
		for (final FaveItemProxy faveItem : playlist) {
			final PlaylistItem playlistItem = new PlaylistItem(_eventBus);
			playlistItem.setRank(i);
			playlistItem.setSong(faveItem.getSong());
			playlistItem.setArtist(faveItem.getArtist());
			playlistItem.setWhyline(faveItem.getWhyline());
			playlistItem.setSongID(faveItem.getSongID());
			playlistItems.add(playlistItem);
			i++;
		}
		getView().setPlaylist(playlistItems);
		// Need to run in a timer because there is slight delay until items are added to DOM
		final Timer timer = new Timer() {
			@Override
			public void run() {
				setPlayingSong(songPageID);
			}
		};
		timer.schedule(500);
	}

	public void setPlayingSong(final String songID) {
		// Scroll the currently playing item into roughly top of playlist
		PlaylistItem currentPlayingItem = null;
		for (final PlaylistItem playlistItem : playlistItems) {
			if (songID.equals(playlistItem.getSongID())) {
				playlistItem.setCurrentlyPlaying(true);
				currentPlayingItem = playlistItem;
				playingSongIndex = playlistItems.indexOf(playlistItem);
			}
			else {
				playlistItem.setCurrentlyPlaying(false);
			}
		}

		currentPlayingItem.getElement().scrollIntoView();
		PlaylistItem toScroll = null;
		int i = 7;
		while (toScroll == null && i > 0) {
			final int furtherIndex = playlistItems.indexOf(currentPlayingItem) + i;
			if (furtherIndex < playlistItems.size())
				toScroll = playlistItems.get(furtherIndex);
			i--;
		}

		if (toScroll != null) {
			toScroll.getElement().scrollIntoView();
			currentPlayingItem.getElement().scrollIntoView();
		}
	}

}
