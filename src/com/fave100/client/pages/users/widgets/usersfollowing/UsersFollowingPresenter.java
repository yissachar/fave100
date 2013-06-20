package com.fave100.client.pages.users.widgets.usersfollowing;

import java.util.ArrayList;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class UsersFollowingPresenter extends PresenterWidget<UsersFollowingPresenter.MyView> {

	public interface MyView extends View {
		void setStarredLists(List<FlowPanel> lists);

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
		final List<FlowPanel> listContainer = new ArrayList<FlowPanel>();
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
						final FlowPanel listItem = new FlowPanel();
						final Image avatar = new Image(user.getAvatarImage());
						listItem.add(avatar);
						final Anchor listAnchor = new Anchor(user.getUsername());
						listAnchor.setHref("#" + new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, user.getUsername()).getPlaceToken().toString());
						listAnchor.addStyleName(getView().getStyle().listLink());
						listItem.add(listAnchor);
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
						listContainer.add(listItem);
					}
				}
				getView().setStarredLists(listContainer);
			}

		};
		if (_currentUser.isLoggedIn())
			_requestCache.getFollowingUsers(followingReq);
	}
}
