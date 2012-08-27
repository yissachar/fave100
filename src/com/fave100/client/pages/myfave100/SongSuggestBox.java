package com.fave100.client.pages.myfave100;

import java.util.HashMap;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyCodeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * SuggestBox that pulls its suggestions from iTunes Search API.
 * @author yissachar.radcliffe
 *
 */
public class SongSuggestBox extends SuggestBox{
	
	private MusicSuggestionOracle suggestions;
	private HashMap<String, SuggestionResult> itemSuggestionMap;
	private Timer suggestionsTimer;
		
	public SongSuggestBox(final MusicSuggestionOracle suggestions, final ApplicationRequestFactory requestFactory) {
		// TODO: Need advanced search option (e.g. search by artist)
		super(suggestions);
		this.suggestions = suggestions;
		this.setLimit(4);
		itemSuggestionMap = new HashMap<String, SuggestionResult>();	
		
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
	
	public interface ListResultFactory extends AutoBeanFactory {		
		AutoBean<ListResultOfSuggestion> result();		
	}

	private void getAutocompleteList() {	
		// Build a JSONP request to grab the info from iTunes
		final String url = "http://itunes.apple.com/search?"+
						"term="+this.getValue()+
						"&media=music"+
						"&entity=song"+
						"&attribute=songTerm"+
						"&limit=5";
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		// TODO: type "gr" then backspace twice and type "o", if "gr" request really long
		// e.g. 6 seconds and "o" request very short, e.g. 100 ms, autocomplete will show results for 
		// "gr" when it should really show for "o"
		// solution: store requests in array, whenever request completes, clear it and any
		// other requests with lower index
		jsonp.requestObject(url, new AsyncCallback<JavaScriptObject>() {			
	       	@Override
			public void onSuccess(final JavaScriptObject jsObject) {	       		
	       		// Turn the resulting JavaScriptObject into an AutoBean
	       		final JSONObject obj = new JSONObject(jsObject);
	       		final ListResultFactory factory = GWT.create(ListResultFactory.class);
	       		final AutoBean<ListResultOfSuggestion> autoBean = AutoBeanCodex.decode(factory, ListResultOfSuggestion.class, obj.toString());	       		
	       		final ListResultOfSuggestion listResult = autoBean.as();
	       		
	       		// Clear the current suggestions)
	       		suggestions.clearSuggestions();
	       		itemSuggestionMap.clear();
	       		
	       		// Get the new suggestions from the iTunes API       		
	       		for (final SuggestionResult entry : listResult.getResults()) {
	    	    	final String suggestionEntry = "<img src='"+UriUtils.sanitizeUri(entry.getArtworkUrl60())+"'/>"+
	    	    			entry.getTrackName()+"</br><span class='artistName'>"+entry.getArtistName()+"</span>";		    	
	    	    	itemSuggestionMap.put(entry.getTrackName(), entry);
	    	    	// TODO: Safe HTML?
	    	    	suggestions.addSuggestion(entry.getTrackName(), suggestionEntry);
	    	    }
	       		// TODO: Duplicate suggestions are hidden! Song name "W" and "W" only one actually shown
	    	    showSuggestionList();
	       	}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Do Something with failure				
			}
		});		
	}
	
	// Returns SuggestionResults mapped from the display string passed in
	public SuggestionResult getFromSuggestionMap(final String key) {
		return itemSuggestionMap.get(key);
	}
}