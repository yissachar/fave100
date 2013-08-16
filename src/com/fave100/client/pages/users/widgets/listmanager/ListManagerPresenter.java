package com.fave100.client.pages.users.widgets.listmanager;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

public class ListManagerPresenter extends
		PresenterWidget<ListManagerPresenter.MyView>
		implements ListManagerUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListManagerUiHandlers> {
		void refreshList(List<FlowPanel> panels);
	}

	private ApplicationRequestFactory _requestFactory;
	private AppUserProxy _user;

	@Inject
	public ListManagerPresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		view.setUiHandlers(ListManagerPresenter.this);
		_requestFactory = requestFactory;
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void addHashtag(final String name) {
		final Request<Void> addFavelistReq = _requestFactory.faveListRequest().addFaveListForCurrentUser(name);
		addFavelistReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				// TODO: Update list
			}
		});
	}

	public void refreshList() {
		final List<FlowPanel> panels = new ArrayList<FlowPanel>();
		final List<String> hashtags = new ArrayList<String>();
		hashtags.addAll(_user.getHashtags());
		hashtags.add(Constants.DEFAULT_HASHTAG);
		for (final String hashtag : hashtags) {
			final FlowPanel container = new FlowPanel();
			final Anchor listLink = new Anchor(hashtag);
			listLink.setHref(new UrlBuilder(NameTokens.users)
					.with(UsersPresenter.USER_PARAM, _user.getUsername())
					.with(UsersPresenter.LIST_PARAM, hashtag)
					.getUrl());

			container.add(listLink);
			panels.add(container);
		}
		getView().refreshList(panels);
	}

	/* Getters and Setters */

	public AppUserProxy getUser() {
		return _user;
	}

	public void setUser(final AppUserProxy user) {
		this._user = user;
	}
}

interface ListManagerUiHandlers extends UiHandlers {

	void addHashtag(String name);
}