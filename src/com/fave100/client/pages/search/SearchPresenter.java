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

	// TODO: does this really need to be limited to 25?
	// TODO: need a global "loading" indicator
	@Override
	public void showResults(final String searchTerm, final String attribute) {
		String searchUrl = SearchPresenter.BASE_SEARCH_URL;
		// TODO: Implement search by album properly
		if(attribute.equals("songTerm")) {
			searchUrl += "release/?query=release:";
		} else if(attribute.equals("artistTerm")) {
			searchUrl += "release/?query=artist:";
		} else if(attribute.equals("albumTerm")) {
			searchUrl += "release/?type=album&query=release:";
		}
		searchUrl += searchTerm;
		
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
		/*Window.alert("search"+searchTerm+" attribute "+attribute);
		final Request<String> searchReq = requestFactory.songRequest().search(searchTerm, attribute);
		searchReq.fire(new Receiver<String>(){
			@Override
			public void onSuccess(String response) {
				
			}			
		});*/
		/*final String url = "http://itunes.apple.com/search?"+
				"term="+searchTerm+
				"&media=music"+
				"&entity=song"+
				"&attribute="+attribute+
				"&limit=25";
		
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, new AsyncCallback<JavaScriptObject>() {			
		   	@Override
			public void onSuccess(final JavaScriptObject jsObject) {	       		
		   		// Turn the resulting JavaScriptObject into an AutoBean
		   		final JSONObject obj = new JSONObject(jsObject);
		   		final ListResultFactory factory = GWT.create(ListResultFactory.class);
		   		final AutoBean<ListResultOfSuggestion> autoBean = AutoBeanCodex.decode(factory, ListResultOfSuggestion.class, obj.toString());	       		
		   		final ListResultOfSuggestion listResult = autoBean.as();		   	
		   		
		   		getView().setResults(listResult.getResults());
		   	}
		
			@Override
			public void onFailure(final Throwable caught) {
				// TODO Do Something with failure				
			}
		});*/	
	}
}

interface SearchUiHandlers extends UiHandlers{
	void showResults(String searchTerm, String attribute);
}
