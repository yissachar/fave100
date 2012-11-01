package com.fave100.client.pages.users;

import java.util.HashMap;

import com.fave100.client.pages.search.MusicbrainzResult;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestBox;

/**
 * SuggestBox that pulls its suggestions from iTunes Search API.
 * @author yissachar.radcliffe
 *
 */
public class SongSuggestBox extends SuggestBox{ 
	
	private MusicSuggestionOracle suggestions;
	private HashMap<String, MusicbrainzResult> itemSuggestionMap;
	private Timer suggestionsTimer;
		
	public SongSuggestBox(final MusicSuggestionOracle suggestions, final ApplicationRequestFactory requestFactory) {		
		super(suggestions);
		this.suggestions = suggestions;
		itemSuggestionMap = new HashMap<String, MusicbrainzResult>();	
		
		suggestionsTimer = new Timer() {
			@Override
			public void run() {
				getAutocompleteList();
			}
		};
		
		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {	
				//To restrict amount of queries, don't bother searching unless more than 200ms have passed
				//since the last keystroke.		
				suggestionsTimer.cancel();
				// Don't search if it was just an arrow key being pressed
				if(!KeyCodeEvent.isArrow(event.getNativeKeyCode()) && event.getNativeKeyCode() != KeyCodes.KEY_ENTER) {
					// Min delay 200ms, Max 1500
					int delay = 200+(20*getText().length());					
					if(delay > 1500) delay = 1500;
					suggestionsTimer.schedule(delay);
				}
			}
		});
	}	

	private void getAutocompleteList() {
		// TODO: Needs to be hosted on actual server
		String url = "http://192.168.214.170:7080/";
		url += "?songName="+this.getValue();
		
		 final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		 jsonp.requestObject(url, new AsyncCallback<AutocompleteJSON>() {
			 
		    @Override
			public void onFailure(final Throwable throwable) {
		    	//Window.alert("Fail!");
		    }

		    @Override
			public void onSuccess(final AutocompleteJSON json) {
			
				// Clear the current suggestions)
	       		suggestions.clearSuggestions();
	       		itemSuggestionMap.clear();
	       		
	       		// Get the new suggestions from the iTunes API       		
	       		final JsArray<JSONResult> results = json.getEntries();
		        for (int i = 0; i < results.length(); i++) {
		        	final JSONResult jsonResult = results.get(i);
		        	final MusicbrainzResult entry = new MusicbrainzResult();
		        	entry.setTrackName(jsonResult.getTrackName());
		        	entry.setArtistName(jsonResult.getArtistName());
		        	entry.setMbid(jsonResult.getMbid());
		        	
	       			final String imageUrl = "";
	       			/*if(entry.getCoverArtUrl() != null) {
	       				imageUrl = UriUtils.sanitizeUri(entry.getCoverArtUrl());;
	       			}*/
	    	    	final String suggestionEntry = "<img src='"+imageUrl+"'/>"+
	    	    			entry.getTrackName()+"</br><span class='artistName'>"+entry.getArtistName()+"</span>";		    	    	
	    	    	String mapEntry = entry.getTrackName();
	    	    	// Use white space to sneak in duplicate song titles into the hashmap
	    	    	while(itemSuggestionMap.get(mapEntry) != null) {
	    	    		mapEntry += " ";
	    	    	}
	    	    	itemSuggestionMap.put(mapEntry, entry);
	    	    	// TODO: Safe HTML?
	    	    	suggestions.addSuggestion(mapEntry, suggestionEntry);
	    	    }
	    	    showSuggestionList();		         
		    }
		});
		// XHR to get info from Musicbrainz 
		/*String searchUrl = SearchPresenter.BASE_SEARCH_URL;
		searchUrl += "recording/?query="+this.getValue();
		searchUrl += "&limit=10";
		
		final XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("GET", searchUrl);
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler()	{
		    @Override
		    public void onReadyStateChange(final XMLHttpRequest xhr2) {
		         if( xhr2.getReadyState() == XMLHttpRequest.DONE ) {
		             xhr2.clearOnReadyStateChange();
		             final MusicbrainzResultList resultList = new MusicbrainzResultList(xhr2.getResponseText());

		             // Clear the current suggestions)
		       		suggestions.clearSuggestions();
		       		itemSuggestionMap.clear();
		       		
		       		// Get the new suggestions from the iTunes API       		
		       		for (final MusicbrainzResult entry : resultList) {
		       			String imageUrl = "";
		       			if(entry.getCoverArtUrl() != null) {
		       				imageUrl = UriUtils.sanitizeUri(entry.getCoverArtUrl());;
		       			}
		    	    	final String suggestionEntry = "<img src='"+imageUrl+"'/>"+
		    	    			entry.getTrackName()+"</br><span class='artistName'>"+entry.getArtistName()+"</span>";		    	    	
		    	    	String mapEntry = entry.getTrackName();
		    	    	// Use white space to sneak in duplicate song titles into the hashmap
		    	    	while(itemSuggestionMap.get(mapEntry) != null) {
		    	    		mapEntry += " ";
		    	    	}
		    	    	itemSuggestionMap.put(mapEntry, entry);
		    	    	// TODO: Safe HTML?
		    	    	suggestions.addSuggestion(mapEntry, suggestionEntry);
		    	    }
		    	    showSuggestionList();
		         }
		    }
		});
		xhr.send();*/
		// TODO: type "gr" then backspace twice and type "o", if "gr" request really long
		// e.g. 6 seconds and "o" request very short, e.g. 100 ms, autocomplete will show results for 
		// "gr" when it should really show for "o"
		// solution: store requests in array, whenever request completes, clear it and any
		// other requests with lower index		
	}
	
	// Returns MusicbrainzResults mapped from the display string passed in
	public MusicbrainzResult getFromSuggestionMap(final String key) {
		return itemSuggestionMap.get(key);
	}
}


/** Overlays for autocomplete JSON **/
class JSONResult extends JavaScriptObject {
   protected JSONResult() {}

   public final native String getTrackName() /*-{
     return this[0];
   }-*/;

   public final native String getArtistName() /*-{
     return this[1];
   }-*/;

   public final native String getMbid() /*-{
     return this[2];
   }-*/;

 }

 class AutocompleteJSON extends JavaScriptObject {
   protected AutocompleteJSON() {}

   public final native JsArray<JSONResult> getEntries() /*-{
     return this.results;
   }-*/;
 }