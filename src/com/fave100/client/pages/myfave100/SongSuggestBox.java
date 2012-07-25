package com.fave100.client.pages.myfave100;

import java.util.HashMap;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.fave100.client.requestfactory.FaveItemRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class SongSuggestBox extends SuggestBox{
	
	private MusicSuggestionOracle suggestions;
	private HashMap<String, SuggestionResult> itemSuggestionMap;
	private Timer suggestionsTimer;
	private ApplicationRequestFactory requestFactory;
	@Inject EventBus eventBus;
		
	public SongSuggestBox(MusicSuggestionOracle suggestions) {
		super(suggestions);
		this.suggestions = suggestions;
		this.setLimit(4);
		itemSuggestionMap = new HashMap<String, SuggestionResult>();	
		
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
	
	public interface ListResultFactory extends AutoBeanFactory {		
		AutoBean<ListResultOfSuggestion> result();		
	}

	private void getAutocompleteList() {		
		String url = "http://itunes.apple.com/search?"+
						"term="+this.getValue()+
						"&media=music"+
						"&entity=song"+
						"&attribute=songTerm"+
						"&limit=5";
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(url, new AsyncCallback<JavaScriptObject>() {			
	       	public void onSuccess(JavaScriptObject jsObject) {	       		
	       		// Turn the resulting JavaScriptObject into an AutoBean
	       		JSONObject obj = new JSONObject(jsObject);
	       		ListResultFactory factory = GWT.create(ListResultFactory.class);
	       		AutoBean<ListResultOfSuggestion> autoBean = AutoBeanCodex.decode(factory, ListResultOfSuggestion.class, obj.toString());
	       		
	       		ListResultOfSuggestion listResult = autoBean.as();
	       		
	       		// Clear the current suggestions)	       		
	       		suggestions.clear();
	       		itemSuggestionMap.clear();
	       		
	       		// Get the new suggestions from the iTunes API
//	    	    JsArray<JSEntry> entries = result.getResults();	       		
	       		for (SuggestionResult entry : listResult.getResults()) {
	    	    	String suggestionEntry = entry.getTrackName()+"<br/><span class='artistName'>"+entry.getArtistName()+"</span>";	    	    	
//	    	    	SongRequest songRequest = requestFactory.songRequest();	    	    	
//	    	    	SongProxy song = songRequest.create(SongProxy.class);
//	    	    	song.setId(entry.getTrackId());
//	    	    	song.setTitle(entry.getTrackName());
//	    	    	song.setArtist(entry.artistName());
//	    	    	song.setReleaseYear(Integer.parseInt(entry.releaseYear()));
//	    	    	song.setItemURL(entry.itemURL());
	    	    	itemSuggestionMap.put(suggestionEntry, entry);
	    	    }
	       		suggestions.addAll(itemSuggestionMap.keySet());
	    	    showSuggestionList();
	    	    
	       	}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}
		});		
	}
	
	public SuggestionResult getFromSuggestionMap(String key) {
		return itemSuggestionMap.get(key);
	}
}