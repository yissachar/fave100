package com.fave100.client.pages.search;

import com.fave100.client.requestfactory.SongProxy;
import com.gwtplatform.mvp.client.UiHandlers;

public interface SearchUiHandlers extends UiHandlers {
	void showResults(String songTerm, String artistTerm);

	void addSong(SongProxy song);
}
