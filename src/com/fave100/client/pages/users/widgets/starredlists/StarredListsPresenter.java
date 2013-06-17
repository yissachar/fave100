package com.fave100.client.pages.users.widgets.starredlists;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.ListStarredEvent;
import com.fave100.client.events.ListUnstarredEvent;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.pages.users.widgets.starredlists.StarredListsView.StarredListsStyle;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FavelistIDProxy;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class StarredListsPresenter extends PresenterWidget<StarredListsPresenter.MyView> {

	public interface MyView extends View {
		void setStarredLists(FlowPanel lists);

		StarredListsStyle getStyle();
	}

	EventBus _eventBus;
	CurrentUser _currentUser;
	ApplicationRequestFactory _requestFactory;

	@Inject
	public StarredListsPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_requestFactory = requestFactory;
	}

	@Override
	protected void onBind() {
		super.onBind();

		ListStarredEvent.register(_eventBus, new ListStarredEvent.Handler() {
			@Override
			public void onListStarred(final ListStarredEvent event) {
				refreshLists();
			}
		});

		ListUnstarredEvent.register(_eventBus, new ListUnstarredEvent.Handler() {
			@Override
			public void onListStarred(final ListUnstarredEvent event) {
				refreshLists();
			}
		});
	}

	public void refreshLists() {
		final FlowPanel listContainer = new FlowPanel();
		final List<FavelistIDProxy> starredLists = _currentUser.getStarredLists();

		if (starredLists != null && starredLists.size() > 0) {
			for (final FavelistIDProxy starredList : starredLists) {
				// Build list
				final Anchor listAnchor = new Anchor(starredList.getUsername() + "'s " + starredList.getHashtag());
				listAnchor.setHref("#" + new UrlBuilder(NameTokens.users).with(UsersPresenter.USER_PARAM, starredList.getUsername()).getPlaceToken().toString());
				listAnchor.addStyleName(getView().getStyle().listLink());
				listContainer.add(listAnchor);
			}
		}
		getView().setStarredLists(listContainer);

	}
}
