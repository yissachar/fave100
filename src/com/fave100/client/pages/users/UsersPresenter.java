package com.fave100.client.pages.users;

import java.util.List;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.pagefragments.favefeed.FaveFeedPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.search.MusicbrainzResult;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.requestfactory.SongRequest;
import com.fave100.server.domain.FaveList;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.following.AlreadyFollowingException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
		void setFollowed();
		void setUnfollowed();
		void showFave100Tab();
		void setActivityTab(SafeHtml html);
		void showActivityTab();
		void setUserProfile(AppUserProxy user);
		void setUserFaveList(List<SongProxy> faveList);
		void refreshPersonalFaveList();
		void showOwnPage();
		void showOtherPage();
	}

	public static final String FAVE_100_TAB = "fave100";
	public static final String ACTIVITY_TAB = "activity";

	private int currentRequestProgress = 0;
	private String requestedUsername;
	private final ApplicationRequestFactory requestFactory;
	private final PlaceManager placeManager;
	private boolean ownPage = false;
	private boolean following = false;
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
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {
	    super.onReveal();
	    setInSlot(FAVE_FEED_SLOT, faveFeed);
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
			final Request<Boolean> checkFollowing = requestFactory.appUserRequest().checkFollowing(requestedUsername);
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
			});

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


		    tab = placeRequest.getParameter("tab", UsersPresenter.FAVE_100_TAB);
		    if(tab.equals(UsersPresenter.ACTIVITY_TAB)) {

				final Request<List<String>> getActivityReq = requestFactory.appUserRequest().getActivityForUser(requestedUsername);
				getActivityReq.fire(new Receiver<List<String>>() {
					@Override
					public void onSuccess(final List<String> activityList) {
						final SafeHtmlBuilder builder = new SafeHtmlBuilder();
						builder.appendHtmlConstant("<ul>");
						for(final String activity : activityList) {
							builder.appendHtmlConstant("<li>");
							builder.appendEscaped(activity);
							builder.appendHtmlConstant("</li>");
						}
						builder.appendHtmlConstant("</ul>");
						getView().setActivityTab(builder.toSafeHtml());
						checkTotalRequestProgress();
					}
				});
		    } else if(tab.equals(UsersPresenter.FAVE_100_TAB)) {
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
	}

	private void checkTotalRequestProgress() {
		currentRequestProgress++;
		if(currentRequestProgress >= 4) {
			currentRequestProgress = 0;

			if(following) {
				getView().setFollowed();
			} else {
				getView().setUnfollowed();
			}

			if(ownPage) {
				getView().showOwnPage();
			} else {
				getView().showOtherPage();
			}

			if(tab.equals(UsersPresenter.FAVE_100_TAB)) {
				getView().showFave100Tab();
			} else if(tab.equals(UsersPresenter.ACTIVITY_TAB)) {
				getView().showActivityTab();
			}
			getProxy().manualReveal(UsersPresenter.this);
		}
	}

	@Override
	public void follow() {
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<Void> followReq = appUserRequest.followUser(requestedUsername);
		followReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				SideNotification.show("Following!");
				getView().setFollowed();
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				if(failure.getExceptionType().equals(AlreadyFollowingException.class.getName())) {
					SideNotification.show("You are already following this user", true);
				}
			}
		});
	}

	@Override
	public void goToFave100Tab() {
		if(!tab.equals(UsersPresenter.FAVE_100_TAB)) {
			placeManager.revealPlace(new PlaceRequest(NameTokens.users).with("u", requestedUsername).with("tab", UsersPresenter.FAVE_100_TAB));
		}
	}

	@Override
	public void goToActivityTab() {
		if(!tab.equals(UsersPresenter.ACTIVITY_TAB)) {
			placeManager.revealPlace(new PlaceRequest(NameTokens.users).with("u", requestedUsername).with("tab", UsersPresenter.ACTIVITY_TAB));
		}
	}

	@Override
	public void addSong(final MusicbrainzResult faveItemMap) {

		final FaveListRequest faveListRequest = requestFactory.faveListRequest();
		final SongRequest songRequest = faveListRequest.append(requestFactory.songRequest());

		// Add the MBID as a FaveItem
		final Request<Void> createReq = faveListRequest.addFaveItemForCurrentUser(FaveList.DEFAULT_HASHTAG,
				faveItemMap.getMbid(), faveItemMap.getTrackName(), faveItemMap.getArtistName());

		createReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				//getView().getPersonalFaveList().refreshList();
				getView().refreshPersonalFaveList();
			}
			@Override
			public void onFailure(final ServerFailure failure) {
				if(failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					placeManager.revealPlace(new PlaceRequest(NameTokens.login));
				} else if(failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
					SideNotification.show("You cannot have more than 100 songs in list");
				} else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
					SideNotification.show("The song is already in your list");
				}
			}
		});
	}
}


interface UsersUiHandlers extends UiHandlers{
	void follow();
	void goToFave100Tab();
	void goToActivityTab();
	void addSong(MusicbrainzResult selectedItem);
}
