package com.fave100.client.pages.search;

import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.myfave100.ListResultOfSuggestion;
import com.fave100.client.pages.myfave100.SongSuggestBox.ListResultFactory;
import com.fave100.client.pages.myfave100.SuggestionResult;
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

	@Override
	public void showResults(final String searchTerm) {
		final String url = "http://itunes.apple.com/search?"+
				"term="+searchTerm+
				"&media=music"+
				"&entity=song"+
				"&attribute=songTerm"+
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
		   		
		   		/*String output = "<ul>";	
		   		for (final SuggestionResult entry : listResult.getResults()) {
		   			output += "<li>";
		   			
		   			final Image imageThumb = new Image();
		   			imageThumb.setUrl(entry.getArtworkUrl60());
		   			output += imageThumb.toString();
		   			
		   			final Label trackName = new Label();
		   			trackName.setText(entry.getTrackName());
		   			trackName.addStyleName("advancedSearchTrackName");
		   			output += trackName.toString();
		   			
		   			final Label releaseDate = new Label();
		   			releaseDate.setText(entry.getReleaseDate().substring(0, 4));
		   			releaseDate.addStyleName("advancedSearchReleaseDate");
		   			output += releaseDate.toString();		   			
		   			
		   			final Label artistName = new Label();
		   			artistName.setText(entry.getArtistName());
		   			artistName.addStyleName("advancedSearchArtistName");
		   			output += artistName.toString();
		   						    	
			    	output += "</li>";
			    }
		   		output += "</ul>";
		   		getView().setResults(output);*/
		   		getView().setResults(listResult.getResults());
		   	}
		
			@Override
			public void onFailure(final Throwable caught) {
				// TODO Do Something with failure				
			}
		});	
	}
}

interface SearchUiHandlers extends UiHandlers{
	void showResults(String searchTerm);
}
