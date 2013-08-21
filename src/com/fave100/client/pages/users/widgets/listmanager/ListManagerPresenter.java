package com.fave100.client.pages.users.widgets.listmanager;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
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
		void refreshList(List<String> lists, String selected);

		void showError(String msg);

		void hideError();

		void setOwnList(boolean ownList);
	}

	private ApplicationRequestFactory _requestFactory;
	private AppUserProxy _user;
	private String _hashtag;
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
				listChanged(name);
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				getView().showError(failure.getMessage());
			}
		});
	}

	public void refreshList() {
		final List<String> hashtags = new ArrayList<String>();
		hashtags.add(Constants.DEFAULT_HASHTAG);
		hashtags.addAll(_user.getHashtags());
		getView().refreshList(hashtags, _hashtag);
		getView().setOwnList(_currentUser != null && _currentUser.equals(_user));
	}

	@Override
	public void listChanged(final String list) {
		if (_hashtag.equals(list))
			return;

		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.users)
				.with(UsersPresenter.USER_PARAM, _user.getUsername())
				.with(UsersPresenter.LIST_PARAM, list)
				.build());
	}

	@Override
	public void setGlobalList(final boolean global) {

	}

	/* Getters and Setters */

	public AppUserProxy getUser() {
		return _user;
	}

	public void setUser(final AppUserProxy user) {
		this._user = user;
	}

	public String getHashtag() {
		return _hashtag;
	}

	public void setHashtag(final String _hashtag) {
		this._hashtag = _hashtag;
	}
}

interface ListManagerUiHandlers extends UiHandlers {

	void addHashtag(String name);

	void listChanged(String list);

	void setGlobalList(boolean global);
}