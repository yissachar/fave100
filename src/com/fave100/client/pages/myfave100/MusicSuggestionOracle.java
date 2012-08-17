package com.fave100.client.pages.myfave100;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

/**
 * Creates suggestions for SongSuggestBox to display.
 * @author yissachar.radcliffe
 *
 */
public class MusicSuggestionOracle extends MultiWordSuggestOracle{
	
	private Map<String, String> suggestionList = new HashMap<String, String>();
	
	// TODO: Need to enforce saftey of display string here, otherwise XSS
	public void addSuggestion(String replacementString, String displayString) {
		suggestionList.put(replacementString, displayString);
		this.add(replacementString);
	}
	
	public void clearSuggestions() {
		suggestionList.clear();
		this.clear();
	}
	
	@Override
	protected MultiWordSuggestion createSuggestion(String replacementString, String displayString) {
		return new MultiWordSuggestion(replacementString, suggestionList.get(replacementString));		
	}
	
}
