package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.users.ListResultOfSuggestion;
import com.fave100.client.pages.users.SuggestionResult;
import com.fave100.client.pages.users.SongSuggestBox.ListResultFactory;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
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
		void setResults(List<SuggestionResult> resultList);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.search)
	public interface MyProxy extends ProxyPlace<SearchPresenter> {
	}
	
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
		String searchUrl = "http://musicbrainz.org/ws/2/";
		if(attribute.equals("songTerm")) {
			searchUrl += "release/?query=release:";
		}
		searchUrl += searchTerm;
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("GET", searchUrl);
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler()	{
		    @Override
		    public void onReadyStateChange(XMLHttpRequest xhr2) {
		         if( xhr2.getReadyState() == XMLHttpRequest.DONE ) {
		             xhr2.clearOnReadyStateChange();
		             Window.alert(xhr2.getResponseText());
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
