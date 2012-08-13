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
		int imgPos = replacementString.indexOf("<img src='");
		int artistPos = replacementString.indexOf("<span class='artistName'>");		
		String songTitle = replacementString.substring(0, imgPos);
		String img = replacementString.substring(imgPos, artistPos);
		String artistName = replacementString.substring(artistPos, replacementString.length()); 
		// The new display string will consist of both the song title and the artist name
		//String newDisplayString = songTitle+replacementString.substring(tokenSplitPos, replacementString.length());
		String newDisplayString = img+songTitle+artistName;
		return new MultiWordSuggestion(songTitle, newDisplayString);
	}
	
}
