package com.fave100.client.pages.search;

import com.fave100.shared.requestfactory.SongProxy;
import com.gwtplatform.mvp.client.UiHandlers;

public interface SearchUiHandlers extends UiHandlers {
	void showResults(String searchTerm);

	void addSong(SongProxy song);
}
