package com.fave100.client.pages.lists.widgets.usersfollowing;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.RequestCache;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.generated.entities.AppUserDto;
import com.fave100.client.generated.entities.FollowingResultDto;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingView.UsersFollowingStyle;
import com.fave100.client.place.NameTokens;
import com.fave100.client.rest.RestSessionDispatch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class UsersFollowingPresenter extends PresenterWidget<UsersFollowingPresenter.MyView>
		implements UsersFollowingUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<UsersFollowingUiHandlers> {
		void setFollowing(List<FlowPanel> lists);

		void addFollowing(List<FlowPanel> lists);

		void hideMoreFollowingButton();

		UsersFollowingStyle getStyle();

		void show();

		void hide();
	}

	EventBus _eventBus;
	CurrentUser _currentUser;
	RequestCache _requestCache;
	AppUserDto _user;
	private RestSessionDispatch _dispatcher;
	private RestServiceFactory _restServiceFactory;
	int listSize = 0;

	@Inject
	public UsersFollowingPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final RequestCache requestCache,
									final RestSessionDispatch dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_requestCache = requestCache;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		UserFollowedEvent.register(_eventBus, new UserFollowedEvent.Handler() {
			@Override
			public void onUserFollowed(final UserFollowedEvent event) {
				if (_user.getUsername().equals(_currentUser.getUsername()))
					refreshLists();
			}
		});

		UserUnfollowedEvent.register(_eventBus, new UserUnfollowedEvent.Handler() {
			@Override
			public void onUserUnfollowed(final UserUnfollowedEvent event) {
				if (_user.getUsername().equals(_currentUser.getUsername()))
					refreshLists();
			}
		});
	}

	public void setUser(final AppUserDto user) {
		_user = user;
	}

	public void refreshLists() {
		// First clear the lists
		getView().setFollowing(null);
		listSize = 0;

		boolean ownFollowing = false;
		if (_currentUser.isLoggedIn())
			ownFollowing = _user.getUsername().equals(_currentUser.getUsername());
		if (ownFollowing) {
			// If we already have the current user list, display it
			if (_currentUser.getFollowing() != null && _currentUser.getFollowing().size() > 0) {
				buildListItems(true, _currentUser.getFollowing());
				if (_currentUser.isFullListRetrieved())
					getView().hideMoreFollowingButton();
			}
			// Otherwise fetch it
			else {
				final AsyncCallback<FollowingResultDto> followingReq = new AsyncCallback<FollowingResultDto>() {
					@Override
					public void onFailure(final Throwable caught) {
						// Don't care
					}

					@Override
					public void onSuccess(final FollowingResultDto followingResult) {
						final List<AppUserDto> usersFollowing = followingResult.getFollowing();
						buildListItems(true, usersFollowing);
						_currentUser.setFullListRetrieved(!followingResult.isMore());
						if (!followingResult.isMore())
							getView().hideMoreFollowingButton();
					}

				};
				_requestCache.getFollowingForCurrentUser(_currentUser.getUsername(), followingReq);
			}
		}
		else {
			_dispatcher.execute(_restServiceFactory.appuser().getFollowing(_user.getUsername(), 0), new AsyncCallback<FollowingResultDto>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().setFollowing(null);
				}

				@Override
				public void onSuccess(FollowingResultDto followingResult) {
					buildListItems(false, followingResult.getFollowing());
					if (!followingResult.isMore())
						getView().hideMoreFollowingButton();
				}
			});

		}

	}

	public void clearLists() {
		buildListItems(false, new ArrayList<AppUserDto>());
	}

	private void buildListItems(final boolean ownFollowing, final List<AppUserDto> usersFollowing) {
		buildListItems(ownFollowing, usersFollowing, true);
	}

	private void buildListItems(final boolean ownFollowing, final List<AppUserDto> usersFollowing, final boolean reset) {
		final List<FlowPanel> listContainer = new ArrayList<FlowPanel>();
		if (usersFollowing != null && usersFollowing.size() > 0) {
			for (final AppUserDto user : usersFollowing) {
				// Build list
				final FlowPanel listItem = new FlowPanel();
				final Image avatar = new Image(user.getAvatarImage());
				listItem.add(avatar);
				final Anchor listAnchor = new Anchor(user.getUsername());
				listAnchor.setHref("#" + new ParameterTokenFormatter()
						.toPlaceToken(new PlaceRequest.Builder()
								.nameToken(NameTokens.lists)
								.with(ListPresenter.USER_PARAM, user.getUsername())
								.build()));
				listAnchor.addStyleName(getView().getStyle().listLink());
				listItem.add(listAnchor);
				if (ownFollowing) {
					final Label deleteButton = new Label("x");
					listItem.add(deleteButton);
					deleteButton.addStyleName(getView().getStyle().deleteButton());
					deleteButton.addStyleName("hoverHidden");
					deleteButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(final ClickEvent event) {
							_currentUser.unfollowUser(user);
						}
					});
				}
				listContainer.add(listItem);
			}
		}
		if (reset) {
			listSize = listContainer.size();
			getView().setFollowing(listContainer);
		}
		else {
			listSize += listContainer.size();
			getView().addFollowing(listContainer);
		}

	}

	@Override
	public void getMoreFollowing() {
		_dispatcher.execute(_restServiceFactory.appuser().getFollowing(_user.getUsername(), listSize), new AsyncCallback<FollowingResultDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(FollowingResultDto followingResult) {
				final List<AppUserDto> users = followingResult.getFollowing();

				if (!followingResult.isMore())
					getView().hideMoreFollowingButton();

				boolean ownFollowing = false;
				if (_currentUser.isLoggedIn())
					ownFollowing = _user.getUsername().equals(_currentUser.getUsername());

				if (ownFollowing) {
					_currentUser.addMoreFollowing(users, followingResult.isMore());
					buildListItems(true, users, false);
				}
				else {
					buildListItems(false, users, false);
				}
			}
		});
	};
}

interface UsersFollowingUiHandlers extends UiHandlers {
	void getMoreFollowing();
}
