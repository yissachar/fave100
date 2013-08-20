package com.fave100.client.pages.users.widgets.listmanager;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.Validator;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class ListManagerPresenter extends
		PresenterWidget<ListManagerPresenter.MyView>
		implements ListManagerUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListManagerUiHandlers> {
		void refreshList(List<FlowPanel> panels);

		void showError(String msg);

		void hideError();
	}

	private ApplicationRequestFactory _requestFactory;
	private AppUserProxy _user;
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;

	@Inject
	public ListManagerPresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory, final CurrentUser currentUser, final PlaceManager placeManager) {
		super(eventBus, view);
		view.setUiHandlers(ListManagerPresenter.this);
		_requestFactory = requestFactory;
		_currentUser = currentUser;
		_placeManager = placeManager;
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void addHashtag(final String name) {
		if (name.isEmpty())
			return;

		final String error = Validator.validateHashtag(name);
		if (error != null) {
			getView().showError(error);
			return;
		}

		final Request<Void> addFavelistReq = _requestFactory.faveListRequest().addFaveListForCurrentUser(name);
		addFavelistReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				_currentUser.addHashtag(name);
				refreshList();
				getView().hideError();
				// Switch to new hashtag page
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.users)
						.with(UsersPresenter.USER_PARAM, _currentUser.getUsername())
						.with(UsersPresenter.LIST_PARAM, name)
						.build());
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				getView().showError(failure.getMessage());
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