package com.fave100.client.pages.users;

import java.util.HashMap;

import com.fave100.client.pages.search.MusicbrainzResult;
import com.fave100.client.pages.search.MusicbrainzResultList;
import com.fave100.client.pages.search.SearchPresenter;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

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
		this.setLimit(4);
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
					suggestionsTimer.schedule(200);
				}
			}
		});
	}	

	private void getAutocompleteList() {	
		// XHR to get info from Musicbrainz
		String searchUrl = SearchPresenter.BASE_SEARCH_URL;
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
		xhr.send();
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