package com.fave100.client.pages.users;

import java.util.List;

import com.fave100.client.events.SongSelectedEvent;
import com.fave100.client.pagefragments.favefeed.FaveFeedPresenter;
import com.fave100.client.pagefragments.topbar.Notification;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
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

public class UsersPresenter extends
		BasePresenter<UsersPresenter.MyView, UsersPresenter.MyProxy>
		implements UsersUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<UsersUiHandlers> {
		//void setFollowed();
		//void setUnfollowed();
		void setUserProfile(AppUserProxy user);
		void setUserFaveList(List<SongProxy> faveList);
		void refreshPersonalFaveList();
		void showOwnPage();
		void showOtherPage();
	}

	public static final String FAVE_100_TAB = "fave100";
	public static final String ACTIVITY_TAB = "activity";
	public static final String USER_PARAM = "u";

	private int currentRequestProgress = 0;
	private String requestedUsername;
	private final ApplicationRequestFactory requestFactory;
	private final PlaceManager placeManager;
	private final EventBus eventBus;
	private boolean ownPage = false;
	//private boolean following = false;
	private String tab = UsersPresenter.FAVE_100_TAB;

	@ProxyCodeSplit
	@NameToken(NameTokens.users)
	public interface MyProxy extends ProxyPlace<UsersPresenter> {
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> FAVE_FEED_SLOT = new Type<RevealContentHandler<?>>();

	@Inject FaveFeedPresenter faveFeed;

	@Inject
	public UsersPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);


	}

	@Override
	protected void onBind() {
		super.onBind();
		SongSelectedEvent.register(eventBus, new SongSelectedEvent.Handler() {

			@Override
			public void onSongSelected(final SongSelectedEvent event) {
				//Window.alert("Song selected from User page: "+event.getSong().getTrackName());
				final SongProxy song = event.getSong();
				// If we are on Users page add the song, otherwise go to song page
				if(isVisible()) {
					addSong(song);
					// TODO: If the user is not logged in should be following:
					//placeManager.revealPlace(new PlaceRequest(NameTokens.song)
					//.with("song", song.getTrackName()).with("artist", song.getArtistName()));
				}
			}
		});
	}

	@Override
	protected void onReveal() {
	    super.onReveal();
	    //setInSlot(FAVE_FEED_SLOT, faveFeed);
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		// Use parameters to determine what to reveal on page
		requestedUsername = placeRequest.getParameter("u", "");
		if(requestedUsername.isEmpty()) {
			// Malformed request, send the user away
			placeManager.revealDefaultPlace();
		} else {
			// Update follow button
			/*final Request<Boolean> checkFollowing = requestFactory.appUserRequest().checkFollowing(requestedUsername);
			checkFollowing.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean followingUser) {
					if(followingUser) {
						following = true;
					} else {
						following = false;
					}
					checkTotalRequestProgress();
				}
			});*/

			// Update user profile
		    final Request<AppUserProxy> userReq = requestFactory.appUserRequest().findAppUser(requestedUsername);
		    userReq.fire(new Receiver<AppUserProxy>() {
		    	@Override
		    	public void onSuccess(final AppUserProxy user) {
		    		if(user != null) {
	    				getView().setUserProfile(user);
	    				// Check if user is the currently logged in user
	    				final Request<AppUserProxy> getLoggedInReq = requestFactory.appUserRequest().getLoggedInAppUser();
	    				getLoggedInReq.fire(new Receiver<AppUserProxy>() {
	    					@Override
	    					public void onSuccess(final AppUserProxy loggedInUser) {
	    						if(loggedInUser != null && loggedInUser.equals(user)) {
	    							ownPage = true;
	    						} else {
	    							ownPage = false;
	    						}
	    						checkTotalRequestProgress();
	    					}
	    				});
	    				checkTotalRequestProgress();
	    			} else {
	    				placeManager.revealDefaultPlace();
	    			}
		    	}
		    });

	    	// Update fave list
			final Request<List<SongProxy>> userFaveListReq = requestFactory.faveListRequest().getFaveList(requestedUsername, FaveList.DEFAULT_HASHTAG);
		    userFaveListReq.fire(new Receiver<List<SongProxy>>() {
		    	@Override
		    	public void onSuccess(final List<SongProxy> faveList) {
		    		if(faveList != null) {
		    			getView().setUserFaveList(faveList);
	    			} else {
	    				placeManager.revealDefaultPlace();
	    			}
		    		checkTotalRequestProgress();
		    	}
		    });
		}
	}

	private void checkTotalRequestProgress() {
		currentRequestProgress++;
		if(currentRequestProgress >= 3) {
			currentRequestProgress = 0;

			/*if(following) {
				getView().setFollowed();
			} else {
				getView().setUnfollowed();
			}*/

			if(ownPage) {
				getView().showOwnPage();
			} else {
				getView().showOtherPage();
			}

			getProxy().manualReveal(UsersPresenter.this);
		}
	}

	/*@Override
	public void follow() {
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<Void> followReq = appUserRequest.followUser(requestedUsername);
		followReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Following!");
				getView().setFollowed();
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				if(failure.getExceptionType().equals(AlreadyFollowingException.class.getName())) {
					SideNotification.show("You are already following this user", true);
				}
			}
		});
	}*/

	public void addSong(final SongProxy faveItemMap) {

		final FaveListRequest faveListRequest = requestFactory.faveListRequest();

		// Add the song as a FaveItem
		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(FaveList.DEFAULT_HASHTAG,
				faveItemMap.getMbid(), faveItemMap.getTrackName(), faveItemMap.getArtistName());

		addReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Song added");
				getView().refreshPersonalFaveList();
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				if(failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					placeManager.revealPlace(new PlaceRequest(NameTokens.login));
				} else if(failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
					Notification.show("You cannot have more than 100 songs in list");
				} else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
			}
		});
	}
}


interface UsersUiHandlers extends UiHandlers{
	//void follow();
}
