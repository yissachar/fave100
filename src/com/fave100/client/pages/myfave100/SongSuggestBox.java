package com.fave100.client.pages.myfave100;

import java.util.HashMap;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.requestfactory.FaveItemRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class SongSuggestBox extends SuggestBox{
	
	private MusicSuggestionOracle suggestions;
	private HashMap<String, FaveItemProxy> itemSuggestionMap;
	private Timer suggestionsTimer;
	private ApplicationRequestFactory requestFactory;
	@Inject EventBus eventBus;
		
	public SongSuggestBox(MusicSuggestionOracle suggestions) {
		super(suggestions);
		this.suggestions = suggestions;
		this.setLimit(4);
		itemSuggestionMap = new HashMap<String, FaveItemProxy>();	
		
		//TODO: inject	
		requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(eventBus);
		
		suggestionsTimer = new Timer() {
			public void run() {
				getAutocompleteList();
			}
		};
		
		addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {	
				//To restrict amount of queries, don't bother searching unless more than 200ms have passed
				//since the last keystroke.		
				suggestionsTimer.cancel();
				// don't search if it was just an arrow key being pressed
				if(!KeyCodeEvent.isArrow(event.getNativeKeyCode()) && event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
					suggestionsTimer.schedule(200);
				}
			}
		});
	}

	private void getAutocompleteList() {		
		String url = "http://itunes.apple.com/search?"+
						"term="+this.getValue()+
						"&media=music"+
						"&entity=song"+
						"&attribute=songTerm"+
						"&limit=5";
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, new AsyncCallback<JSResult>() {			
	       	public void onSuccess(JSResult result) {
	       		// Clear the current suggestions)	       		
	       		suggestions.clear();
	       		itemSuggestionMap.clear();
	       		
	       		// Get the new suggestions from the iTunes API
	    	    JsArray<JSEntry> entries = result.getResults();
	         
	    	    for (int i = 0; i < entries.length(); i++) {
	    	    	JSEntry entry = entries.get(i);
	    	    	String suggestionEntry = entry.trackName()+"<br/><span class='artistName'>"+entry.artistName()+"</span>";
	    	    	suggestions.add(suggestionEntry);
	    	    	
	    	    	FaveItemRequest faveRequest = requestFactory.faveItemRequest();	    	    	
	    	    	FaveItemProxy faveItem = faveRequest.create(FaveItemProxy.class);
	    	    	faveItem.setId(Long.parseLong(entry.id()));
	    	    	faveItem.setTitle(entry.trackName());
	    	    	faveItem.setArtist(entry.artistName());
	    	    	faveItem.setReleaseYear(Integer.parseInt(entry.releaseYear()));
	    	    	faveItem.setItemURL(entry.itemURL());
	    	    	itemSuggestionMap.put(suggestionEntry, faveItem);
	    	    }
	    	    showSuggestionList();
	       	}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}
		});		
	}
	
	public FaveItemProxy getFromSuggestionMap(String key) {
		return itemSuggestionMap.get(key);
	}
}

/**
*
* Classes to convert JSON return into Java parseable object.
*/
class JSEntry extends JavaScriptObject {
	protected JSEntry() {}
	
	public final native String id() /*-{
		return String(this.trackId);
	}-*/;
	
	public final native String itemURL() /*-{
		return this.trackViewUrl;
	}-*/;
	
	public final native String trackName() /*-{
    	return this.trackName;
  	}-*/;
  
	public final native String artistName() /*-{
	   return this.artistName;
	}-*/;
	
	public final native String releaseYear() /*-{
		return this.releaseDate.substring(0, 4);
	}-*/;
}

class JSResult extends JavaScriptObject {
  protected JSResult() {}

  public final native JsArray<JSEntry> getResults() /*-{
    return this.results;
  }-*/;
}
