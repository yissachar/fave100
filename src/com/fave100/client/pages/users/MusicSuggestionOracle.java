package com.fave100.client.pages.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

/**
 * Creates suggestions for SongSuggestBox to display.
 * @author yissachar.radcliffe
 *
 */
public class MusicSuggestionOracle extends MultiWordSuggestOracle{
	
	private Map<String, String> suggestionMap = new HashMap<String, String>();
	
	// TODO: Need to enforce saftey of display string here, otherwise XSS
	public void addSuggestion(final String replacementString, final String displayString) {
		suggestionMap.put(replacementString, displayString);
		this.add(replacementString);
	}
	
	public void clearSuggestions() {
		suggestionMap.clear();
		this.clear();
	}
	
	@Override
	protected MultiWordSuggestion createSuggestion(final String replacementString, final String displayString) {
		return new MultiWordSuggestion(replacementString, suggestionMap.get(replacementString));		
	}
	
	// Override GWT SuggestBox and use all suggestions that were added
	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		final List<MultiWordSuggestion> suggestions = new ArrayList<MultiWordSuggestOracle.MultiWordSuggestion>();
		
		for(final String suggestionKey : suggestionMap.keySet()) {
			suggestions.add(new MultiWordSuggestion(suggestionKey, suggestionMap.get(suggestionKey)));
		}

	    final Response response = new Response(suggestions);

	    callback.onSuggestionsReady(request, response);
	}
	
}
