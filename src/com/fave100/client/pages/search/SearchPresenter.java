package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class SearchPresenter extends
		BasePresenter<SearchPresenter.MyView, SearchPresenter.MyProxy>
		implements SearchUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<SearchUiHandlers> {
		void resetView();
		int getPageNum();
		void setResultCount(int count);
		void setResults(List<SongProxy> resultList);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.search)
	public interface MyProxy extends ProxyPlace<SearchPresenter> {
	}

	public static final int RESULTS_PER_PAGE = 25;

	private ApplicationRequestFactory requestFactory;

	@Inject
	public SearchPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onHide() {
		// Set the result list to be blank
		getView().resetView();
	}

	// TODO: need a global "loading" indicator
	@Override
	public void showResults(final String songTerm, final String artistTerm) {

		final int offset = RESULTS_PER_PAGE*(getView().getPageNum()-1);
		Request<List<SongProxy>> searchReq;
		if(!songTerm.isEmpty()) {
			if(!artistTerm.isEmpty()) {
				searchReq = requestFactory.songRequest().search(songTerm, artistTerm, offset);
			} else {
				searchReq = requestFactory.songRequest().searchSong(songTerm, offset);
			}
		} else if(!artistTerm.isEmpty()) {
			searchReq = requestFactory.songRequest().searchArtist(artistTerm, offset);
		} else {
			return;
		}

		searchReq.fire(new Receiver<List<SongProxy>>() {
			@Override
			public void onSuccess(final List<SongProxy> resultList) {
				getView().setResults(resultList);
				if(resultList.size() > 0) {
					getView().setResultCount(resultList.get(0).getResultCount());
				} else {
					// No result, set to 0
					getView().setResultCount(0);
				}
			}
		});
	}
}

interface SearchUiHandlers extends UiHandlers{
	void showResults(String songTerm, String artistTerm);
}
