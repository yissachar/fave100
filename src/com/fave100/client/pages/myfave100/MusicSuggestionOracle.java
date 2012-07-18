package com.fave100.client.pages.myfave100;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class MusicSuggestionOracle extends MultiWordSuggestOracle{
	
	public void addSongSuggestion(String songTitle, String songArtist) {
		this.add(songTitle);
	}
	
	@Override
	protected MultiWordSuggestion createSuggestion(String replacementString,String displayString) {		
		int tokenSplitPos = replacementString.indexOf("<br/><span class='artistName'>");
		String newReplacementString = replacementString.substring(0, tokenSplitPos);
		String newDisplayString = newReplacementString+replacementString.substring(tokenSplitPos, replacementString.length());
		return new MultiWordSuggestion(newReplacementString, newDisplayString);
	}
	
}
