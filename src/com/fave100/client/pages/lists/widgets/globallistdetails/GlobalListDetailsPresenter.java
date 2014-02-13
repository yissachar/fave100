package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.generated.entities.StringResultCollection;
import com.fave100.client.generated.entities.StringResultDto;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.pagefragments.login.aboutpopup.AboutPopupPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.rest.RestSessionDispatch;
import com.fave100.shared.Constants;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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

	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private RestSessionDispatch _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private String _hashtag;
	@Inject GlobalListAutocompletePresenter listAutocomplete;
	@Inject AboutPopupPresenter aboutPresenter;

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final PlaceManager placeManager,
										final RestSessionDispatch dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view);
		_currentUser = currentUser;
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
		getView().setUiHandlers(this);
	}

	public void setHashtag(final String hashtag) {
		_hashtag = hashtag;
		getView().setInfo(hashtag);
		_dispatcher.execute(_restServiceFactory.getFavelistService().getTrendingFaveLists(), new AsyncCallback<StringResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(StringResultCollection result) {
				List<String> trending = new ArrayList<>();
				for (StringResultDto stringResult : result.getItems()) {
					trending.add(stringResult.getValue());
				}
				getView().setTrendingLists(trending);
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

	@Override
	public void showAbout() {
		addToPopupSlot(aboutPresenter);
	}
}

interface GlobalListDetailsUiHandlers extends UiHandlers {

	void contributeToList();

	void showAbout();

}
