package com.fave100.client.pages.song;

import java.util.List;

import com.fave100.client.Notification;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.PlaylistSongChangedEvent;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.song.widgets.playlist.PlaylistPresenter;
import com.fave100.client.pages.song.widgets.youtube.YouTubePresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.fave100.shared.requestfactory.FaveListRequest;
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
import com.google.web.bindery.requestfactory.shared.ServerFailure;
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
		void setSongInfo(SongProxy song);

		void setPlaylist(Boolean visible);

		void showPlaylist();

		void showWhylines();

		void setWhylineHeight(int px);

		void scrollYouTubeIntoView();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.song)
	public interface MyProxy extends ProxyPlace<SongPresenter> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> YOUTUBE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> PLAYLIST_SLOT = new Type<RevealContentHandler<?>>();

	public static final String ID_PARAM = "id";
	public static final String USER_PARAM = "user";

	private final ApplicationRequestFactory _requestFactory;
	private final PlaceManager _placeManager;
	private final EventBus _eventBus;
	private SongProxy songProxy;
	@Inject YouTubePresenter youtubePresenter;
	@Inject PlaylistPresenter playlistPresenter;

	@Inject
	public SongPresenter(final EventBus eventBus, final MyView view,
							final MyProxy proxy,
							final ApplicationRequestFactory requestFactory,
							final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		_eventBus = eventBus;
		_requestFactory = requestFactory;
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
		if (id.isEmpty()) {
			// Malformed request, send the user away
			_placeManager.revealDefaultPlace();
		}
		else {
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

			// By default, hide playlist
			getView().setPlaylist(false);
			getView().showWhylines();

			// If there is a user, get their info and their playlist
			if (!username.isEmpty()) {
				// Get username and avatar
				final Request<AppUserProxy> getUserReq = _requestFactory.appUserRequest().findAppUser(username);
				getUserReq.fire(new Receiver<AppUserProxy>() {
					@Override
					public void onSuccess(final AppUserProxy user) {
						if (user != null) {
							playlistPresenter.setUserInfo(user.getUsername(), user.getAvatarImage());
						}
					}
				});

				// Get playlist
				final Request<List<FaveItemProxy>> getFavelistReq = _requestFactory.faveListRequest().getFaveList(username, Constants.DEFAULT_HASHTAG);
				getFavelistReq.fire(new Receiver<List<FaveItemProxy>>() {
					@Override
					public void onSuccess(final List<FaveItemProxy> favelist) {
						// Only show playlist if good username
						if (favelist != null) {
							getView().setPlaylist(true);
							playlistPresenter.setPlaylist(favelist, id);
							getView().showPlaylist();
						}
					}
				});
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
		youtubePresenter.clearVideo();
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

	// TODO: Merge this method with FavelistPresenter.addSong()
	@Override
	public void addSong() {

		// Make sure we actually have a song to work with
		if (songProxy == null)
			return;

		// Add the song as a FaveItem
		final FaveListRequest faveListRequest = _requestFactory
				.faveListRequest();
		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(
				Constants.DEFAULT_HASHTAG, songProxy.getId());

		addReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Added");
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				// Alert the user if adding the song fails for any reason
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					_eventBus.fireEvent(new CurrentUserChangedEvent(null));
					_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
				}
				else if (failure.getExceptionType().equals(
						SongLimitReachedException.class.getName())) {
					Notification
							.show("You cannot have more than 100 songs in list");
				}
				else if (failure.getExceptionType().equals(
						SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
				else {
					// Catch-all
					Notification.show("Error: Could not add song");
				}
			}
		});

	}
}

interface SongUiHandlers extends UiHandlers {
	void addSong();
}
