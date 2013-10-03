package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> implements GlobalListDetailsUiHandlers {

	public interface MyView extends View, HasUiHandlers<GlobalListDetailsUiHandlers> {

		void setInfo(String hashtag);

		void setTrendingLists(List<String> lists);

		void hideContributeCTA();

		void showContributeCTA();

		void show();

		void hide();

	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LIST_AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();

	private ApplicationRequestFactory _requestFactory;
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private String _hashtag;
	@Inject ListAutocompletePresenter listAutocomplete;

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view, ApplicationRequestFactory requestFactory, final CurrentUser currentUser, final PlaceManager placeManager) {
		super(eventBus, view);
		_requestFactory = requestFactory;
		_currentUser = currentUser;
		_placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	public void setHashtag(final String hashtag) {
		_hashtag = hashtag;
		getView().setInfo(hashtag);
		final Request<List<String>> getTrendingReq = _requestFactory.faveListRequest().getTrendingFaveLists();
		getTrendingReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(List<String> lists) {
				getView().setTrendingLists(lists);
			}
		});

		// Hide contribute if user already has it
		if (_currentUser.isLoggedIn() && (_currentUser.getHashtags().contains(hashtag) || hashtag.equals(Constants.DEFAULT_HASHTAG)))
			getView().hideContributeCTA();
		else
			getView().showContributeCTA();
	}

	@Override
	public void onReveal() {
		super.onReveal();
		setInSlot(LIST_AUTOCOMPLETE_SLOT, listAutocomplete);
	}

	@Override
	public void contributeToList() {
		if (_currentUser.isLoggedIn()) {
			_currentUser.addFaveList(_hashtag);
		}
		else {
			_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
		}

	}
}

interface GlobalListDetailsUiHandlers extends UiHandlers {

	void contributeToList();

}
