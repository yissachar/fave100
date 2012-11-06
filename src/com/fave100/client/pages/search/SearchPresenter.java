package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
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
		void setResults(List<MusicbrainzResult> resultList);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.search)
	public interface MyProxy extends ProxyPlace<SearchPresenter> {
	}
	
	public static final String BASE_SEARCH_URL = "http://192.168.214.170:7080/";//"http://musicbrainz.org/ws/2/";
	
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
	
	// TODO: instead of limiting to 25 results, should give paged results?
	// TODO: need a global "loading" indicator
	@Override
	public void showResults(final String songTerm,final String artistTerm) {
		String searchUrl = SearchPresenter.BASE_SEARCH_URL;
		searchUrl += "?limit=25&";
		
		if(!songTerm.isEmpty()) {
			searchUrl += "song="+songTerm;	
		} 
		
		if(!artistTerm.isEmpty()) {
			if(!songTerm.isEmpty()) {
				searchUrl += "&";
			}
			searchUrl += "artist="+artistTerm;
		}
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(searchUrl, new AsyncCallback<AutocompleteJSON>() {
			 
		    @Override
			public void onFailure(final Throwable throwable) {
		    	//Window.alert("Fail!");
		    }

		    @Override
			public void onSuccess(final AutocompleteJSON json) {			
	       		
		    	final MusicbrainzResultList resultList = new MusicbrainzResultList(json);
	            getView().setResults(resultList);
	            
		    }
		});
	}
}

interface SearchUiHandlers extends UiHandlers{
	void showResults(String songTerm, String artistTerm);
}
