package com.fave100.client.pages.users.widgets.usersfollowing;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.RequestCache;
import com.fave100.client.events.UserFollowedEvent;
import com.fave100.client.events.UserUnfollowedEvent;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.pages.users.widgets.usersfollowing.UsersFollowingView.UsersFollowingStyle;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class UsersFollowingPresenter extends PresenterWidget<UsersFollowingPresenter.MyView> {

	public interface MyView extends View {
		void setStarredLists(FlowPanel lists);

		UsersFollowingStyle getStyle();
	}

	EventBus _eventBus;
	CurrentUser _currentUser;
	ApplicationRequestFactory _requestFactory;
	RequestCache _requestCache;

	@Inject
	public UsersFollowingPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final ApplicationRequestFactory requestFactory,
									final RequestCache requestCache) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_requestFactory = requestFactory;
		_requestCache = requestCache;
	}

	@Override
	protected void onBind() {
		super.onBind();

		UserFollowedEvent.register(_eventBus, new UserFollowedEvent.Handler() {
			@Override
			public void onUserFollowed(final UserFollowedEvent event) {
				refreshLists();
			}
		});

		UserUnfollowedEvent.register(_eventBus, new UserUnfollowedEvent.Handler() {
			@Override
			public void onUserUnfollowed(final UserUnfollowedEvent event) {
				refreshLists();
			}
		});
	}

	public void refreshLists() {
		final FlowPanel listContainer = new FlowPanel();
		final AsyncCallback<List<AppUserProxy>> followingReq = new AsyncCallback<List<AppUserProxy>>() {
			@Override
			public void onFailure(final Throwable caught) {
				// Don't care
			}

			@Override
			public void onSuccess(final List<AppUserProxy> usersFollowing) {
				if (usersFollowing != null && usersFollowing.size() > 0) {
					for (final AppUserProxy user : usersFollowing) {
						// Build list
						final Image avatar = new Image(user.getAvatarImage());
						listContainer.add(avatar);
						final Anchor listAnchor = new Anchor(user.getUsername() + "'s ");
						listAnchor.setHref("#" + new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, user.getUsername()).getPlaceToken().toString());
						listAnchor.addStyleName(getView().getStyle().listLink());
						listContainer.add(listAnchor);
					}
				}
				getView().setStarredLists(listContainer);
			}

		};
		if (_currentUser.isLoggedIn())
			_requestCache.getFollowingUsers(followingReq);
	}
}
