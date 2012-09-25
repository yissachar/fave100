package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.users.ListResultOfSuggestion;
import com.fave100.client.pages.users.SongSuggestBox.ListResultFactory;
import com.fave100.client.pages.users.SuggestionResult;
import com.fave100.client.place.NameTokens;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
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

	@Inject
	public SearchPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
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
		final String url = "http://itunes.apple.com/search?"+
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
		});	
		
		// TODO: Blend in with iTunes results
		// Amazon Product API		
		/*final String amazonAccessKey = "AKIAJISW3JUSF22DG5MQ";
		final String amazonSecretKey = "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";
		final String amazonAssociateTage = "fave100-20";
		final AWSECommerceService service = new AWSECommerceService();
		final AWSECommerceServicePortType port = service.getAWSECommerceServicePort();
		final ItemSearchRequest itemRequest = new ItemSearchRequest();
		
		// Set the request object
		itemRequest.setSearchIndex("Music");
		itemRequest.setKeywords(searchTerm);
		final ItemSearch itemElement = new ItemSearch();
		itemElement.setAWSAccessKeyId(amazonAccessKey)
		itemElement.getRequest().add(itemRequest);
		
		// Call the Web service operation and store the result
		final ItemSearchResponse response = port.itemSearch(marketplaceDomain, amazonAccessKey, amazonAssociateTage,
												xmlEscaping, validate, shared, request, operationRequest, items)*/
		
	}
}

interface SearchUiHandlers extends UiHandlers{
	void showResults(String searchTerm, String attribute);
}
