package com.fave100.client.pages.myfave100;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

/**
 * Creates suggestions for SongSuggestBox to display.
 * @author yissachar.radcliffe
 *
 */
public class MusicSuggestionOracle extends MultiWordSuggestOracle{
	
	@Override
	protected MultiWordSuggestion createSuggestion(String replacementString,String displayString) {
		// Split the replacement string into two parts, song title and artist name
		int tokenSplitPos = replacementString.indexOf("<br/><span class='artistName'>");
		// The new replacement string will just be the song title
		String newReplacementString = replacementString.substring(0, tokenSplitPos);
		// The new display string will consist of both the song title and the artist name
		String newDisplayString = newReplacementString+replacementString.substring(tokenSplitPos, replacementString.length());
		return new MultiWordSuggestion(newReplacementString, newDisplayString);
	}
	
}
