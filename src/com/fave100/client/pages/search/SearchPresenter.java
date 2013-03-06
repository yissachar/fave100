package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.Notification;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveListRequest;
import com.fave100.shared.requestfactory.SearchResultProxy;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class SearchPresenter extends
		BasePresenter<SearchPresenter.MyView, SearchPresenter.MyProxy>
		implements SearchUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<SearchUiHandlers> {
		void resetView();

		int getPageNum();

		void setResultCount(int count);

		void setResults(List<SongProxy> resultList);

		void populateSearchFields(String song);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.search)
	public interface MyProxy extends ProxyPlace<SearchPresenter> {
	}

	public static final int				RESULTS_PER_PAGE	= 25;
	private ApplicationRequestFactory	requestFactory;
	private PlaceManager				placeManager;

	@Inject
	public SearchPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy,
			final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onHide() {
		super.onHide();
		// Set the result list to be blank
		getView().resetView();
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	public interface SearchResultFactory extends AutoBeanFactory {
		AutoBean<SearchResultProxy> response();
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		// Use parameters to determine what to search for
		final String searchTerm = URL.decode(placeRequest.getParameter("searchTerm", ""));

		// TODO: need a global "loading" indicator
		// Build the search request
		final String url = Constants.SEARCH_URL+"searchTerm="+searchTerm+"&limit=25&page="+(getView().getPageNum() - 1);

		// Clear any old results
		getView().setResults(null);
		getView().setResultCount(0);

		// Search for the song
		final AsyncCallback<JavaScriptObject> autocompleteReq = new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(final Throwable caught) {
				//TODO: error catching
			}

			@Override
			public void onSuccess(final JavaScriptObject jsObject) {
				// Turn the resulting JavaScriptObject into an AutoBean
				final JSONObject obj = new JSONObject(jsObject);
				final SearchResultFactory factory = GWT.create(SearchResultFactory.class);
				final AutoBean<SearchResultProxy> autoBean = AutoBeanCodex.decode(factory, SearchResultProxy.class, obj.toString());

				getView().setResults(autoBean.as().getResults());
				getView().setResultCount(autoBean.as().getTotal());

				getView().populateSearchFields(searchTerm);

				// Show page
				getProxy().manualReveal(SearchPresenter.this);
			}
		};

		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, autocompleteReq);
	}

	@Override
	public void showResults(final String searchTerm) {
		final PlaceRequest placeRequest = new PlaceRequest(NameTokens.search)
			.with("searchTerm", searchTerm);
		placeManager.revealPlace(placeRequest);
	}

	@Override
	public void addSong(final SongProxy song) {
		final FaveListRequest faveListRequest = requestFactory
				.faveListRequest();

		final String hashtag = Constants.DEFAULT_HASHTAG;

		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(
				hashtag, song.getSong(), song.getArtist());

		addReq.fire(new Receiver<Void>() {

			@Override
			public void onSuccess(final Void response) {
				Notification.show("Added");
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(
						NotLoggedInException.class.getName())) {
					placeManager
							.revealPlace(new PlaceRequest(NameTokens.login));
				} else if (failure.getExceptionType().equals(
						SongLimitReachedException.class.getName())) {
					Notification
							.show("You cannot have more than 100 songs in list");
				} else if (failure.getExceptionType().equals(
						SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
			}

		});
	}
}