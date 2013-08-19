package com.fave100.client.pages.song;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.song.PlaylistSongChangedEvent;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.song.widgets.playlist.PlaylistPresenter;
import com.fave100.client.pages.song.widgets.youtube.YouTubePresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.SongInterface;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.fave100.shared.requestfactory.SongProxy;
import com.fave100.shared.requestfactory.YouTubeSearchResultProxy;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

/**
 * A song page that will display details about the song
 * 
 * @author yissachar.radcliffe
 * 
 */
public class SongPresenter extends
		BasePresenter<SongPresenter.MyView, SongPresenter.MyProxy> implements
		SongUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<SongUiHandlers> {
		void setSongInfo(SongInterface song);

		void setPlaylist(Boolean visible);

		void showPlaylist();

		void showWhylines();

		void setWhylineHeight(int px);

		void scrollYouTubeIntoView();

		void clearWhylines();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.song)
	public interface MyProxy extends ProxyPlace<SongPresenter> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> YOUTUBE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> PLAYLIST_SLOT = new Type<RevealContentHandler<?>>();

	public static final String ID_PARAM = "id";
	public static final String USER_PARAM = "user";
	public static final String LIST_PARAM = "list";

	private final ApplicationRequestFactory _requestFactory;
	private final CurrentUser _currentUser;
	private final PlaceManager _placeManager;
	private final EventBus _eventBus;
	private SongInterface songProxy;
	private AppUserProxy _requestedAppUser;
	@Inject YouTubePresenter youtubePresenter;
	@Inject PlaylistPresenter playlistPresenter;

	@Inject
	public SongPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final ApplicationRequestFactory requestFactory, final CurrentUser currentUser,
							final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_currentUser = currentUser;
		_placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		// Use parameters to determine what to reveal on page
		final String id = URL.decode(placeRequest.getParameter(ID_PARAM, ""));
		final String username = URL.decode(placeRequest.getParameter(USER_PARAM, ""));
		final String hashtag = URL.decode(placeRequest.getParameter(LIST_PARAM, Constants.DEFAULT_HASHTAG));

		// Valid parameter combinations:
		// id: show the song and any whylines
		// user: show the users #fave100 playlist, starting from the first song
		// hashtag: show the aggregate list for the given hashtag
		// id + user: show the user's #fave100 playlist, starting from the provided song
		// id + hashtag: show the aggregate list for the given hashtag, starting from the provided song
		// user + hashtag: show the user's playlist for the given hashtag, starting from the first song
		// id + user + hashtag: show the user's playlist for the given hashtag, starting from the provided song

		if (!id.isEmpty()) {
			// Load the song from the datastore
			final Request<SongProxy> getSongReq = _requestFactory.songRequest()
					.findSong(id);
			getSongReq.fire(new Receiver<SongProxy>() {
				@Override
				public void onSuccess(final SongProxy song) {
					songProxy = song;
					updateYouTube();

					getProxy().manualReveal(SongPresenter.this);
				}
			});
		}

		// By default, hide playlist
		getView().setPlaylist(false);
		getView().showWhylines();

		// If there is a user, get their info and their playlist
		if (!username.isEmpty()) {
			// If we have a current user, just grab their info locally
			if (_currentUser.isLoggedIn() && _currentUser.getUsername().equals(username)) {
				_requestedAppUser = _currentUser;
				playlistPresenter.setUserInfo(_currentUser.getUsername(), hashtag, _currentUser.getAvatarImage());
			}
			else if (_requestedAppUser != null && username.equals(_requestedAppUser.getUsername())) {
				// We already fetched the user info
				playlistPresenter.setUserInfo(_requestedAppUser.getUsername(), hashtag, _requestedAppUser.getAvatarImage());
			}
			else {
				// Get username and avatar from the datastore
				final Request<AppUserProxy> getUserReq = _requestFactory.appUserRequest().findAppUser(username);
				getUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy user) {
						_requestedAppUser = user;
						if (user != null) {
							playlistPresenter.setUserInfo(user.getUsername(), hashtag, user.getAvatarImage());
						}
					}
				});
			}

			// Get the list for the requested user 
			// TODO: If user's own playlist, use that instead of going to server
			final Request<List<FaveItemProxy>> getFavelistReq = _requestFactory.faveListRequest().getFaveList(username, hashtag);
			getFavelistReq.fire(new Receiver<List<FaveItemProxy>>() {
				@Override
				public void onSuccess(final List<FaveItemProxy> favelist) {
					loadedFavelist(id, favelist);
				}
			});

		}
		else {
			// Get the master list for the hashtag
			final Request<List<FaveItemProxy>> getFaveListReq = _requestFactory.faveListRequest().getMasterFaveList(hashtag);
			getFaveListReq.fire(new Receiver<List<FaveItemProxy>>() {
				@Override
				public void onSuccess(final List<FaveItemProxy> favelist) {
					playlistPresenter.setUserInfo("", hashtag, "");
					loadedFavelist(id, favelist);
				}
			});
		}
	}

	private void loadedFavelist(final String id, final List<FaveItemProxy> favelist) {
		// Only show playlist if good params
		if (favelist != null && favelist.size() > 0) {
			getView().setPlaylist(true);
			playlistPresenter.setPlaylist(favelist, id.isEmpty() ? favelist.get(0).getId() : id);
			getView().showPlaylist();

			// No id provided, start from first song in list
			if (id.isEmpty()) {
				songProxy = favelist.get(0);
				updateYouTube();

				getProxy().manualReveal(SongPresenter.this);
			}
		}
	}

	@Override
	protected void onBind() {
		super.onBind();

		PlaylistSongChangedEvent.register(_eventBus, new PlaylistSongChangedEvent.Handler() {
			@Override
			public void onPlaylistSongChanged(final PlaylistSongChangedEvent event) {

				// Load the song from the datastore
				final Request<SongProxy> getSongReq = _requestFactory.songRequest()
						.findSong(event.songID());
				getSongReq.fire(new Receiver<SongProxy>() {
					@Override
					public void onSuccess(final SongProxy song) {
						songProxy = song;
						updateYouTube();
					}
				});
			}
		});

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				resizePlaylist();
			}
		});
	}

	private void resizePlaylist() {
		final Timer timer = new Timer() {
			@Override
			public void run() {
				final int newHeight = youtubePresenter.asWidget().getOffsetHeight() + 7;
				playlistPresenter.setHeight(newHeight);
				getView().setWhylineHeight(newHeight);
			}
		};
		timer.schedule(500);

	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(YOUTUBE_SLOT, youtubePresenter);
		setInSlot(PLAYLIST_SLOT, playlistPresenter);
	}

	@Override
	protected void onHide() {
		super.onHide();
		_requestedAppUser = null;
		youtubePresenter.clearVideo();
		getView().clearWhylines();
	}

	private void updateYouTube() {
		getView().setSongInfo(songProxy);

		// Get any YouTube videos
		final Request<List<YouTubeSearchResultProxy>> getYoutubeReq = _requestFactory.songRequest()
				.getYouTubeResults(songProxy.getSong(), songProxy.getArtist());
		getYoutubeReq.fire(new Receiver<List<YouTubeSearchResultProxy>>() {
			@Override
			public void onSuccess(final List<YouTubeSearchResultProxy> results) {
				youtubePresenter.setYouTubeVideos(results);
				resizePlaylist();
				getView().scrollYouTubeIntoView();
			}
		});
	}

	@Override
	public void addSong() {
		// Make sure we actually have a song to work with
		if (songProxy == null)
			return;

		_currentUser.addSong(songProxy.getId(), songProxy.getSong(), songProxy.getArtist());
	}
}

interface SongUiHandlers extends UiHandlers {
	void addSong();
}
