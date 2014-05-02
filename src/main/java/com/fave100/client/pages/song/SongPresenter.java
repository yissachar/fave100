package com.fave100.client.pages.song;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.song.PlaylistSongChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.FaveItemCollection;
import com.fave100.client.generated.entities.YouTubeSearchResultCollection;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pagefragments.popups.addsong.AddSongPresenter;
import com.fave100.client.pages.PagePresenter;
import com.fave100.client.pages.song.widgets.playlist.PlaylistPresenter;
import com.fave100.client.pages.song.widgets.whyline.WhylinePresenter;
import com.fave100.client.pages.song.widgets.youtube.YouTubePresenter;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * A song page that will display details about the song
 * 
 * @author yissachar.radcliffe
 * 
 */
public class SongPresenter extends
		PagePresenter<SongPresenter.MyView, SongPresenter.MyProxy>
		implements SongUiHandlers {

	public interface MyView extends View, HasUiHandlers<SongUiHandlers> {
		void setSongInfo(FaveItem song);

		void scrollYouTubeIntoView();

		int getSongContainerHeight();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.song)
	public interface MyProxy extends ProxyPlace<SongPresenter> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> YOUTUBE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> WHYLINE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> PLAYLIST_SLOT = new Type<RevealContentHandler<?>>();

	public static final String ID_PARAM = "id";
	public static final String USER_PARAM = "user";
	public static final String LIST_PARAM = "list";

	private final CurrentUser _currentUser;
	private final EventBus _eventBus;
	private FaveItem songProxy;
	private AppUser _requestedAppUser;
	private PlaceManager _placeManager;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	@Inject YouTubePresenter youtubePresenter;
	@Inject WhylinePresenter whylinePresenter;
	@Inject PlaylistPresenter playlistPresenter;
	@Inject private AddSongPresenter addSongPresenter;

	@Inject
	public SongPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final CurrentUser currentUser,
							final PlaceManager placeManager, final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view, proxy);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
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
			_dispatcher.execute(_restServiceFactory.songs().getSong(id), new AsyncCallback<FaveItem>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSuccess(FaveItem song) {
					songProxy = song;
					updateYouTube();

					getProxy().manualReveal(SongPresenter.this);
				}
			});
		}

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
				_dispatcher.execute(_restServiceFactory.users().getAppUser(username), new AsyncCallback<AppUser>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub						
					}

					@Override
					public void onSuccess(AppUser user) {
						_requestedAppUser = user;
						if (user != null) {
							playlistPresenter.setUserInfo(user.getUsername(), hashtag, user.getAvatarImage());
						}
					}
				});
			}

			// Get the list for the requested user
			_dispatcher.execute(_restServiceFactory.users().getFaveList(username, hashtag), new AsyncCallback<FaveItemCollection>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub					
				}

				@Override
				public void onSuccess(FaveItemCollection faveList) {
					loadedFavelist(id, faveList.getItems());
				}

			});

		}
		else {
			// Get the master list for the hashtag
			_dispatcher.execute(_restServiceFactory.favelists().getMasterFaveList(hashtag), new AsyncCallback<FaveItemCollection>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub					
				}

				@Override
				public void onSuccess(FaveItemCollection faveList) {
					playlistPresenter.setUserInfo("", hashtag, "");
					loadedFavelist(id, faveList.getItems());
				}

			});
		}
	}

	private void loadedFavelist(final String id, final List<FaveItem> favelist) {
		// Only show playlist if good params
		if (favelist != null && favelist.size() > 0) {
			playlistPresenter.setPlaylist(favelist, id.isEmpty() ? favelist.get(0).getId() : id);

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
				_dispatcher.execute(_restServiceFactory.songs().getSong(event.songID()), new AsyncCallback<FaveItem>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(FaveItem song) {
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

	/**
	 * Resizes the playlist to match the height of the song container
	 */
	private void resizePlaylist() {
		resizePlaylistAfterDelay(50);
		resizePlaylistAfterDelay(150);
		resizePlaylistAfterDelay(350);
		resizePlaylistAfterDelay(500);
		resizePlaylistAfterDelay(900);
		resizePlaylistAfterDelay(1600);
	}

	private void resizePlaylistAfterDelay(int delay) {
		final Timer timer = new Timer() {
			@Override
			public void run() {
				final int newHeight = getView().getSongContainerHeight() - 2;
				playlistPresenter.setHeight(newHeight);
			}
		};
		timer.schedule(delay);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(YOUTUBE_SLOT, youtubePresenter);
		setInSlot(WHYLINE_SLOT, whylinePresenter);
		setInSlot(PLAYLIST_SLOT, playlistPresenter);
	}

	@Override
	protected void onHide() {
		super.onHide();
		_requestedAppUser = null;
		youtubePresenter.clearVideo();
	}

	private void updateYouTube() {
		whylinePresenter.showWhylines(songProxy);
		getView().setSongInfo(songProxy);

		// Get any YouTube videos
		_dispatcher.execute(_restServiceFactory.search().getYouTubeResults(songProxy.getSong(), songProxy.getArtist()), new AsyncCallback<YouTubeSearchResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(YouTubeSearchResultCollection result) {
				youtubePresenter.setYouTubeVideos(result.getItems());
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

		if (!_currentUser.isLoggedIn()) {
			_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
		}
		else {
			if (_currentUser.getHashtags().size() == 1) {
				_currentUser.addSong(songProxy.getId(), songProxy.getSong(), songProxy.getArtist());
			}
			else {
				addSongPresenter.setSongToAddId(songProxy.getId());
				addSongPresenter.setSongToAddName(songProxy.getSong());
				addSongPresenter.setSongToAddArtist(songProxy.getArtist());
				addToPopupSlot(addSongPresenter);
			}
		}
	}
}

interface SongUiHandlers extends UiHandlers {
	void addSong();
}
