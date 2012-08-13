package com.fave100.client.pages.myfave100;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

/**
 * Creates suggestions for SongSuggestBox to display.
 * @author yissachar.radcliffe
 *
 */
public class MusicSuggestionOracle extends MultiWordSuggestOracle{
	
	@Override
	protected MultiWordSuggestion createSuggestion(String replacementString, String displayString) {
		return new MultiWordSuggestion(replacementString, displayString);
		// TODO: We can't seem to do all this fancy stuff without messing up the suggestion results
		// We still get some suggestion results but not as many as we should		
		// Split the replacement string into two parts, song title and artist name
		/*int imgEndPos = replacementString.indexOf(">")+1;
		int artistStartPos = replacementString.indexOf("</br><span class='artistName'>");		
		String songTitle = replacementString.substring(imgEndPos, artistStartPos);
		String img = replacementString.substring(0, imgEndPos);
		String artistName = replacementString.substring(artistStartPos, replacementString.length()); 
		// The new display string will consist of both the song title and the artist name
		//String newDisplayString = songTitle+replacementString.substring(tokenSplitPos, replacementString.length());
		String newDisplayString = img+songTitle+artistName;
		return new MultiWordSuggestion(songTitle, newDisplayString);*/
	}
	
}
