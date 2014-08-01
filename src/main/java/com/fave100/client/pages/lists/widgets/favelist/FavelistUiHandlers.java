package com.fave100.client.pages.lists.widgets.favelist;

import com.gwtplatform.mvp.client.UiHandlers;

public interface FavelistUiHandlers extends UiHandlers {
	void addSong(String songID, String song, String artist, boolean forceAddToCurrentList);

	void removeSong(String songID, int index);

	void editWhyline(String songID, String whyline);

	void changeSongPosition(String songID, int currentIndex, int newIndex);

	void playSong(String songId);
}
