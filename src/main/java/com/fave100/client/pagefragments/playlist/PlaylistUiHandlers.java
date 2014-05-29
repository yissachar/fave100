package com.fave100.client.pagefragments.playlist;

import com.gwtplatform.mvp.client.UiHandlers;

public interface PlaylistUiHandlers extends UiHandlers {

	void previousSong();

	void nextSong();

	void skipVideo();

	void addSong();
}
