package com.fave100.client.widgets.search;

import com.gwtplatform.mvp.client.UiHandlers;

interface SearchUiHandlers extends UiHandlers {
	void getSearchResults(String searchTerm);

	void clearSearchResults();

	int getSelection();

	void setSelection(int position);

	int getMaxSelection();

	void deselect();

	void incrementSelection();

	void decrementSelection();

	void loadMore();

	void setSearchType(SearchType searchType);

	void selectSuggestion();

	int getTotalResults();
}
