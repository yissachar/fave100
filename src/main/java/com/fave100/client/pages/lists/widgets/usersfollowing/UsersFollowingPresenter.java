package com.fave100.client.pages.lists.widgets.usersfollowing;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.RequestCache;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FollowingResult;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingView.UsersFollowingStyle;
import com.fave100.client.widgets.Icon;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.shared.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class UsersFollowingPresenter extends PresenterWidget<UsersFollowingPresenter.MyView>
		implements UsersFollowingUiHandlers {

	public interface MyView extends View, HasUiHandlers<UsersFollowingUiHandlers> {
		void setFollowing(List<FlowPanel> lists);

		void addFollowing(List<FlowPanel> lists);

		void hideMoreFollowingButton();

		UsersFollowingStyle getStyle();

		void show();

		void hide();
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> USER_AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();

	EventBus _eventBus;
	CurrentUser _currentUser;
	RequestCache _requestCache;
	AppUser _user;
	private FaveApi _api;
	private ParameterTokenFormatter _parameterTokenFormatter;
	int listSize = 0;

	@Inject
	public UsersFollowingPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final RequestCache requestCache,
									final FaveApi api, ParameterTokenFormatter parameterTokenFormatter) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_requestCache = requestCache;
		_api = api;
		_parameterTokenFormatter = parameterTokenFormatter;
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

	public void setUser(final AppUser user) {
		_user = user;
	}

	public void refreshLists() {
		// First clear the lists
		getView().setFollowing(null);
		listSize = 0;

		boolean ownFollowing = _currentUser.isLoggedIn() && _user.getUsername().equals(_currentUser.getUsername());

		if (ownFollowing) {
			// If we already have the current user list, display it
			if (_currentUser.getFollowing() != null && _currentUser.getFollowing().size() > 0) {
				buildListItems(true, _currentUser.getFollowing());
				if (_currentUser.isFullListRetrieved()) {
					getView().hideMoreFollowingButton();
				}
			}
			// Otherwise fetch it
			else {
				final AsyncCallback<FollowingResult> followingReq = new AsyncCallback<FollowingResult>() {
					@Override
					public void onFailure(final Throwable caught) {
						// Don't care
					}

					@Override
					public void onSuccess(final FollowingResult followingResult) {
						final List<AppUser> usersFollowing = followingResult.getFollowing();
						buildListItems(true, usersFollowing);
						_currentUser.setFullListRetrieved(!followingResult.isMore());
						if (!followingResult.isMore()) {
							getView().hideMoreFollowingButton();
						}
					}

				};
				_requestCache.getFollowingForCurrentUser(_currentUser.getUsername(), followingReq);
			}
		}
		else {
			_api.call(_api.service().users().getFollowing(_user.getUsername(), 0), new AsyncCallback<FollowingResult>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().setFollowing(null);
				}

				@Override
				public void onSuccess(FollowingResult followingResult) {
					buildListItems(false, followingResult.getFollowing());
					if (!followingResult.isMore())
						getView().hideMoreFollowingButton();
				}
			});

		}

	}

	public void clearLists() {
		buildListItems(false, new ArrayList<AppUser>());
	}

	private void buildListItems(final boolean ownFollowing, final List<AppUser> usersFollowing) {
		buildListItems(ownFollowing, usersFollowing, true);
	}

	private void buildListItems(final boolean ownFollowing, final List<AppUser> usersFollowing, final boolean reset) {
		final List<FlowPanel> listContainer = new ArrayList<FlowPanel>();
		if (usersFollowing != null && usersFollowing.size() > 0) {
			for (final AppUser user : usersFollowing) {
				// Build list
				final FlowPanel listItem = new FlowPanel();
				final Image avatar = new Image(user.getAvatarImage());
				listItem.add(avatar);
				final Anchor listAnchor = new Anchor(user.getUsername());
				listAnchor.setHref("#" + _parameterTokenFormatter
						.toPlaceToken(new PlaceRequest.Builder()
								.nameToken(NameTokens.lists)
								.with(PlaceParams.USER_PARAM, user.getUsername())
								.build()));
				listAnchor.addStyleName(getView().getStyle().listLink());
				listItem.add(listAnchor);
				if (ownFollowing) {
					final Icon deleteButton = new Icon("fa-times");
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
		_api.call(_api.service().users().getFollowing(_user.getUsername(), listSize), new AsyncCallback<FollowingResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(FollowingResult followingResult) {
				final List<AppUser> users = followingResult.getFollowing();

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
