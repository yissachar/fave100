package com.fave100.client.pages.song.widgets.playlist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.events.song.PlaylistSongChangedEvent;
import com.fave100.client.events.song.YouTubePlayerEndedEvent;
import com.fave100.client.generated.entities.FaveItem;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class PlaylistPresenter extends PresenterWidget<PlaylistPresenter.MyView> {

	public interface MyView extends View {
		void setUsername(String username);

		void setHashtag(String hashtag);

		void setAvatar(String avatar);

		void setUrls(String username, String hashtag);

		void setPlaylist(List<PlaylistItem> playlistItems);

		void setPlaylistHeight(int px);
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

	public void setUserInfo(final String username, final String hashtag, final String avatar) {
		getView().setUsername(username);
		getView().setHashtag(hashtag);
		getView().setAvatar(avatar);
		getView().setUrls(username, hashtag);
	}

	public void setPlaylist(final List<FaveItem> playlist, final String songPageID) {
		playlistItems.clear();
		int i = 1;
		for (final FaveItem faveItem : playlist) {
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

		if (currentPlayingItem != null)
			scrollPlaylistItemToTop(currentPlayingItem);

	}

	private void scrollPlaylistItemToTop(PlaylistItem item) {
		// First simply scroll it into the view
		item.getElement().scrollIntoView();

		// Pick an item further down the list and scroll it into view
		PlaylistItem toScroll = null;
		int i = 7;
		while (toScroll == null && i > 0) {
			final int furtherIndex = playlistItems.indexOf(item) + i;
			if (furtherIndex < playlistItems.size())
				toScroll = playlistItems.get(furtherIndex);
			i--;
		}

		if (toScroll != null) {
			toScroll.getElement().scrollIntoView();
			// At this point our item may be too high up and out of view, so scroll it back into view
			item.getElement().scrollIntoView();
		}
	}

	public void setHeight(final int px) {
		getView().setPlaylistHeight(px);
	}

}
