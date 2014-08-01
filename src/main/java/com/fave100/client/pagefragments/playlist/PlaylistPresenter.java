package com.fave100.client.pagefragments.playlist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.Utils;
import com.fave100.client.events.song.PlaylistSongChangedEvent;
import com.fave100.client.events.song.YouTubePlayerEndedEvent;
import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.YouTubeSearchResult;
import com.fave100.client.generated.entities.YouTubeSearchResultCollection;
import com.fave100.client.pagefragments.popups.addsong.AddSongPresenter;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pages.song.widgets.whyline.WhylinePresenter;
import com.fave100.client.pages.song.widgets.youtube.YouTubePresenter;
import com.fave100.client.resources.css.AppClientBundle;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class PlaylistPresenter extends PresenterWidget<PlaylistPresenter.MyView> implements PlaylistUiHandlers {

	public interface MyView extends View, HasUiHandlers<PlaylistUiHandlers> {

		void playSong(String listName, String username, String song, String artist, String videoId, boolean globalList, List<PlaylistItem> playlistItems);

		void scrollPlayingItemToTop();

		void setFullScreen(boolean fullScreen);
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> YOUTUBE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> WHYLINE_SLOT = new Type<RevealContentHandler<?>>();

	private EventBus _eventBus;
	private List<PlaylistItem> _playlistItems = new ArrayList<PlaylistItem>(100);;
	private int _playingSongIndex = 0;
	private PlaceManager _placeManager;
	private CurrentUser _currentUser;
	private FaveApi _api;
	private String _listName = "";
	private String _username = "";
	private boolean _globalList;
	private List<FaveItem> _faveItems;
	private List<YouTubeSearchResult> _youTubeSearchResults;
	private boolean _skippedVideo;
	@Inject AddSongPresenter _addSongPresenter;
	@Inject YouTubePresenter youtubePresenter;
	@Inject WhylinePresenter whylinePresenter;
	@Inject LoginPopupPresenter _loginBox;

	@Inject
	PlaylistPresenter(EventBus eventBus, MyView view, final PlaceManager placeManager, final CurrentUser currentUser, final FaveApi api) {
		super(eventBus, view);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_currentUser = currentUser;
		_api = api;

		getView().setUiHandlers(this);

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				if (Utils.isSmallDisplay()) {
					youtubePresenter.stopVideo();
				}
			}
		});
	}

	@Override
	protected void onBind() {
		super.onBind();

		setInSlot(YOUTUBE_SLOT, youtubePresenter);
		setInSlot(WHYLINE_SLOT, whylinePresenter);

		PlaylistSongChangedEvent.register(_eventBus, new PlaylistSongChangedEvent.Handler() {
			@Override
			public void onPlaylistSongChanged(final PlaylistSongChangedEvent event) {
				playSong(event.getSongId(), _listName, _username, _globalList, _faveItems);
			}
		});

		YouTubePlayerEndedEvent.register(_eventBus, new YouTubePlayerEndedEvent.Handler() {
			@Override
			public void onYouTubePlayerEnded(final YouTubePlayerEndedEvent event) {
				// Start playing the next song in the playlist
				if (_playingSongIndex < _playlistItems.size()) {
					PlaylistItem playing = _playlistItems.get(_playingSongIndex + 1);
					_eventBus.fireEvent(new PlaylistSongChangedEvent(playing.getSongID(), playing.getSong(), playing.getArtist(), playing.getList(), playing.getUsername(), _playlistItems));
				}

			}
		});

		addRegisteredHandler(NavigationEvent.getType(), new NavigationHandler() {

			@Override
			public void onNavigation(NavigationEvent navigationEvent) {
				if (Utils.isSmallDisplay()) {
					if (_placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.song)) {
						getView().setFullScreen(true);
					}
					else {
						getView().setFullScreen(false);
						youtubePresenter.stopVideo();
					}
				}
			}
		});
	}

	public void playSong(String songId, String song, String artist) {
		_faveItems = new ArrayList<>();
		FaveItem faveItem = new FaveItem();
		faveItem.setId(songId);
		faveItem.setSong(song);
		faveItem.setArtist(artist);
		_faveItems.add(faveItem);
		playSong(songId, "", "", false, _faveItems);
	}

	public void playSong(String songId, final String listName, final String username, boolean globalList, final List<FaveItem> faveItems) {
		_listName = listName;
		_username = username;
		_globalList = globalList;
		_faveItems = faveItems;

		FaveItem tempPlaying = null;
		int i = 0;
		for (FaveItem faveItem : _faveItems) {
			if (faveItem.getId().equals(songId)) {
				tempPlaying = faveItem;
				_playingSongIndex = i;
			}
			i++;
		}

		final FaveItem playing = tempPlaying;

		_playlistItems.clear();
		int rank = 1;
		for (FaveItem faveItem : _faveItems) {
			PlaylistItem playlistItem = new PlaylistItem(_eventBus);
			playlistItem.setCurrentlyPlaying(faveItem.getId().equals(songId));
			playlistItem.setRank(rank);
			playlistItem.setSongID(faveItem.getId());
			playlistItem.setSong(faveItem.getSong());
			playlistItem.setArtist(faveItem.getArtist());
			playlistItem.setWhyline(faveItem.getWhyline());
			playlistItem.setList(_listName);
			playlistItem.setUsername(_username);
			_playlistItems.add(playlistItem);
			rank++;
		}

		whylinePresenter.showWhylines(playing);

		_api.call(_api.service().search().getYouTubeResults(playing.getSong(), playing.getArtist()), new AsyncCallback<YouTubeSearchResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(YouTubeSearchResultCollection result) {
				_youTubeSearchResults = result.getItems();
				if (_youTubeSearchResults.isEmpty()) {
					nextSong();
				}
				else {
					getView().playSong(_listName, _username, playing.getSong(), playing.getArtist(), _youTubeSearchResults.get(0).getVideoId(), _globalList, _playlistItems);
					youtubePresenter.setYouTubeVideos(_youTubeSearchResults);
				}
			}
		});

		if (Utils.isSmallDisplay()) {
			_placeManager.revealPlace(new PlaceRequest.Builder()
					.nameToken(NameTokens.song)
					.build());

			getView().setFullScreen(true);
		}
		else {
			getView().setFullScreen(false);
		}

		RootPanel.get().addStyleName(AppClientBundle.INSTANCE.getGlobalCss().playlistVisible());
	}

	@Override
	public void previousSong() {
		int previousSongIndex = _playingSongIndex - 1;
		if (previousSongIndex >= 0) {
			String songId = _faveItems.get(previousSongIndex).getId();
			playSong(songId, _listName, _username, _globalList, _faveItems);
			getView().scrollPlayingItemToTop();
		}
	}

	@Override
	public void nextSong() {
		int nextSongIndex = _playingSongIndex + 1;
		if (_faveItems.size() > nextSongIndex) {
			FaveItem playing = _faveItems.get(nextSongIndex);
			_eventBus.fireEvent(new PlaylistSongChangedEvent(playing.getId(), playing.getSong(), playing.getArtist(), _listName, _username, _playlistItems));
			getView().scrollPlayingItemToTop();
		}
	}

	// TODO: Verify this is working
	@Override
	public void skipVideo() {
		if (_youTubeSearchResults.size() > 1 && !_skippedVideo) {
			FaveItem playing = _faveItems.get(_playingSongIndex);
			getView().playSong(_listName, _username, playing.getSong(), playing.getArtist(), _youTubeSearchResults.get(1).getVideoId(), _globalList, _playlistItems);
			_skippedVideo = true;
		}
		else {
			nextSong();
		}
	}

	@Override
	public void addSong() {
		if (!_currentUser.isLoggedIn()) {
			addToPopupSlot(_loginBox);
			_loginBox.showLogin();
		}
		else {
			FaveItem playing = _faveItems.get(_playingSongIndex);
			if (_currentUser.getHashtags().size() == 1) {
				_currentUser.addSong(playing.getId(), playing.getSong(), playing.getArtist());
			}
			else {
				_addSongPresenter.setSongToAddId(playing.getId());
				_addSongPresenter.setSongToAddName(playing.getSong());
				_addSongPresenter.setSongToAddArtist(playing.getArtist());
				addToPopupSlot(_addSongPresenter);
			}
		}
	}

	@Override
	public void stopSong() {
		youtubePresenter.stopVideo();
	}

	@Override
	public void toggleThumbs() {
		youtubePresenter.toggleThumbs();
	}

	@Override
	public void hideThumbs() {
		youtubePresenter.hideThumbs();
	}
}
