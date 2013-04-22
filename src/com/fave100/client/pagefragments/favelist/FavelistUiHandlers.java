package com.fave100.client.pagefragments.favelist;

import com.gwtplatform.mvp.client.UiHandlers;

public interface FavelistUiHandlers extends UiHandlers {
	void addSong(String songID);

	void removeSong(String songID, int index);

	void editWhyline(String songID, String whyline);

	void changeSongPosition(String songID, int currentIndex, int newIndex);
}
