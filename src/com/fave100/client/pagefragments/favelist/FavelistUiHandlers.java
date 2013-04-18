package com.fave100.client.pagefragments.favelist;

import com.gwtplatform.mvp.client.UiHandlers;

public interface FavelistUiHandlers extends UiHandlers {
	void addSong(String songID);

	void removeSong(String songID);

	void editWhyline(String songID, String whyline);

	void changeSongPosition(String songID, int newIndex);

	void changeSongPosition(int currentIndex, int newIndex);
}
