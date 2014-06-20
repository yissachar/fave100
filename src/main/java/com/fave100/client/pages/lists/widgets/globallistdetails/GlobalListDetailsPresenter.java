package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.StringResultCollection;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> implements GlobalListDetailsUiHandlers {

	public interface MyView extends View, HasUiHandlers<GlobalListDetailsUiHandlers> {

		void setTrendingLists(String hashtag, List<String> lists);

		void show();

		void hide();

	}

	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private String _hashtag;

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view, final CurrentUser currentUser, final PlaceManager placeManager,
										final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view);
		_currentUser = currentUser;
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
		getView().setUiHandlers(this);
	}

	@Override
	public void onReveal() {
		super.onReveal();
		render();
	}

	private void render() {
		_dispatcher.execute(_restServiceFactory.trending().getTrendingFaveLists(), new AsyncCallback<StringResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(StringResultCollection result) {
				List<String> trending = new ArrayList<>();
				for (StringResult stringResult : result.getItems()) {
					trending.add(stringResult.getValue());
				}
				getView().setTrendingLists(_hashtag, trending);
			}
		});
	}

	@Override
	public void contributeToList() {
		if (_currentUser.isLoggedIn()) {
			// If user already has that list, switch to it
			if (_currentUser.getHashtags().contains(_hashtag) || _hashtag.equals(Constants.DEFAULT_HASHTAG)) {
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.USER_PARAM, _currentUser.getUsername())
						.with(PlaceParams.LIST_PARAM, _hashtag)
						.build());
			}
			// Otherwise create the list for them
			else {
				_currentUser.addFaveList(_hashtag);
			}
		}
		else {
			_currentUser.setAfterLoginAction(new AddListAfterLoginAction(this, _hashtag));
			_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
		}
	}

	public void setHashtag(final String hashtag) {
		_hashtag = hashtag;
		render();
	}

}

interface GlobalListDetailsUiHandlers extends UiHandlers {

	void contributeToList();

}
