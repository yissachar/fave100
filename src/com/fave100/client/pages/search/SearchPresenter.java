package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
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
	
	public static final String BASE_SEARCH_URL = "http://musicbrainz.org/ws/2/";
	
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
	public void showResults(final String songTerm, final Boolean songExactly, final String artistTerm,
			final Boolean artistExactly, final String albumTerm, final Boolean albumExactly ) {
		String searchUrl = SearchPresenter.BASE_SEARCH_URL;
		searchUrl += "recording/?query=";
		
		if(!songTerm.isEmpty()) {
			searchUrl += "recording:";
			if(songExactly) {
				searchUrl += '"'+songTerm+'"';  
			} else {
				searchUrl += songTerm;
			}			
		} 
		if(!artistTerm.isEmpty()) {
			searchUrl += "+AND+";
			searchUrl += "artist:";
			if(artistExactly) {
				searchUrl += '"'+artistTerm+'"';  
			} else {
				searchUrl += artistTerm;
			}			
		}
		if(!albumTerm.isEmpty()) {
			searchUrl += "+AND+";
			searchUrl += "release:";
			if(albumExactly) {
				searchUrl += '"'+albumTerm+'"';  
			} else {
				searchUrl += albumTerm;
			}			
		}
		
		// Get the search results from Musicbrainz
		final XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("GET", searchUrl);
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler()	{
		    @Override
		    public void onReadyStateChange(final XMLHttpRequest xhr2) {
		         if( xhr2.getReadyState() == XMLHttpRequest.DONE ) {
		             xhr2.clearOnReadyStateChange();
		             final MusicbrainzResultList resultList = new MusicbrainzResultList(xhr2.getResponseText());
		             getView().setResults(resultList);
		         }
		    }
		});
		xhr.send();
	}
}

interface SearchUiHandlers extends UiHandlers{
	void showResults(String songTerm, Boolean songExactly, String artistTerm,
			Boolean artistExactly, String albumTerm, Boolean albumExactly);
}
