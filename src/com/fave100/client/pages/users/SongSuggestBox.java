package com.fave100.client.pages.users;

import java.util.HashMap;
import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

/**
 * SuggestBox that pulls its suggestions from iTunes Search API.
 * @author yissachar.radcliffe
 *
 */
public class SongSuggestBox extends SuggestBox{

	private MusicSuggestionOracle suggestions;
	private HashMap<String, SongProxy> itemSuggestionMap;
	private Timer suggestionsTimer;
	private ApplicationRequestFactory requestFactory;

	public SongSuggestBox(final MusicSuggestionOracle suggestions, final ApplicationRequestFactory requestFactory) {
		super(suggestions);
		this.suggestions = suggestions;
		this.requestFactory = requestFactory;
		itemSuggestionMap = new HashMap<String, SongProxy>();

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
		final Request<List<SongProxy>> autocompleteReq = requestFactory.songRequest().getAutocomplete(this.getValue());
		autocompleteReq.fire(new Receiver<List<SongProxy>>() {
			@Override
			public void onSuccess(final List<SongProxy> results) {
				// Clear the current suggestions)
	       		suggestions.clearSuggestions();
	       		itemSuggestionMap.clear();

	       		// Get the new suggestions from the autocomplete API
	       		//final MusicbrainzResultList results = new MusicbrainzResultList(json);
		        for (int i = 0; i < results.size(); i++) {
		        	final SongProxy entry = results.get(i);

	       			final String imageUrl = "";
	       			/*if(entry.getCoverArtUrl() != null) {
	       				imageUrl = UriUtils.sanitizeUri(entry.getCoverArtUrl());;
	       			}*/
	    	    	final String suggestionEntry = "<img src='"+imageUrl+"'/>"+
	    	    			entry.getTitle()+"</br><span class='artistName'>"+entry.getArtist()+"</span>";
	    	    	String mapEntry = entry.getTitle();
	    	    	// Use white space to sneak in duplicate song titles into the hashmap
	    	    	while(itemSuggestionMap.get(mapEntry) != null) {
	    	    		mapEntry += " ";
	    	    	}
	    	    	itemSuggestionMap.put(mapEntry, entry);

	    	    	suggestions.addSuggestion(mapEntry, suggestionEntry);
	    	    }
	    	    showSuggestionList();
			}
		});
		// TODO: type "gr" then backspace twice and type "o", if "gr" request really long
		// e.g. 6 seconds and "o" request very short, e.g. 100 ms, autocomplete will show results for
		// "gr" when it should really show for "o"
		// solution: store requests in array, whenever request completes, clear it and any
		// other requests with lower index
	}

	// Returns MusicbrainzResults mapped from the display string passed in
	public SongProxy getFromSuggestionMap(final String key) {
		return itemSuggestionMap.get(key);
	}
}